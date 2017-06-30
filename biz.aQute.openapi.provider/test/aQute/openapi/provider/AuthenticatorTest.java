package aQute.openapi.provider;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import aQute.openapi.security.api.Authentication;

public class AuthenticatorTest extends Assert {

	static class DTOAuthentication implements Authentication {
		boolean	credentialsRequested;
		boolean	needsCredentials;
		boolean	isAuthenticated;
		boolean	ignore;
		String	user;

		DTOAuthentication(boolean needsCredentials, boolean isAuthenticated, boolean ignore, String user) {
			this.needsCredentials = needsCredentials;
			this.isAuthenticated = isAuthenticated;
			this.ignore = ignore;
			this.user = user;
		}

		@Override
		public void requestCredentials() throws IOException {
			credentialsRequested = true;
		}

		@Override
		public boolean needsCredentials() {
			return needsCredentials;
		}

		@Override
		public boolean isAuthenticated() {
			return isAuthenticated;
		}

		@Override
		public boolean ignore() {
			return ignore;
		}

		@Override
		public String getUser() {
			return user;
		}
	}

	@Test
	public void testOneLevelAuthenticated() throws Exception {
		Authenticator a = new Authenticator();
		Authentication auth = new DTOAuthentication(false, true, false, "Peter");
		a.authenticate(auth);
		a.verify();
	}

	@Test(expected = SecurityException.class)
	public void testOneLevelNotAuthenticated() throws Exception {
		Authenticator a = new Authenticator();
		DTOAuthentication auth = new DTOAuthentication(false, false, false, "Peter");
		a.authenticate(auth);
		a.verify();
		assertFalse(auth.credentialsRequested);
	}

	@Test
	public void testOneLevelIgnore() throws Exception {
		Authenticator a = new Authenticator();
		DTOAuthentication auth = new DTOAuthentication(false, false, true, "Peter");
		a.authenticate(auth);
		a.verify();
		assertFalse(auth.credentialsRequested);
	}

	@Test
	public void testOneLevelNeedCredentials() throws Exception {
		Authenticator a = new Authenticator();
		DTOAuthentication auth = new DTOAuthentication(true, false, false, "Peter");
		a.authenticate(auth);
		a.verify();
		assertTrue(auth.credentialsRequested);
	}

	@Test(expected = SecurityException.class)
	public void testFailAndWithNotAndAuthentication() throws Exception {
		Authenticator a = new Authenticator();
		DTOAuthentication no = new DTOAuthentication(false, false, false, "Peter");
		DTOAuthentication yes = new DTOAuthentication(false, true, false, "Peter");
		a.authenticate(no);
		a.authenticate(yes);

		a.verify();

		assertFalse(no.credentialsRequested);
		assertFalse(yes.credentialsRequested);
	}

	@Test
	public void testFailOrWithNotAndAuthentication() throws Exception {
		Authenticator a = new Authenticator();
		DTOAuthentication no = new DTOAuthentication(false, false, false, "Peter");
		DTOAuthentication yes = new DTOAuthentication(false, true, false, "Peter");
		a.authenticate(no);
		a.or();
		a.authenticate(yes);
		a.verify();

		assertFalse(no.credentialsRequested);
		assertFalse(yes.credentialsRequested);
	}

	@Test
	public void testFailAndWithNeedsCredentialsForFirst() throws Exception {
		Authenticator a = new Authenticator();
		DTOAuthentication no = new DTOAuthentication(true, false, false, "Peter");
		DTOAuthentication yes = new DTOAuthentication(false, true, false, "Peter");
		a.authenticate(no);
		a.authenticate(yes);
		a.verify();

		assertTrue(no.credentialsRequested);
		assertFalse(yes.credentialsRequested);
	}

	@Test
	public void testFailAndWithNeedsCredentialsForSecond() throws Exception {
		Authenticator a = new Authenticator();
		DTOAuthentication no = new DTOAuthentication(true, false, false, "Peter");
		DTOAuthentication yes = new DTOAuthentication(false, true, false, "Peter");
		a.authenticate(yes);
		a.authenticate(no);
		a.verify();

		assertTrue(no.credentialsRequested);
		assertFalse(yes.credentialsRequested);
	}

	@Test
	public void testFailAndWithNeedsCredentialsForBoth() throws Exception {
		Authenticator a = new Authenticator();
		DTOAuthentication no = new DTOAuthentication(true, false, false, "Peter");
		DTOAuthentication yes = new DTOAuthentication(true, false, false, "Peter");
		a.authenticate(no);
		a.authenticate(yes);
		a.verify();

		assertTrue(no.credentialsRequested);
		assertFalse(yes.credentialsRequested);
	}

}
