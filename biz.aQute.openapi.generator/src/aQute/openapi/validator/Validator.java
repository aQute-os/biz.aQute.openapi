package aQute.openapi.validator;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import aQute.lib.env.Env;
import aQute.openapi.annotations.Required;
import aQute.openapi.annotations.ValidatorArray;
import aQute.openapi.annotations.ValidatorNumber;
import aQute.openapi.annotations.ValidatorObject;
import aQute.openapi.annotations.ValidatorString;

public class Validator extends Env {

	static class Link {
		Link	prev;
		String	path;
		Object	object;

		Link(Link prev, String path, Object object) {
			this.prev = prev;
			this.path = path;
			this.object = object;

			if (prev != null) {
				Link p = prev.find(object);
				if (p != null) {
					throw new RuntimeException("Cycle detected from " + p.path + " to " + path);
				}
			}
		}

		Link find(Object object2) {

			if (prev == null)
				return null;

			if (object != object2)
				return null;

			return this;
		}
	}

	public Validator(Env parent) {
		super(parent);
	}

	public Validator() {}

	public void verify(Object object) {
		try {

			verify(null, object, "#", new Link(null, "#", object));

		} catch (Exception e) {
			e.printStackTrace();
			error("Failed to verify %s", e);
		}
	}

	public void verify(AnnotatedElement element, Object object, String path, Link link) throws Exception {

		Link nxtLink = new Link(link, path, object);

		if (object instanceof Collection)
			verifyCollection(element, (Collection< ? >) object, path, link);
		else if (object.getClass().isArray())
			verifyArray(element, object, path, link);
		else if (object instanceof String) {
			verifyString(element, (String) object, path);
		} else if (object instanceof Number) {
			verifyNumber(element, (Number) object, path);
		} else if (object instanceof Map) {
			verifyMap(element, (Map< ? , ? >) object, path, link);
		} else if (isDTO(object)) {
			verifyDTO(element, object, path, link);
		} else {
			// Ignore other types
		}
	}

	private boolean isDTO(Object object) {

		long cnt = Stream.of(object.getClass().getFields()).filter(f -> !Modifier.isStatic(f.getModifiers())).count();

		return cnt > 0;
	}

	private void verifyMap(AnnotatedElement element, Map< ? , ? > v, String path, Link link) throws Exception {

		for (Entry< ? , ? > e : v.entrySet()) {

			if (!(e.getKey() instanceof String) && !(e.getKey() instanceof Enum)) {
				error("%s - Unsupported key type %s for validation", path, e.getKey().getClass());
			} else {
				String nxtPath = path + "/" + e.getKey();
				Object value = e.getValue();

				if (value == null) {
					error("%s - null value", nxtPath);
				} else {
					verify(element, value, nxtPath, link);
				}
			}
		}

	}

	void verifyDTO(AnnotatedElement element, Object o, String path, Link link) throws Exception {

		ValidatorObject vo = element != null ? element.getAnnotation(ValidatorObject.class) : null;

		Class< ? extends Object> clazz = o.getClass();

		Field[] fields = clazz.getFields();
		int nrOfFields = 0;

		for (Field f : fields) {
			if (isStatic(f.getModifiers()))
				continue;

			nrOfFields++;

			Object v = f.get(o);

			String nxtPath = path + "/" + f.getName();

			Required required = f.getAnnotation(Required.class);
			if (required != null) {

				if (v == null) {
					error("%s required but not set", nxtPath);
					continue;
				}

				if (v instanceof Collection && ((Collection< ? >) v).size() == 0) {
					error("%s required but collection empty", nxtPath);
					continue;
				}

				if (v instanceof Map && ((Map< ? , ? >) v).size() == 0) {
					error("%s required but map empty", nxtPath);
					continue;
				}
			}

			if (v != null) {
				verify(f, v, nxtPath, link);
			}

		}
	}

	private void verifyNumber(AnnotatedElement f, Number v, String path) {
		ValidatorNumber vn = f.getAnnotation(ValidatorNumber.class);
		if (vn != null) {
			double d = v.doubleValue();

			boolean exclusiveMaximum = vn.exclusiveMaximum();
			boolean exclusiveMinimum = vn.exclusiveMinimum();

			if (exclusiveMaximum && d > vn.maximum()) {
				error("%s – %s > %s", path, d, vn.maximum());
			}
			if (exclusiveMaximum == false && d >= vn.maximum()) {
				error("%s – %s >= %s", path, d, vn.maximum());
			}
			if (exclusiveMinimum && d < vn.maximum()) {
				error("%s – %s < %s", path, d, vn.maximum());
			}
			if (exclusiveMinimum == false && d <= vn.maximum()) {
				error("%s – %s <= %s", path, d, vn.maximum());
			}

			long multipleOf = vn.multipleOf();
			if (multipleOf != 1 && multipleOf != 0) {
				long l = (long) d;
				if ((l % multipleOf) != 0) {
					error("%s – %s not a multiple of %s", path, l, multipleOf);
				}
			}
		}
	}

	private void verifyString(AnnotatedElement f, String v, String path) {
		ValidatorString vs = f.getAnnotation(ValidatorString.class);
		if (vs != null) {

			int size = v.length();

			if (size < vs.minLength())
				error("%s – String too short. It has %s, but minLength=%s", path, size, vs.minLength());
			if (size > vs.maxLength())
				error("%s – String too long. It has %s, but maxLength=%s", path, size, vs.maxLength());

			String pattern = vs.pattern();
			if (pattern != null)
				try {
					Pattern p = Pattern.compile(pattern);
					Matcher matcher = p.matcher(v);
					if (!matcher.matches()) {
						error("%s – Pattern mismatch. Value is %s, pattern is %s", path, v, matcher);
					}
				} catch (Exception e) {
					error("%s – Invalid pattern %s (%s)", path, pattern, e.getMessage());
				}

			Class< ? > enum_ = vs.enum_();

			if (enum_ != null) {
				Object[] enumConstants = enum_.getEnumConstants();
				if (enumConstants != null) {
					for (Object e : enumConstants) {
						if (v.equals(e)) {
							return;
						}
					}
					error("%s – %s is not a member of specified enum %s.", path, v, Arrays.toString(enumConstants));
				}
			}
		}
	}

	private void verifyCollection(AnnotatedElement f, Collection< ? > v, String path, Link link) throws Exception {
		ValidatorArray va = f.getAnnotation(ValidatorArray.class);

		if (va != null) {
			int size = v.size();

			if (size > va.maxItems()) {
				error("%s – Array has too many items. It has %s, but maxItems=%s", path, size, va.maxItems());
			}
			if (size < va.minItems()) {
				error("%s – Array has too few items. It has %s, but minItems=%s", path, size, va.minItems());
			}

			if (va.uniqueItems()) {

				StringBuilder duplicates = new StringBuilder();

				Set<Object> set = new HashSet<>(v);

				v.stream().forEach(i -> {
					if (!set.remove(i)) {
						duplicates.append(" ").append(i);
					}
				});

				if (set.size() < v.size()) {
					error("%s – Array must have unique items, duplicates are %s", path, duplicates);
				}
			}
		}

		int n = 0;
		for (Object o : v) {
			verify(f, o, path + "/" + n++, link);
		}
	}

	private void verifyArray(AnnotatedElement f, Object v, String path, Link link)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException, Exception {
		ValidatorArray va = f.getAnnotation(ValidatorArray.class);

		int size = Array.getLength(v);

		if (va != null) {

			if (size > va.maxItems()) {
				error("%s – Array has too many items. It has %s, but maxItems=%s", path, size, va.maxItems());
			}
			if (size < va.minItems()) {
				error("%s – Array has too few items. It has %s, but minItems=%s", path, size, va.minItems());
			}

			if (va.uniqueItems()) {

				StringBuilder duplicates = new StringBuilder();
				Set<Object> set = new HashSet<>();

				for (int i = 0; i < size; i++) {

					Object member = Array.get(v, i);
					if (set.add(member))
						duplicates.append(" ").append(member);

				}

				if (set.size() < size) {
					error("%s – Array must have unique items, duplicates are %s", path, duplicates);
				}
			}
		}

		for (int i = 0; i < size; i++) {
			verify(f, Array.get(v, i), path + "/" + i, link);
		}
	}

	private boolean isStatic(int modifiers) {
		return Modifier.isStatic(modifiers);
	}

}
