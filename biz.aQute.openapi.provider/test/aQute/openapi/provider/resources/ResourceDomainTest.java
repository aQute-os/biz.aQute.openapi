
package aQute.openapi.provider.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;

import aQute.lib.exceptions.Exceptions;
import aQute.lib.io.IO;
import aQute.openapi.provider.resources.ResourceDomain.ResourceHolder;
import aQute.openapi.provider.resources.ResourceDomain.Result;

/**
 * Maintains a set of resources that can come from different places. It can
 * provide caching information about those resources as well as compress them.
 */
public class ResourceDomainTest {
	List<String>			paths	= new ArrayList<>();

	Function<String,URL>	get		= (path) -> {
										try {
											paths.add(path);
											File f = IO.getFile("resources" + path);
											if (f.isFile())
												return f.toURI().toURL();
											else
												return null;
										} catch (MalformedURLException e) {
											throw Exceptions.duck(e);
										}
									};
	ResourceDomain			domain	= new ResourceDomain();

	@Test
	public void testSimple() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[0]);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Result resource = domain.getResource("foo.txt", null, bout, new String[0]);

		assertNotNull(resource);
		assertNotNull(resource.details);
		assertNull(resource.chosenCompressionAlgorithm);
		assertFalse(resource.details.compress);
		assertFalse(resource.details.uncached);

		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);
		assertEquals(3, resource.details.length);
		assertEquals("\"0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33\"", resource.details.etag);
		assertEquals(1000, resource.details.holder.modified);

		assertEquals("foo", new String(bout.toByteArray(), "UTF-8"));
	}

	@Test
	public void testCompressed() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[] {
				"*.txt"
		}, new String[0]);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		Result resource = domain.getResource("foo.txt", null, bout, new String[] {
				"BZIP", "GZIP", "DEFLATE"
		});

		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);
		assertTrue(resource.details.compress);
		assertFalse(resource.details.uncached);
		assertEquals("GZIP", resource.chosenCompressionAlgorithm);
		assertNotEquals("foo", new String(bout.toByteArray(), "UTF-8"));
		assertEquals(3, resource.details.length);
		assertEquals("\"0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33\"", resource.details.etag);
	}

	@Test
	public void testNotCompressed() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[] {
				"*.txt"
		}, new String[0]);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		Result resource = domain.getResource("foo.not", null, bout, new String[] {
				"BZIP", "GZIP", "DEFLATE"
		});

		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);
		assertFalse(resource.details.compress);
		assertFalse(resource.details.uncached);
		assertNull(resource.chosenCompressionAlgorithm);
		assertEquals("foo", new String(bout.toByteArray(), "UTF-8"));
		assertEquals(3, resource.details.length);
		assertEquals("\"0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33\"", resource.details.etag);

		bout = new ByteArrayOutputStream();
		resource = domain.getResource("foo.not", null, bout, new String[] {
				"BZIP", "GZIP", "DEFLATE"
		});

		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);
		assertFalse(resource.details.compress);
		assertFalse(resource.details.uncached);
		assertNull(resource.chosenCompressionAlgorithm);
		assertEquals("foo", new String(bout.toByteArray(), "UTF-8"));
		assertEquals(3, resource.details.length);
		assertEquals("\"0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33\"", resource.details.etag);
	}

	@Test
	public void testUncached() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[] {
				"*.not"
		});

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		Result resource = domain.getResource("foo.not", null, bout, new String[0]);

		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);
		assertTrue(resource.details.uncached);
	}

	@Test
	public void testNotUncached() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[] {
				"*.not"
		});

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		Result resource = domain.getResource("foo.txt", null, bout, new String[0]);

		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);
		assertFalse(resource.details.uncached);
	}

	@Test
	public void testUnknown() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[0]);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		Result resource = domain.getResource("foo.non-existent", null, bout, new String[0]);

		assertEquals(ResourceDomain.ResultCode.UNKNOWN, resource.action);
		assertNull(resource.details);

		resource = domain.getResource("foo.non-existent", null, bout, new String[0]);

		assertEquals(ResourceDomain.ResultCode.UNKNOWN, resource.action);
		assertNull(resource.details);
	}

	@Test
	public void testEtagMatches() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[0]);

		BiFunction<String,Long,Boolean> doConditionally = (tag, modified) -> {
			return tag == null || !"\"0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33\"".equals(tag);
		};
		Result resource = domain.getResource("foo.txt", doConditionally, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);

		resource = domain.getResource("foo.txt", doConditionally, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.UNMODIFIED, resource.action);

	}

	@Test
	public void testEtagNotMatches() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[0]);

		BiFunction<String,Long,Boolean> doConditionally = (tag, modified) -> {
			return tag == null || "\"0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33\"".equals(tag);
		};
		Result resource = domain.getResource("foo.txt", doConditionally, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);

		resource = domain.getResource("foo.txt", doConditionally, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);

	}

	@Test
	public void testNewerResourceAvailble() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[0]);

		BiFunction<String,Long,Boolean> doConditionally = (tag, modified) -> {
			return modified >= 1000;
		};
		Result resource = domain.getResource("foo.txt", doConditionally, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);

		resource = domain.getResource("foo.txt", doConditionally, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);

	}

	@Test
	public void testNewerResourceNotAvailble() throws Exception {
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[0]);

		BiFunction<String,Long,Boolean> doConditionally = (tag, modified) -> {
			return modified < 1000;
		};
		Result resource = domain.getResource("foo.txt", doConditionally, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.UNMODIFIED, resource.action);
		assertEquals(3, resource.details.length);
		assertEquals("\"0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33\"", resource.details.etag);

		resource = domain.getResource("foo.txt", doConditionally, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.UNMODIFIED, resource.action);
		assertEquals(3, resource.details.length);
		assertEquals("\"0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33\"", resource.details.etag);
	}

	@Test
	public void removeHolderWhileWorking() throws Exception {
		Function<String,URL> get = (path) -> {
			try {
				File f = IO.getFile("resources" + path);
				if (f.isFile())
					return f.toURI().toURL();
				else
					return null;
			} catch (MalformedURLException e) {
				throw Exceptions.duck(e);
			}
		};
		ResourceHolder holder = domain.createHolder(get, 1000, 100, "resourcedomain", new String[0], new String[0]);
		Result resource = domain.getResource("foo.txt", null, null, new String[0]);

		assertNotNull(resource);
		assertEquals(ResourceDomain.ResultCode.COPIED, resource.action);
		assertNotNull(resource.details);
		holder.delete();
		resource = domain.getResource("foo.txt", null, null, new String[0]);
		assertEquals(ResourceDomain.ResultCode.UNKNOWN, resource.action);

	}

}
