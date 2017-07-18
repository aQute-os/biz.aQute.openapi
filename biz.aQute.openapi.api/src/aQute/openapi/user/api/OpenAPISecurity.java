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

	/**
	 * Set a property for a user. Will create the user if it does not exist yet
	 *
	 * @param user
	 *            the user, not null
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	void setProperty(String user, String key, String value);

	/**
	 * Set a credential for a user. Will create the user if it does not exist
	 * yet
	 *
	 * @param user
	 *            the user, not null
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	void setCredential(String user, String key, byte[] value);

	/**
	 * Get a credential for a user. Will create the user if it does not exist
	 * yet
	 *
	 * @param user
	 * @param key
	 * @return the value
	 */
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
	 * Dispatch the request and associate the authenticated user. After running,
	 * the current user is restored.
	 *
	 * @param authenticatedUser
	 *            the user or null if not authenticated
	 * @param request
	 * @return the return value of the request
	 * @throws Exception
	 */
	<T> T dispatch(String authenticatedUser, Callable<T> request) throws Exception;

	/**
	 * Get the user currently associated with the thread.
	 *
	 * @param userId
	 * @return
	 */
	Optional<String> getUser(String userId);

	/**
	 * Answer if the user associated with this thread has the following permission
	 * @param action the action to perform
	 * @param arguments the arguments of the action
	 * @return true if the permission is granted, false otherwise
	 */
	default boolean hasPermission(String action, String ... arguments) {
		return false;
	}
}
