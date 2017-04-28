package aQute.openapi.provider;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

import aQute.json.codec.JSONCodec;
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

	@Override
	public String getContentType() {
		return "application/json";
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, X> List<T> decodeList(Class<T> type, InputStream in, String mime, Function<Class<X>,X> instantiator)
			throws Exception {
		ParameterizedType pt = new ParameterizedType() {

			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] {
						type
				};
			}

			@Override
			public Type getRawType() {
				return List.class;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}

		};
		return (List<T>) codec.dec().instantiator(instantiator).from(in).get(pt);
	}

}
