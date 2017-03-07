package aQute.openapi.provider;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import aQute.json.util.JSONCodec;
import aQute.openapi.provider.OpenAPIBase.Codec;

public class CodecWrapper implements Codec {

	final JSONCodec codec;

	public CodecWrapper(JSONCodec codec) {
		this.codec = codec;
	}

	public CodecWrapper() {
		this.codec = new JSONCodec();
	}

	@Override
	public String encode(Object object, OutputStream out) throws Exception {
		codec.enc().to(out).put(object);
		return "application/json;charset=utf-8";
	}

	@Override
	public <T, X> T decode(Class<T> type, InputStream in, String mime, Function<Class<X>,X> instantiator)
			throws Exception {

		if (!isJson(mime))
			return null;

		return codec.dec().instantiator(instantiator).from(in).get(type);
	}

	private boolean isJson(String mime) {
		return mime == null || mime.startsWith("application/json");
	}

	@Override
	public <T> void addStringHandler(Class<T> type, Function<T,String> toString, Function<String,T> fromString) {
		codec.addStringHandler(type, toString, fromString);
	}

}
