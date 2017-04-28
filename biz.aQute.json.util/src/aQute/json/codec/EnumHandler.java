package aQute.json.codec;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import aQute.json.codec.ObjectHandler.FDesc;
import aQute.lib.exceptions.Exceptions;

public class EnumHandler extends Handler {
	@SuppressWarnings("rawtypes")
	final Class<? extends Enum> type;
	final FDesc[] fields;

	static class FDesc implements Comparable<FDesc>{
		String	name;
		Object	instance;

		@Override
		public int compareTo(FDesc o) {
			return name.compareTo(o.name);
		}
	}

	@SuppressWarnings("unchecked")
	public EnumHandler(JSONCodec codec, Class<?> type) throws Exception {
		this.type = (Class<? extends Enum<?>>) type;
		fields = Stream.of(this.type.getFields())
				.filter(f -> Modifier.isStatic(f.getModifiers()))
				.filter(f -> type.isAssignableFrom(f.getType()))
				.map(f -> {
					try {
						FDesc r = new FDesc();
						r.instance = f.get(null);
						r.name = codec.renamer.apply(f);
						return r;
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw Exceptions.duck(e);
					}
				})
				.sorted()
				.toArray(FDesc[]::new);

	}

	@Override
	public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
		StringHandler.string(app, object.toString());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object decode(Decoder dec, String s) throws Exception {
		FDesc field = getField(s);
		if ( field == null) {
			return Enum.valueOf(type, s);
		}

		return field.instance;
	}

	private FDesc getField(String key) {
		int low = 0;
		int high = fields.length - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			FDesc midVal = fields[mid];

			int cmp = midVal.name.compareTo(key);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return midVal; // key found
		}
		return null; // key not found.
	}
}
