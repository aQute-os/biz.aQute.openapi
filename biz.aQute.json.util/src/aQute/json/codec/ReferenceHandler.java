package aQute.json.codec;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.json.codec.ObjectVisitor.Visitor;
import aQute.lib.converter.Converter;

public class ReferenceHandler {
	final static Logger			log			= LoggerFactory
			.getLogger(ReferenceHandler.class);
	final Map<String, Object>	roots		= new HashMap<String, Object>();
	final Map<String, Object>	paths		= new HashMap<>();
	final Map<String, Object>	resolved	= new HashMap<>();
	final static Pattern		REF_P		= Pattern
			.compile("(?<root>[^#]*)#/(?<sub>.*)");

	public ReferenceHandler(Object root, Map<String, Object> roots) {
		if (roots != null)
			this.roots.putAll(roots);
		this.roots.put(null, root);
	}

	public Object locate(String path) {
		Matcher m = REF_P.matcher(path);
		if (!m.matches())
			throw new IllegalArgumentException("Invalid path " + path);

		Object replacement = paths.get(path);
		if (replacement != null)
			return replacement;

		String root = m.group("root");
		String sub = m.group("sub");

		if (root.isEmpty())
			replacement = get(null, sub);
		else
			replacement = get(root, sub);

		if (replacement == null)
			throw new RuntimeException("Ref to " + path + " not found");

		paths.put(path, replacement);

		//
		// Handle references to references ...
		//

		String recursive = getPath(replacement);
		if (recursive != null)
			return locate(recursive);

		if (replacement instanceof Map) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Map<String, String> map = (Map) replacement;
			if (map.size() == 1 && map.containsKey("$ref"))
				return locate(map.get("$ref"));
		}
		return replacement;
	}

	public void resolve(Object resolve) {
		ObjectVisitor.visit(new Visitor() {

			@Override
			public Object visit(Object object) {
				if (object == resolve)
					return object;

				String path = getPath(object);
				if (path == null)
					return object;

				//
				// object == DTO since it has a $ref field
				//

				if (resolved.containsKey(path))
					return resolved.get(path);

				//
				// We use the first object we find
				// as the anchor. Other references to the
				// same path use this first object
				//

				resolved.put(path, object);

				Object replacement = locate(path);
				if (object == replacement)
					return object;

				if (object.getClass() != replacement.getClass())
					replacement = convert(object.getClass(), replacement);

				copy(replacement, object);
				set(object, "$ref", path);
				resolve(object);
				return object;
			}
		}, resolve);
	}

	private <T> T convert(Class<T> type, Object replacement) {
		try {
			return Converter.cnv(type, replacement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void copy(Object from, Object to) {
		for (Field f : ObjectVisitor.getFields(from)) {
			try {
				f.set(to, f.get(from));
			} catch (Exception e) {
				log.error("failed to copy {}", f);
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	private boolean set(Object target, String field, Object path) {
		try {

			Field f = getField(target, field);
			if (f != null) {
				f.set(target, Converter.cnv(f.getGenericType(), path));
				return true;
			}
		} catch (Exception e) {
			log.warn("set ref {}", e);
			try {
				Field f = getField(target, "__extra");
				if (f != null) {
					Map<String, Object> map = (Map<String, Object>) f
							.get(target);
					map.put("$ref", path);
					return true;
				}
			} catch (Exception ee) {
				log.warn("set ref {}", ee);
				// ignore
			}
		}
		return false;
	}

	private Object get(String root, String sub) {
		Object r = roots.get(root);

		return get(r, sub.split("/"), 0);
	}

	@SuppressWarnings("rawtypes")
	private Object get(Object r, String[] split, int i) {
		if (r == null || split.length <= i)
			return r;

		String segment = split[i];
		if (r instanceof Map) {
			Map map = (Map) r;
			return get(map.get(segment), split, i + 1);
		}
		if (r instanceof List) {
			List list = (List) r;
			int index = getInt(segment);
			return get(list.get(index), split, i + 1);
		}
		if (r instanceof Collection) {
			Collection coll = (Collection) r;
			Object[] array = coll.toArray();
			int index = getInt(segment);
			return get(array[index], split, i + 1);
		}

		if (r.getClass().isArray()) {
			int index = getInt(segment);
			return Array.get(r, index);
		}

		Field field = getField(r, segment);
		if (field == null)
			field = getField(r, segment + "$");
		if (field == null)
			return null;

		return get(get(r, field), split, i + 1);
	}

	protected int getInt(String segment) {
		return Integer.parseInt(segment);
	}

	protected Field getField(Object r, String segment) {
		try {
			if (r == null) {
				System.out.println("??");
			} else
				return r.getClass().getField(segment);
		} catch (NoSuchFieldException | SecurityException e) {
			// ignore
		}
		return null;
	}

	private String getPath(Object object) {
		Field f = getField(object, "$ref");
		if (f == null || f.getType() != String.class)
			return null;

		return (String) get(object, f);
	}

	protected Object get(Object object, Field f) {
		try {
			return f.get(object);
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public static void resolve(Object root, Map<String, Object> roots) {
		ReferenceHandler rh = new ReferenceHandler(root, roots);
		rh.resolve(root);
	}

}
