package aQute.openapi.provider.resources;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import org.osgi.service.component.annotations.Component;

import aQute.lib.exceptions.Exceptions;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.libg.glob.Glob;

/**
 * Maintains a set of resources that can come from different places. It can
 * provide caching information about those resources as well as compress them.
 */
@Component(service = ResourceDomain.class)
public class ResourceDomain {

	final Map<String,ResourceEntry>	entries	= new ConcurrentHashMap<>();
	final List<ResourceHolder>		holders	= new CopyOnWriteArrayList<>();

	/**
	 * Get a resource from one of the bundles registered as resource providers
	 * 
	 * @param path The web path of the resources
	 * @param etag the etag or null
	 * @param modifiedSince the last modified date seen or <= 0
	 * @param out the stream to copy the resource to
	 * @param compressions the list of compression algorithms. Supported: gzip,
	 *            deflate (case insensitive)
	 * @return a resource
	 */
	Result getResource(String path, BiFunction<String,Long,Boolean> doConditionally, OutputStream out,
			String... compressions) throws Exception {
		ResourceEntry entry = entries.computeIfAbsent(path, ResourceEntry::new);
		return entry.get(doConditionally, out, compressions);
	}

	enum ResultCode {
		UNKNOWN, UNMODIFIED, COPIED
	}

	static class Result {
		ResultCode		action	= ResultCode.COPIED;
		ResourceState	details;
		String			chosenCompressionAlgorithm;
	}

	static class ResourceState {
		final ResourceHolder	holder;
		final boolean			compress;
		final String			etag;
		final URL				url;
		final boolean			uncached;
		final int				length;
		final int				version;
		final int				maxAge;

		ResourceState(ResourceHolder holder, boolean compress, String etag, URL url, boolean uncached, int length,
				int version, int maxAge) {
			this.holder = holder;
			this.compress = compress;
			this.etag = etag;
			this.url = url;
			this.uncached = uncached;
			this.length = length;
			this.version = version;
			this.maxAge = maxAge;
		}
	}

	static class Found {
		ResourceHolder	holder;
		URL				url;
	}

	static class CounterStream extends FilterInputStream {

		int length;

		CounterStream(InputStream in) {
			super(in);
		}

		@Override
		public int read() throws IOException {
			length++;
			return super.read();
		}

		@Override
		public int read(byte[] data, int off, int len) throws IOException {
			int read = super.read(data, off, len);
			if (read > 0) {
				length += read;
			}
			return read;
		}
	}

	class ResourceEntry {
		final String	path;
		ResourceState	state;

		ResourceEntry(String path) {
			this.path = path;
		}

		@SuppressWarnings("resource")
		synchronized Result get(BiFunction<String,Long,Boolean> doConditionally, OutputStream out,
				String... compressions) throws IOException, NoSuchAlgorithmException {

			Result result = new Result();

			boolean stale = state == null;

			if (stale) {

				Found found = find(path);

				if (found == null) {
					result.action = ResultCode.UNKNOWN;
					return result;
				}

				if (doConditionally != null && !doConditionally.apply(null, found.holder.modified)) {
					result.action = ResultCode.UNMODIFIED;
				} else
					result.action = ResultCode.COPIED;

				InputStream in;
				try {
					in = found.url.openStream();
				} catch (FileNotFoundException e) {
					result.action = ResultCode.UNKNOWN;
					return result;
				}

				MessageDigest digester = MessageDigest.getInstance("SHA-1");
				in = new DigestInputStream(in, digester);
				CounterStream cin = new CounterStream(in);
				in = cin;

				boolean compress = matches(path, found.holder.compress);

				if (out != null && result.action == ResultCode.COPIED) {

					if (compress)
						out = doCompression(out, result, compressions);

					IO.copy(in, out);
				} else
					IO.copy(in, IO.nullStream);

				this.state = new ResourceState(found.holder, compress, toEtag(digester), found.url,
						matches(path, found.holder.uncached), cin.length, found.holder.version, found.holder.maxAge);
			} else {

				assert this.state != null;

				if (doConditionally != null && !doConditionally.apply(this.state.etag, this.state.holder.modified)) {
					result.action = ResultCode.UNMODIFIED;
				} else {

					if (out != null) {
						InputStream in = state.url.openStream();
						if (this.state.compress)
							out = doCompression(out, result, compressions);
						IO.copy(in, out);
					}
				}
			}
			result.details = this.state;
			return result;
		}

		/*
		 * Include the double quotes for simplicity. These quotes are required
		 * by the header. Normally we would have the raw value but this is less
		 * work.
		 */
		String toEtag(MessageDigest digester) {
			return "\"" + Hex.toHexString(digester.digest()) + "\"";
		}

		OutputStream doCompression(OutputStream out, Result result, String... compressions) throws IOException {
			for (String compression : compressions) {
				switch (compression.toLowerCase()) {
					case "gzip" :
						try {
							result.chosenCompressionAlgorithm = compression;
							return new GZIPOutputStream(out);
						} catch (IOException e) {
							throw Exceptions.duck(e);
						}

					case "deflate" :
						result.chosenCompressionAlgorithm = compression;
						return new DeflaterOutputStream(out);
				}
			}
			return out;
		}

		private Found find(String path) {
			for (ResourceHolder holder : holders) {
				synchronized (holder) {
					URL url = holder.get.apply(holder.base + path);
					if (url != null) {
						Found found = new Found();
						found.url = url;
						found.holder = holder;
						return found;
					}
				}
			}
			return null;
		}

		private boolean matches(String path, Glob... globs) {
			return Stream.of(globs).anyMatch(g -> g.matcher(path).matches());
		}
	}

	class ResourceHolder {
		final Function<String,URL>	get;
		Glob[]						compress;
		Glob[]						uncached;
		String						base;
		long						modified;
		volatile int				version;
		int							maxAge;

		ResourceHolder(Function<String,URL> get, long modified, int maxAge, String base, String[] compressed,
				String[] uncached) {
			this.get = get;
			this.maxAge = maxAge;
			this.modified = modified;
			this.version++;

			if (!base.startsWith("/"))
				base = "/" + base;

			if (!base.endsWith("/"))
				base = base + "/";
			this.base = base;
			this.modified = modified;
			this.compress = toGlob(compressed);
			this.uncached = toGlob(uncached);
		}

		protected Glob[] toGlob(String... globs) {
			return Stream.of(globs).map(Glob::new).toArray(Glob[]::new);
		}

		public void delete() {
			holders.remove(this);
			for (ResourceEntry e : entries.values()) {
				synchronized (e) {
					if (e.state.holder == this) {
						e.state = null;
					}
				}
			}
		}
	}

	ResourceHolder createHolder(Function<String,URL> get, long modified, int maxAge, String base, String[] compressed,
			String[] uncached) {
		ResourceHolder holder = new ResourceHolder(get, modified, maxAge, base, compressed, uncached);
		holders.add(holder);
		return holder;
	}

}
