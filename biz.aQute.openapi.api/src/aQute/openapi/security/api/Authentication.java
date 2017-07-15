package aQute.openapi.security.api;

public interface Authentication {

	/**
	 * The user name or null if set. This does not imply authentication
	 *
	 * @return the user name or null
	 */
	String getUser();

	/**
	 * Indicate if the caller could provide credentials
	 *
	 * @return true if the caller needs to provide credentials
	 * @throws Exception
	 */
	boolean needsCredentials() throws Exception;

	/**
	 * Force the security provider to request the credentials
	 */
	void requestCredentials() throws Exception;

	/**
	 * Is the caller authenticated?
	 *
	 * @return true if the caller is authenticated
	 */
	boolean isAuthenticated() throws Exception;

	/**
	 * Some security providers can be ignored, they are just there to trace
	 *
	 * @return true if this authentication can be ignored
	 */
	boolean ignore();

}
