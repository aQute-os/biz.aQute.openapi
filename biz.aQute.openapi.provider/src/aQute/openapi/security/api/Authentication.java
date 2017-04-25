package aQute.openapi.security.api;

public interface Authentication {

	String getUser();

	boolean needsCredentials();

	void requestCredentials();

	boolean isAuthenticated();

	boolean ignore();

}
