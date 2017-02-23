package aQute.openapi.security.oauth2.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.dto.DTO;

public class OAuth2DTO extends DTO {
	public String name;
	public String description;
	public Flow flow;
	public String authorizationURL;
	public String tokenURL;
	public Map<String,String> scopes = new LinkedHashMap<>();
}

