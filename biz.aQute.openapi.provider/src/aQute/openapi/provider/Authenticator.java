package aQute.openapi.provider;

import java.util.ArrayList;
import java.util.List;

import aQute.openapi.security.api.Authentication;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.api.OpenAPISecurityProvider;

class Authenticator {

	final OpenAPIContext	context;

	String					user;
	final static int		ST_OK					= 0;
	final static int		ST_FAILED				= 1;
	final static int		ST_INITIAL				= 2;
	final static int		ST_AUTHENTICATED		= 3;
	final static int		ST_NEEDS_CREDENTIALS	= 4;
	final static int		ST_OR					= 5;

	final static int		EV_OR					= 0;
	final static int		EV_FAIL					= 1;
	final static int		EV_AUTHENTICATED		= 2;
	final static int		EV_CREDENTIALS			= 3;

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
	int						state					= ST_INITIAL;
	Authentication			needsCredentials;
	final List<Authentication>	selectedAuthentications	= new ArrayList<Authentication>();

	Authenticator(OpenAPIContext context) {
		this.context = context;
	}

	OpenAPIContext authenticate(OpenAPISecurityDefinition def, String... scopes) {
		if (state != ST_OK) {
			OpenAPISecurityProvider provider = context.dispatcher.getSecurityProvider(def);
			if (provider == null) {
				event(EV_FAIL);
			} else {

				Authentication auth = provider.authenticate(context, def);

				if (auth.isAuthenticated()) {
					event(EV_AUTHENTICATED);
				} else if (auth.needsCredentials()) {

					if (needsCredentials == null)
						needsCredentials = auth;

					event(EV_CREDENTIALS);
				} else if (!auth.ignore()) {
					event(EV_FAIL);
				}
			}
		}
		return context;
	}

	private void event(int ev) {
		this.state = transition[this.state][ev];
	}

	public OpenAPIContext or() {
		event(EV_OR);
		selectedAuthentications.clear();
		return context;
	}

	public void verify() {
		if (state == ST_OK || state == ST_AUTHENTICATED)
			return;

		if (state == ST_NEEDS_CREDENTIALS) {
			needsCredentials.requestCredentials();
			throw new SecurityException(); // provider should throw a response
		}
	}

	public void authorize(String action) {

	}

}
