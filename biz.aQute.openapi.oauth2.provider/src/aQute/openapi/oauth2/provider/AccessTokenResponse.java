package aQute.openapi.oauth2.provider;

import java.util.Map;

import org.osgi.dto.DTO;

public class AccessTokenResponse extends DTO {
	public String				access_token;
	public String				token_type;
	public int					expires_in;
	public String				refresh_token;
	public String				id_token;
	public String				error;
	public String				body;
	public Map<String, Object>	__extra;
}
