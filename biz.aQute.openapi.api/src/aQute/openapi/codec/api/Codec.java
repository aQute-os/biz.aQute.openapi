package aQute.openapi.codec.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

/**
 * This is the interface between the (de)serializer and the runtime. There is a
 * default implementation included but JSON serializers/deserializers are highly
 * competitive. Especially the JSONB standard allows dtos to be annotated in a
 * standardized way.
 *
 */
public interface Codec {

	/**
	 * Encode an object to an outpustream. Return the mime type.
	 * 
	 * @param object
	 *            any object, including null
	 * @param out
	 *            the output stream, in general buffered.
	 * @return the mime type or null for default JSON & UTF-8.
	 */
	String encode(Object object, OutputStream out) throws Exception;

	/**
	 * Decode a JSON stream defined by the mime type from the in input
	 * stream.The instantiator provides a function that is used for the creating
	 * of objects encountered so that the caller can override.
	 * 
	 * @param <T> the return typed object
	 * @param <X> any class to instantiate
	 * @param type the type of the returned object
	 * @param in the input stream
	 * @param mime the mime type or null for standard JSON & UTF-8
	 * @param instantiator the instantiator that can create custom subtypes, this is coming from the OpenAPIBase instantiate method.
	 * @return an object of the requested type
	 */
	<T, X> T decode(Class<T> type, InputStream in, String mime, Function<Class<X>, X> instantiator) throws Exception;

	
	/**
	 * Decode a JSON stream defined by the mime type from the in input
	 * stream.The instantiator provides a function that is used for the creating
	 * of objects encountered so that the caller can override.
	 * 
	 * @param <T> the typed object of the returned list e.g. List<T>
	 * @param <X> any class to instantiate
	 * @param type the type of the returned object
	 * @param in the input stream
	 * @param mime the mime type or null for standard JSON & UTF-8
	 * @param instantiator the instantiator that can create custom subtypes
	 * @return an object of the requested type
	 */
	<T, X> List<T> decodeList(Class<T> type, InputStream in, String mime, Function<Class<X>, X> instantiator)
			throws Exception;

	/**
	 * Handle strings encountered in the input stream 
	 * @param <T> the specific type substituted for a string field
	 * @param type the type 
	 * @param toString the function to take a type and turn it into a String
	 * @param fromString the function to take a string and turn it into a type
	 */
	<T> void addStringHandler(Class<T> type, Function<T, String> toString, Function<String, T> fromString);

	/**
	 * The content type
	 * @return
	 */
	default String getContentType() {
		return "application/json;charset=utf8";
	}
}
