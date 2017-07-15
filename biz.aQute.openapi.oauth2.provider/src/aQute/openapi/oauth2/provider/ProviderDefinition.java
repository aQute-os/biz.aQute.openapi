package aQute.openapi.oauth2.provider;

import java.net.URI;

import org.osgi.dto.DTO;

public class ProviderDefinition extends DTO {

	public String						issuer;
	public URI							authorization_endpoint;
	public URI							token_endpoint;
	public URI							userinfo_endpoint;
	public URI							revocation_endpoint;
	public URI							jwks_uri;
	public String[]						response_types_supported;
	public String[]						subject_types_supported;
	public String[]						id_token_signing_alg_values_supported;
	public String[]						scopes_supported;
	public String[]						token_endpoint_auth_methods_supported;
	public String[]						claims_supported;
	public String[]						code_challenge_methods_supported;

}
