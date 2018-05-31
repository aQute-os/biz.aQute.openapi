package aQute.json.codec;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Traverse an object and replace reference if so required. The visitor can
 * replace a reference to an object. A replace object is not traversed. If the
 * graph contains cycles then the visitor is only called for the first occasion.
 *
 * Lists are traversed and the index is replace when necessary. Other
 * collections will remove the old object and add the replacement.
 */
public class ObjectVisitor {

	final IdentityHashMap<Object, Object>	preventCycles	= new IdentityHashMap<>();
	private Visitor							visitor;
	private Object							target;

	public interface Visitor {
		Object visit(Object object);
	}

	private ObjectVisitor(Visitor visitor, Object target) {
		this.visitor = visitor;
		this.target = target;
	}

	public static Object visit(Visitor visitor, Object target) {
		return new ObjectVisitor(visitor, target).visit();
	}

	private Object visit() {
		return visit0(target);
	}

	@SuppressWarnings("unchecked")
	Object visit0(Object object) {
		if (object == null)
			return null;

		if (preventCycles.containsKey(object))
			return preventCycles.get(object);

		Object visit = visitor.visit(object);
		preventCycles.put(object, visit);

		if (visit == object) {
			if (object instanceof Map)
				visitMap(visitor, (Map<Object, Object>) object);
			else if (object instanceof List)
				visitList(visitor, (List<Object>) object);
			else if (object instanceof Collection)
				visitCollection(visitor, (Collection<Object>) object);
			else if (object.getClass().isArray())
				visitArray(visitor, object);
			else if (!isSimple(object))
				visitDTO(visitor, object);
		}
		return visit;
	}

	private boolean isSimple(Object object) {
		return object instanceof Number || object instanceof String
				|| object instanceof Pattern || object instanceof Date
				|| object instanceof UUID;
	}

	private void visitDTO(Visitor visitor, Object object) {
		for (Field f : getFields(object)) {
			try {
				Object target = f.get(object);
				Object replacement = visit0(target);
				if (target != replacement) {
					f.set(object, replacement);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void visitArray(Visitor visitor, Object array) {
		if (!array.getClass().getComponentType().isPrimitive()) {

			int length = Array.getLength(array);
			for (int i = 0; i < length; i++) {
				Object target = Array.get(array, i);
				Object replacement = visit0(target);
				if (target != replacement) {
					Array.set(array, i, replacement);
				}
			}
		}
	}

	private void visitList(Visitor visitor, List<Object> coll) {
		List<Object> list = coll;
		for (int i = 0; i < list.size(); i++) {
			Object target = list.get(i);
			Object replacement = visit0(target);
			if (target != replacement) {
				list.set(i, replacement);
			}
		}
	}

	private void visitCollection(Visitor visitor, Collection<Object> coll) {
		for (Object target : new ArrayList<>(coll)) {
			Object replacement = visit0(target);
			if (target != replacement) {
				coll.remove(target);
				coll.add(replacement);
			}
		}
	}

	protected void visitMap(Visitor visitor, Map<Object, Object> map) {
		Iterator<Map.Entry<Object, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Object, Object> entry = it.next();

			Object target = entry.getValue();

			Object replacement = visit0(target);
			if (target != replacement) {
				entry.setValue(replacement);
			}
		}
	}

	public static Field[] getFields(Object from) {
		return getFields(from.getClass());
	}

	public static Field[] getFields(Class<?> from) {
		return Stream.of(from.getFields())
				.filter(f -> !Modifier.isStatic(f.getModifiers())
						&& !Modifier.isFinal(f.getModifiers())
						&& !Modifier.isVolatile(f.getModifiers()))
				.toArray(Field[]::new);
	}
}
