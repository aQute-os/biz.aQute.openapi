package aQute.json.codec;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class OptionalHandler extends Handler {
	final Type type;

	OptionalHandler(Type type) {
		this.type = type;
	}

	@Override
	public void encode(Encoder app, Object object, Map<Object, Type> visited) throws Exception {
		Optional<?> optional = (Optional<?>) object;
		if (optional == null || !optional.isPresent()) {
			object = null;
		} else
			object = optional.get();

		app.encode(object, type, visited);
	}

	@Override
	public Object decode(Decoder dec) throws Exception {
		Object decode = dec.decode(type);
		return Optional.ofNullable(decode);
	}

	public Object decode(Decoder dec, Object o) {
		return Optional.ofNullable(o);
	}

	public Object decodeObject(Decoder isr) throws Exception {
		return decode(isr);
	}

	public Object decodeArray(Decoder isr) throws Exception {
		return decode(isr);
	}

	public Object decode(Decoder dec, String s) throws Exception {
		return Optional.ofNullable(s);
	}

	public Object decode(Decoder dec, Number s) throws Exception {
		return Optional.ofNullable(s);
	}

	public Object decode(Decoder dec, boolean s) {
		return Optional.ofNullable(s);
	}
}
