package aQute.openapi.security.useradmin.util;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import aQute.lib.json.JSONCodec;
import aQute.lib.strings.Strings;
import aQute.openapi.security.useradmin.util.UserAdminDTO.RoleDTO;

@Component(service = UserAdminFacade.class)
public class UserAdminFacade {
	final static Pattern										ROLE_NAME_P	= Pattern
			.compile("\\p{javaJavaIdentifierStart}[\\p{javaJavaIdentifierPart}:*,\\-]*");
	final static ConcurrentHashMap<String, WildcardPermission>	permissions	= new ConcurrentHashMap<>();

	@Reference
	UserAdmin													userAdmin;

	public User getUser(String id) {
		if (id.equals("@"))
			id = Role.USER_ANYONE;

		Role role = userAdmin.getRole(id);
		if (role == null)
			return null;

		if (!(role instanceof User))
			throw new IllegalArgumentException("Not a user");

		return (User) role;
	}

	public CharSequence line(User r) throws InvalidSyntaxException {
		return basics(r);
	}

	private StringBuilder basics(Role r) throws InvalidSyntaxException {
		StringBuilder sb = new StringBuilder();
		if (r instanceof Group)
			sb.append("G:");
		else if (r instanceof User)
			sb.append("U:");
		else
			sb.append("#");

		sb.append(r.getName()).append(":");

		sb.append(Strings.join(getInGroups(r)));
		return sb;
	}

	public CharSequence line(Group r) throws InvalidSyntaxException {
		StringBuilder sb = basics(r);
		sb.append(":");
		sb.append(Strings.join(getMembers(r)));
		sb.append(":");
		sb.append(Strings.join(getRequiredMembers(r)));
		return sb;
	}

	public Set<Role> getMembers(Group r) {
		Role[] roles = r.getMembers();
		return toSet(roles);
	}

	public Set<Role> getRequiredMembers(Group r) {
		Role[] roles = r.getRequiredMembers();
		return toSet(roles);
	}

	public <T> Set<T> toSet(T[] roles) {
		if (roles == null)
			return Collections.emptySet();

		return new HashSet<T>(Arrays.asList(roles));
	}

	@SuppressWarnings("unchecked")
	public CharSequence inspect(User r) throws InvalidSyntaxException {
		Authorization a = userAdmin.getAuthorization(r);
		return String.format(
				"User               %s\n"
						+ "In groups          %s\n"
						+ "Authorized         %s\n"
						+ "Properties         %s\n"
						+ "Credentials        %s\n",
				r.getName(), getInGroups(r), toSet(a.getRoles()), toMap(r.getProperties()),
				Collections.list(r.getCredentials().keys()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> toMap(Dictionary properties) {
		Map<String, Object> map = new HashMap<>();
		for (Object k : Collections.list(properties.keys())) {
			map.put((String) k, properties.get(k));
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public CharSequence inspect(Group r) throws InvalidSyntaxException {
		return String.format(
				"Group              %s\n"
						+ "In groups          %s\n"
						+ "Basic Members      %s\n"
						+ "Requried Members   %s\n"
						+ "Properties         %s\n"
						+ "Credentials        %s\n",
				r.getName(), getInGroups(r), toSet(r.getMembers()), toSet(r.getRequiredMembers()),
				toMap(r.getProperties()), Collections.list(r.getCredentials().keys()));
	}

	public Set<Group> getInGroups(Role r) throws InvalidSyntaxException {

		return Stream
				.of(userAdmin.getRoles(null))
				.filter(r1 -> r1 instanceof Group)
				.map(r2 -> (Group) r2)
				.filter(g -> hasMember(g, r))
				.collect(Collectors.toSet());
	}

	public boolean hasMember(Group g, Role r) {
		Role[] members = g.getMembers();
		if (members == null)
			return false;

		for (Role m : members) {
			if (m.equals(r))
				return true;
		}
		return false;
	}

	public void validateName(String id) {
		if (!ROLE_NAME_P.matcher(id).matches())
			throw new IllegalArgumentException(
					"Not a valid user name " + id + " (must be a java identifier with '-' allowed)");
	}

	public Group getGroup(String groupName) {
		Role role = userAdmin.getRole(groupName);
		if (role == null)
			return null;

		if (!(role instanceof Group))
			throw new IllegalArgumentException("Not a group");

		return (Group) role;
	}

	public Role getRole(String id) {
		return userAdmin.getRole(id);
	}

	public User createUser(String id) {
		validateName(id);
		Role role = getRole(id);
		if (role != null)
			throw new IllegalArgumentException("A role with this name already exists " + id);

		return (User) userAdmin.createRole(id, Role.USER);
	}

	public Group createGroup(String id) {
		validateName(id);
		Role role = getRole(id);
		if (role != null)
			throw new IllegalArgumentException("A role with this name already exists " + id);

		Group g = (Group) userAdmin.createRole(id, Role.GROUP);
		return g;
	}

	public void modify(Role role, boolean noCreateGroups, boolean replace, Set<Role> becomeMemberOf)
			throws InvalidSyntaxException {

		Role[] roles = userAdmin.getRoles(null);

		if (replace) {
			for (Role r : roles) {
				if (r instanceof Group) {
					((Group) r).removeMember(role);
				}
			}
		}

		for (Role target : becomeMemberOf) {
			if (target instanceof Group) {
				((Group) target).addMember(role);
			}
		}

	}

	public boolean remove(Role g) {
		return userAdmin.removeRole(g.getName());
	}

	public Collection<Role> getRoles() throws InvalidSyntaxException {
		return getRoles(null);
	}

	public Collection<Role> getRoles(String filter) throws InvalidSyntaxException {
		Role[] roles = userAdmin.getRoles(filter);
		if (roles == null)
			return Collections.emptyList();

		return Arrays.asList(roles);
	}

	public Set<Role> toRoles(boolean noCreateGroups, String... members) throws InvalidSyntaxException {
		return toRoles(noCreateGroups, Arrays.asList(members));
	}

	public Set<Role> toRoles(boolean noCreateGroups, Collection<String> members) throws InvalidSyntaxException {
		if (members == null || members.isEmpty())
			return Collections.emptySet();

		Set<Role> result = new HashSet<>();
		Collection<Role> all = getRoles();
		for (String member : members) {
			if (isWildcard(member)) {
				result.addAll(filter(member, all));
			} else {
				Role role = getRole(member);
				if (role == null) {
					if (noCreateGroups)
						throw new IllegalArgumentException("No such role name " + member);
					role = userAdmin.createRole(member, Role.GROUP);
				}
				result.add(role);
			}
		}
		return result;
	}

	public Set<? extends Role> filter(String glob, Collection<? extends Role> all) {
		glob = toPattern(glob);

		return filter(Pattern.compile(glob), all);
	}

	private String toPattern(String glob) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < glob.length(); i++) {
			char c = glob.charAt(i);
			switch (c) {
			case '*':
			case '?':
			case '+':
			case '[':
			case '(':
			case '\\':
			case '^':
			case '$':
			case '.':
				sb.append('\\').append(c);
				break;

			case '@':
				sb.append(".*");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	public Set<? extends Role> filter(Pattern glob, Collection<? extends Role> all) {
		Set<Role> result = new HashSet<>();
		for (Role r : all) {
			if (glob.matcher(r.getName()).matches())
				result.add(r);
		}
		return result;
	}

	private boolean isWildcard(String member) {
		return member.indexOf('@') >= 0;
	}

	public void addToGroup(Group g, Collection<? extends Role> roles) {
		for (Role r : roles) {
			g.addMember(r);
		}
	}

	public boolean remove(String id) {
		return userAdmin.removeRole(id);
	}

	public void clear(Group g) {
		Role[] roles = g.getMembers();
		if (roles == null)
			return;

		for (Role role : roles)
			g.removeMember(role);
	}

	public Set<Group> getGroups() throws InvalidSyntaxException {
		return toGroups(getRoles());
	}

	public Set<Group> toGroups(Collection<Role> roles) {
		return roles.stream()
				.filter(r -> r instanceof Group)
				.map(r -> (Group) r)
				.collect(Collectors.toSet());
	}

	public Set<User> toUsers(Collection<Role> roles) {
		return roles.stream()
				.filter(r -> r instanceof User)
				.map(r -> (User) r)
				.collect(Collectors.toSet());
	}

	public void remove(Set<? extends Role> roles) throws InvalidSyntaxException {
		for (Role r : roles) {
			for (Group g : getGroups()) {
				if (!roles.contains(g))
					g.removeMember(r);
			}
			userAdmin.removeRole(r.getName());
		}
	}

	public boolean exists(Role r) {
		return r.equals(userAdmin.getRole(r.getName()));
	}

	public Set<Role> imports(String in) throws Exception {
		UserAdminDTO result = new JSONCodec().dec().from(in).get(UserAdminDTO.class);
		Set<Role> roles = new TreeSet<>((a, b) -> a.getName().compareTo(b.getName()));

		for (RoleDTO rdto : result.roles) {

			Role role = mkRole(rdto);
			roles.add(role);
		}

		return roles;
	}

	private Role mkRole(RoleDTO r) throws InvalidSyntaxException {
		switch (r.type) {
		case Role.GROUP:
			return mkGroup(r);

		case Role.USER:
			return mkUser(r);

		default:
		case Role.ROLE:
			throw new IllegalArgumentException("Import cannot define Role types " + r);
		}
	}

	private Role mkUser(RoleDTO r) {
		User u = getUser(r.name);
		if (u == null)
			u = createUser(r.name);

		setProperties(u.getProperties(), r.properties);
		setProperties(u.getCredentials(), r.credentials);
		return u;
	}

	private Role mkGroup(RoleDTO r) throws InvalidSyntaxException {
		Group g = getGroup(r.name);
		if (g == null)
			g = createGroup(r.name);

		setProperties(g.getProperties(), r.properties);
		setProperties(g.getCredentials(), r.credentials);

		for (Role m : toRoles(false, r.basic))
			g.addMember(m);

		for (Role m : toRoles(false, r.required))
			g.addRequiredMember(m);
		return g;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setProperties(Dictionary dictionary, Map<String, Object> properties) {
		if (properties == null)
			return;

		for (Map.Entry<String, Object> e : properties.entrySet()) {
			dictionary.put(e.getKey(), e.getValue());
		}
	}

	public String exports() throws Exception {
		UserAdminDTO uadto = new UserAdminDTO();
		uadto.date = System.currentTimeMillis();
		for (Role role : getRoles()) {
			if (role instanceof User) {
				if (Role.USER_ANYONE.equals(role.getName()))
					continue;

				RoleDTO roledto = new RoleDTO();
				roledto.name = role.getName();
				roledto.properties = toMap(role.getProperties());
				if (role instanceof User) {
					roledto.type = Role.USER;
					roledto.credentials = toMap(((User) role).getCredentials());
				}
				if (role instanceof Group) {
					roledto.type = Role.GROUP;
					Group g = (Group) role;
					roledto.basic = getMembers(g).stream().map(m -> m.getName()).collect(toList());
					roledto.required = getRequiredMembers(g).stream().map(m -> m.getName()).collect(toList());
				}
				uadto.roles.add(roledto);
			}
		}
		return new JSONCodec().enc().put(uadto).toString();
	}

	public Authorization getAuthorization(String userId) {
		User user = getUser(userId);
		if (user == null)
			return null;

		return userAdmin.getAuthorization(user);
	}

	public boolean implies(Role user, String action, String... arguments) {
		if ( ! (user instanceof User )) // user.anyone is role
			return false;

		Authorization authorization = userAdmin.getAuthorization((User)user);

		String[] roles = authorization.getRoles();
		if (roles == null || roles.length == 0) {
			return false;
		}

		String effective = effective(action, arguments);
		for (String role : roles) {
			if (role.equals(action) && arguments.length == 0)
				return true;

			WildcardPermission wp = permissions.computeIfAbsent(role, (r) -> WildcardPermission.caseSensitive(role));
			if (wp.implies(effective))
				return true;

		}
		return false;
	}

	public String effective(String permission, String[] arguments) {
		if (arguments.length == 0)
			return permission;

		StringBuilder sb = new StringBuilder();
		sb.append(permission);
		for (String arg : arguments) {
			sb.append(":");
			escape(sb, arg);
		}
		return sb.toString();
	}

	public void escape(StringBuilder sb, String arg) {
		for (int i = 0; i < arg.length(); i++) {
			char c = arg.charAt(i);
			if (c != '\\' && c != ':')
				sb.append(c);
			else {
				sb.append('\\');
				sb.append(c);
			}
		}
	}

	public User getUser(String key, String value) {
		return userAdmin.getUser(key, value);
	}

	public Set<User> getUsers() throws InvalidSyntaxException {
		return toUsers(getRoles());
	}

}
