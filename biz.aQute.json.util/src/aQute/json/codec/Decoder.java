package aQute.json.codec;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.InflaterInputStream;

public class Decoder implements Closeable {
	final JSONCodec			codec;

	Reader					reader;
	int						current;
	MessageDigest			digest;
	Map<String, Object>		extra;
	String					encoding	= "UTF-8";

	boolean					strict		= false;

	boolean					inflate;
	boolean					keepOpen	= false;
	private boolean			resolve;
	Function<Class<?>, ?>	instantiator;
	boolean					log;
	String					id;

	Decoder(JSONCodec codec) {
		this.codec = codec;
		this.log = codec.log;
	}

	public Decoder from(File file) throws Exception {
		return from(new FileInputStream(file)).id(file.getName());
	}

	public Decoder from(InputStream in) throws Exception {

		if (inflate)
			in = new InflaterInputStream(in);

		return from(new InputStreamReader(in, encoding));
	}

	public Decoder from(byte[] data) throws Exception {
		return from(new ByteArrayInputStream(data));
	}

	public Decoder charset(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public Decoder strict() {
		this.strict = true;
		return this;
	}

	public Decoder from(Reader in) throws Exception {
		reader = in;
		read();
		return this;
	}

	public Decoder faq(String in) throws Exception {
		return from(in.replace('\'', '"'));
	}

	public Decoder from(String in) throws Exception {
		return from(new StringReader(in));
	}

	public Decoder mark() throws NoSuchAlgorithmException {
		if (digest == null)
			digest = MessageDigest.getInstance("SHA1");
		digest.reset();
		return this;
	}

	public byte[] digest() {
		if (digest == null)
			return null;

		return digest.digest();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) throws Exception {
		try {
			return (T) decode(clazz);
		} finally {
			if (!keepOpen)
				close();
		}
	}

	protected Object decode(Type clazz) throws Exception {
		Object decoded = codec.decode(clazz, this);
		if (resolve) {
			ReferenceHandler handler = new ReferenceHandler(decoded, null);
			handler.resolve(decoded);
		}
		return decoded;
	}

	public Object get(Type type) throws Exception {
		try {
			return decode(type);
		} finally {
			if (!keepOpen)
				close();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(TypeReference<T> type) throws Exception {
		return (T) decode(type.getType());
	}

	public Object get() throws Exception {
		try {
			return decode(null);
		} finally {
			if (!keepOpen)
				close();
		}
	}

	public Decoder keepOpen() {
		keepOpen = true;
		return this;
	}

	int read() throws Exception {
		current = reader.read();
		if (digest != null) {
			digest.update((byte) (current / 256));
			digest.update((byte) (current % 256));
		}
		return current;
	}

	int current() {
		return current;
	}

	/**
	 * Skip any whitespace.
	 *
	 * @throws Exception
	 */
	int skipWs() throws Exception {
		while (Character.isWhitespace(current()))
			read();
		return current();
	}

	/**
	 * Skip any whitespace.
	 *
	 * @throws Exception
	 */
	int next() throws Exception {
		read();
		return skipWs();
	}

	void expect(String s) throws Exception {
		for (int i = 0; i < s.length(); i++)
			if (!(s.charAt(i) == read()))
				throw new IllegalArgumentException("Expected " + s + " but got something different");
		read();
	}

	public boolean isEof() throws Exception {
		int c = skipWs();
		return c < 0;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	public Map<String, Object> getExtra() {
		if (extra == null)
			extra = new HashMap<String, Object>();
		return extra;
	}

	public Decoder inflate() {
		if (reader != null)
			throw new IllegalStateException("Reader already set, inflate must come before from()");
		inflate = true;
		return this;
	}

	public Decoder resolve() {
		this.resolve = true;
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Decoder instantiator(Function<Class<T>, T> instantiator) {
		this.instantiator = (Function) instantiator;
		return this;
	}

	public Object instantiate(Class<?> rawClass) throws Exception {
		if (instantiator == null)
			return rawClass.newInstance();
		else
			return instantiator.apply(rawClass);
	}

	public boolean isLog() {
		return log;
	}

	public Decoder log(boolean on) {
		this.log = on;
		return this;
	}

	public void log(String format, Object... args) {
		if (isLog()) {
			JSONCodec.log(format, args);
		}
	}

	public double checkInvalidRange(double v, Type type, double min, double max) {
		if (v < min) {
			String msg = String.format("%s too small for %s, must be in [%s,%s] for %s", v, type, min, max, this);
			log(msg);
			if (strict) {
				throw new IllegalArgumentException(msg);
			}
		} else if (v > max) {
			String msg = String.format("%s too large for %s, must be in [%s,%s] for %s", v, type, min, max, this);
			log(msg);
			if (strict) {
				throw new IllegalArgumentException(msg);
			}
		}
		return v;
	}

	public long checkInvalidRange(long v, Type type, double min, double max) {
		if (v < min || v > max) {
			checkInvalidRange((double) v, type, min, max);
		}
		return v;
	}

	public Decoder id(String id) {
		this.id = id;
		return this;
	}
}
