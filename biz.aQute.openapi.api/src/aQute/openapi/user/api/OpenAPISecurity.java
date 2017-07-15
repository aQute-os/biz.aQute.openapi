package aQute.openapi.user.api;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Abstracts an application's user oriented security model. The idea is that
 * authenticators will find a defined user by, for example, its email. They then
 * can look for passwords or other credentials in the properties to authenticate
 * that user. The OpenAPI Runtime then uses the authenticated user to run the
 * request in a thread that is marked with the authenticated user.
 */
public interface OpenAPISecurity {

	/**
	 * Get a property for a given user
	 *
	 * @param user
	 *            the user id
	 * @param key
	 *            the property key
	 * @return the value or null to remove
	 */
	Optional<String> getProperty(String user, String key);
	void setProperty(String user, String key, String value);
	void setCredential(String user, String key, byte[] value);
	Optional<byte[]> getCredential(String user, String key);

	/**
	 * Return a user id for a user that has the given value for the given key
	 *
	 * @param user
	 *            the user id
	 * @param key
	 *            the property key
	 * @param value
	 *            the expected value
	 * @return the associated user id
	 */
	Optional<String> getUser(String key, String value);

	/**
	 * Dispatch the request and associate the authenticated user.
	 *
	 * @param authenticatedUser the user or null if not authenticated
	 * @param request
	 * @return the return value of the request
	 * @throws Exception
	 */
	<T> T dispatch(String authenticatedUser, Callable<T> request) throws Exception;

	Optional<String> getUser(String userId);
}
