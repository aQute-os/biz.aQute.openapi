package aQute.openapi.provider;

import java.util.ArrayList;
import java.util.List;

import aQute.openapi.provider.OpenAPIBase.DoNotTouchResponse;
import aQute.openapi.security.api.Authentication;

/**
 * @author aqute
 */
class Authenticator {

	String						user;
	final static int			ST_OK					= 0;
	final static int			ST_FAILED				= 1;
	final static int			ST_INITIAL				= 2;
	final static int			ST_AUTHENTICATED		= 3;
	final static int			ST_NEEDS_CREDENTIALS	= 4;
	final static int			ST_OR					= 5;

	final static int			EV_OR					= 0;
	final static int			EV_FAIL					= 1;
	final static int			EV_AUTHENTICATED		= 2;
	final static int			EV_CREDENTIALS			= 3;

	//@formatter:off
	final static int[][] transition = {
			//           					EV_OR			EV_FAIL  		EV_AUTHENTICATED 		EV_CREDENTIALS
		    /* ST_OK 				*/ {	-1,				-1, 		  	-1,						-1 },
		    /* ST_FAILED 			*/ {	ST_OR,			ST_FAILED, 		ST_FAILED,				ST_FAILED },
		    /* ST_INITIAL 			*/ {	ST_OR,			ST_FAILED, 		ST_AUTHENTICATED,		ST_NEEDS_CREDENTIALS },
		    /* ST_AUTHENTICATED 	*/ {	ST_OK,			ST_FAILED, 		ST_AUTHENTICATED,		ST_NEEDS_CREDENTIALS },
		    /* ST_NEEDS_CREDENTIALS	*/ {	ST_OK,			ST_FAILED, 		ST_NEEDS_CREDENTIALS,	ST_NEEDS_CREDENTIALS },
		    /* ST_OR				*/ {	ST_OR,			ST_FAILED, 		ST_AUTHENTICATED,		ST_NEEDS_CREDENTIALS },
	};

	//@formatter:on
	int							state					= ST_INITIAL;
	Authentication				needsCredentials;
	final List<Authentication>	selectedAuthentications	= new ArrayList<Authentication>();
	boolean						hadIgnores;

	void authenticate(Authentication auth, String... scopes) throws Exception {
		if (state != ST_OK) {
			if (auth == null) {
				event(EV_FAIL);
			} else {

				if (auth.isAuthenticated()) {
					this.user = auth.getUser();
					event(EV_AUTHENTICATED);
				} else if (auth.needsCredentials()) {

					if (needsCredentials == null)
						needsCredentials = auth;

					event(EV_CREDENTIALS);
				} else if (auth.ignore()) {
					hadIgnores = true;
				} else {
					event(EV_FAIL);
				}
			}
		}
	}

	private void event(int ev) {
		this.state = transition[this.state][ev];
	}

	public void or() {
		event(EV_OR);
		selectedAuthentications.clear();
	}

	public void verify() throws Exception {
		if (state == ST_OK || state == ST_AUTHENTICATED) {
			return;
		}

		if (state == ST_INITIAL && hadIgnores)
			return;

		if (state == ST_NEEDS_CREDENTIALS) {
			needsCredentials.requestCredentials();
			throw new DoNotTouchResponse();
		}
		throw new SecurityException(); // provider should throw a response
	}

	public void authorize(String action) {

	}

	public String getUser() {
		return user;
	}

}
