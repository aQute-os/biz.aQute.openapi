package aQute.openapi.provider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

class ParameterizedTypeImpl implements ParameterizedType {

	final Type			rawType;
	final Class< ? >	rawClass;
	final Type[]		actualTypeArguments;
	final Type			ownerType;

	ParameterizedTypeImpl(Type rawType, Class< ? > rawClass, Type ownerType, Type... actualTypeArguments) {
		this.rawType = rawType;
		this.rawClass = rawClass;
		this.ownerType = ownerType;
		this.actualTypeArguments = actualTypeArguments;

	}

	@Override
	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	@Override
	public Type getOwnerType() {
		return ownerType;
	}

}
