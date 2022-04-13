package aQute.json.codec;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> implements Type {

	protected TypeReference() {
		// Make sure it cannot be directly instantiated
	}

	public Type getType() {
		return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
}
