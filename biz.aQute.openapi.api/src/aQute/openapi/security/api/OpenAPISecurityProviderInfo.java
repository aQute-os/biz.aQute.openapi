package aQute.openapi.security.api;

import org.osgi.dto.DTO;

public class OpenAPISecurityProviderInfo extends DTO {
	public String	name;
	public String	type;
	public String	currentUser;
	public String	idKey;
	public String	idValue;
	public long		expires;
}