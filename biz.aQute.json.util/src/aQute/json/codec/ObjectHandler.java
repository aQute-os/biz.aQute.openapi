package aQute.json.codec;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ObjectHandler extends Handler {
	@SuppressWarnings("rawtypes")
	final Class	rawClass;
	final FDesc	fields[];
	final FDesc	extra;

	static class FDesc implements Comparable<FDesc> {
		boolean	optional;
		String	name;
		Field	field;
		Type	type;
		Object	deflt;
		boolean	skip;

		@Override
		public int compareTo(FDesc o) {
			return name.compareTo(o.name);
		}
	}

	ObjectHandler(JSONCodec codec, Class<?> c) throws Exception {
		rawClass = c;
		List<FDesc> fields = new ArrayList<>();

		FDesc x = null;

		Object template;

		try {
			template = c.newInstance();
		} catch (Exception e1) {
			template = null;
		}

		for (Field f : c.getFields()) {
			if (Modifier.isStatic(f.getModifiers()))
				continue;

			FDesc fdesc = new FDesc();
			fdesc.field = f;
			fdesc.type = f.getGenericType();
			fdesc.name = codec.renamer.apply(f);
			fdesc.skip = f.getName().startsWith("__");

			if (f.getType() == Optional.class) {
				ParameterizedType ptype = (ParameterizedType) fdesc.type;
				fdesc.type = ptype.getActualTypeArguments()[0];
				fdesc.optional = true;
			}

			if (f.getName().equals("__extra"))
				x = fdesc;

			if (template != null) {
				try {
					fdesc.deflt = fdesc.field.get(template);
				} catch (Exception e) {
					// Ignore
				}
			}

			fields.add(fdesc);
		}

		if (x != null && Map.class.isAssignableFrom(x.field.getType()))
			extra = x;
		else
			extra = null;

		Collections.sort(fields);

		this.fields = fields.toArray(new FDesc[fields.size()]);
	}

	@Override
	public void encode(Encoder app, Object object, Map<Object, Type> visited) throws Exception {
		app.append("{");
		app.indent();
		String del = "";
		for (FDesc f : fields) {
			if (f.skip)
				continue;

			try {

				Object value = f.field.get(object);

				if (f.optional) {
					Optional<?> opt = (Optional<?>) value;
					if (opt == null || !opt.isPresent())
						continue;

					value = opt.get();
				}

				if (value == null)
					continue;

				if (!app.writeDefaults) {
					if (value == f.deflt)
						continue;

					if (value.equals(f.deflt))
						continue;
				}

				app.append(del);
				StringHandler.string(app, f.name);
				app.append(":");
				app.encode(value, f.type, visited);
				del = ",";
			} catch (Exception e) {
				throw new IllegalArgumentException(f.field + ":", e);
			}
		}
		app.undent();
		app.append("}");
	}

	@Override
	public Object decodeObject(Decoder r) throws Exception {
		assert r.current() == '{';
		Object targetObject = r.instantiate(rawClass);

		int c = r.next();
		while (JSONCodec.START_CHARACTERS.indexOf(c) >= 0) {

			// Get key
			String key = r.codec.parseString(r);

			// Get separator
			c = r.skipWs();
			if (c != ':')
				throw new IllegalArgumentException("Expected ':' but got " + (char) c);

			c = r.next();

			// Get value

			FDesc f = getField(key);
			if (f != null) {

				Object value = r.codec.decode(f.type, r);

				if (value != null || !r.codec.ignorenull) {
					if (Modifier.isFinal(f.field.getModifiers()))
						throw new IllegalArgumentException("Field " + f + " is final");

					if (f.optional) {
						value = Optional.ofNullable(value);
					}
					f.field.set(targetObject, value);
				}
			} else {
				if (r.isLog()) {
					try {
						Field ff = rawClass.getDeclaredField(key);
						r.log("Field %s accessed but is not public", ff);
					} catch (Exception e) {
						// ignore
					}
				}
				// No field, but may extra is defined
				if (extra == null) {
					if (r.strict)
						throw new IllegalArgumentException("No such field " + key);
					Object value = r.codec.decode(null, r);
					r.getExtra().put(rawClass.getName() + "." + key, value);
				} else {

					@SuppressWarnings("unchecked")
					Map<String, Object> map = (Map<String, Object>) extra.field.get(targetObject);
					if (map == null) {
						map = new LinkedHashMap<String, Object>();
						extra.field.set(targetObject, map);
					}
					Object value = r.codec.decode(null, r);
					map.put(key, value);
				}
			}

			c = r.skipWs();

			if (c == '}')
				break;

			if (c == ',') {
				c = r.next();
				continue;
			}

			throw new IllegalArgumentException(
					"Invalid character in parsing object, expected } or , but found " + (char) c);
		}
		assert r.current() == '}';
		r.read(); // skip closing
		return targetObject;
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
