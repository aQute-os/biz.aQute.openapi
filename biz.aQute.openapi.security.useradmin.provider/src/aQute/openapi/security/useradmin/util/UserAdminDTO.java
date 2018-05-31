package aQute.openapi.security.useradmin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.dto.DTO;

public class UserAdminDTO extends DTO {

	public static class RoleDTO extends DTO {
		public int					type;
		public String				name;
		public Map<String, Object>	properties;
		public Map<String, Object>	credentials;
		public List<String>			basic;
		public List<String>			required;
	}

	public long				date;
	public List<RoleDTO>	roles	= new ArrayList<>();

}
