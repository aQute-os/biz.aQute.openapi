package aQute.openapi.oauth2.provider;

import org.slf4j.Logger;

public enum OAuth2ProviderEnum {
	OPENID_CONNECT(null,true) {

		@Override
		public Handler handler(Logger logger, OAuth2Configuration config, ProviderDefinition def) throws Exception {
			return new OpenIdHandler(logger,config,def);
		}

	}, //
	GOOGLE(GoogleHandler.google,true) {

		@Override
		public Handler handler(Logger logger, OAuth2Configuration config, ProviderDefinition def) throws Exception {
			return new GoogleHandler(logger,config,def);
		}

	},
	GITHUB(GithubHandler.github,false) {

		@Override
		public Handler handler(Logger logger, OAuth2Configuration config, ProviderDefinition def) throws Exception {
			return new GithubHandler(logger,config,def);
		}

	};

	public final ProviderDefinition def;
	public final boolean openid;

	public ProviderDefinition getProviderDefinition() {
		return def;
	}

	OAuth2ProviderEnum(ProviderDefinition def, boolean openid) {
		this.def = def;
		this.openid = openid;
	}

	public abstract Handler handler(Logger logger,OAuth2Configuration config, ProviderDefinition def2) throws Exception;
}
