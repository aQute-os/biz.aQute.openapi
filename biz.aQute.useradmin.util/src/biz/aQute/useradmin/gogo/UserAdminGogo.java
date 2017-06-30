package biz.aQute.useradmin.gogo;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.felix.service.command.Converter;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

import biz.aQute.useradmin.util.UserAdminFacade;
import osgi.enroute.debug.api.Debug;

/**
 * Gogo commands to handle a User Admin. Commands were designed to look like
 * Unix commands.
 */
@Component(property = { Debug.COMMAND_SCOPE + "=ua", //
		Debug.COMMAND_FUNCTION + "=useradd", //
		Debug.COMMAND_FUNCTION + "=userdel", //
		Debug.COMMAND_FUNCTION + "=groupadd", //
		Debug.COMMAND_FUNCTION + "=groupdel", //
		Debug.COMMAND_FUNCTION + "=groupmod", //
		Debug.COMMAND_FUNCTION + "=usermod", //
		Debug.COMMAND_FUNCTION + "=roledel", //
		Debug.COMMAND_FUNCTION + "=roles", //
		Debug.COMMAND_FUNCTION + "=role", //
		Debug.COMMAND_FUNCTION + "=addtogrp", //
		Debug.COMMAND_FUNCTION + "=rmfrmgrp", //
		Debug.COMMAND_FUNCTION + "=exports", //
		Debug.COMMAND_FUNCTION + "=imports", //
		Debug.COMMAND_FUNCTION + "=implies", //
})
public class UserAdminGogo implements Converter {

	Pattern			ROLE_NAME_P	= Pattern.compile("\\p{javaJavaIdentifierStart}[\\p{javaJavaIdentifierPart}\\-]*");

	@Reference
	UserAdminFacade	userAdmin;

	BundleContext	context;

	@Activate
	void activate(BundleContext context) {
		this.context = context;
	}

	@Descriptor("Create a new user and set the groups membership")
	public User useradd(
			@Parameter(absentValue = "false", presentValue = "true", names = { "-n",
					"--nocreategroups" }) boolean noCreateGroups,
			@Descriptor("The id of the user") String id,
			@Descriptor("Group names that this user should be added to, may contain wildcards for existing groups") String... groups)
			throws InvalidSyntaxException {

		User user = userAdmin.createUser(id);
		userAdmin.modify(user, noCreateGroups, false, userAdmin.toRoles(noCreateGroups, groups));
		return user;
	}

	@Descriptor("Create a new group and set the groups membership")
	public Group groupadd(
			@Parameter(absentValue = "false", presentValue = "true", names = { "-n",
					"--nocreategroups" }) boolean noCreateGroups,
			@Descriptor("The id of the user") String id,
			@Descriptor("Group names that this user should be added to, may contain wildcards for existing groups") String... groups)
			throws InvalidSyntaxException {
		Group group = userAdmin.createGroup(id);
		userAdmin.modify(group, noCreateGroups, false, userAdmin.toRoles(noCreateGroups, groups));
		return group;
	}

	@Descriptor("Modify a role's group membership by adding the given id to all the given groups. That is 'all g : groups | g.addMember[role]")
	public Role usermod(
			@Descriptor("Do not create groups when group name is not found") @Parameter(absentValue = "false", presentValue = "true", names = {
					"-n",
					"--nocreategroups" }) boolean noCreateGroups,
			@Descriptor("Remove the role from all groups before adding") @Parameter(absentValue = "false", presentValue = "true", names = {
					"-r", "--replace" }) boolean replace,
			@Descriptor("The role id") String id,
			@Descriptor("Can be wildcarded for existing groups. Group is created if does not exist") String... groups)
			throws InvalidSyntaxException {

		Role role = userAdmin.getRole(id);
		if (role == null)
			throw new IllegalArgumentException("No such role " + id);

		userAdmin.modify(role, noCreateGroups, replace, userAdmin.toRoles(noCreateGroups, groups));
		return role;
	}

	@Descriptor("Modify a role's group membership by adding the given id to all the given groups. That is 'all g : groups | g.addMember[role]")
	public Role groupmod(

			@Descriptor("Do not create groups when group name is not found") @Parameter(absentValue = "false", presentValue = "true", names = {
					"-n",
					"--nocreategroups" }) boolean noCreateGroups,

			@Descriptor("Remove the role from all groups before adding") @Parameter(absentValue = "false", presentValue = "true", names = {
					"-r", "--replace" }) boolean replace,

			@Descriptor("The role id") String id,
			@Descriptor("Can be wildcarded for existing groups. Group is created if does not exist") String... groups)
			throws InvalidSyntaxException {
		return usermod(noCreateGroups, replace, id, groups);
	}

	@Descriptor("Delete one or more groups")
	public Set<Group> groupdel(String... ids) throws InvalidSyntaxException {
		Set<Role> roles = userAdmin.toRoles(false, ids);
		Set<Group> groups = userAdmin.toGroups(roles);
		userAdmin.remove(groups);
		return groups;
	}

	@Descriptor("Delete a user")
	public void userdel(String... ids) throws InvalidSyntaxException {
		Set<Role> roles = userAdmin.toRoles(false, ids);
		Set<User> users = userAdmin.toUsers(roles);
		userAdmin.remove(users);
	}

	@Descriptor("Delete a role")
	public Set<Role> roledel(String... ids) throws InvalidSyntaxException {
		Set<Role> roles = userAdmin.toRoles(false, ids);
		userAdmin.remove(roles);
		return roles;
	}

	@Descriptor("Show all roles")
	public Collection<Role> roles() throws InvalidSyntaxException {
		return userAdmin.getRoles();
	}

	@Descriptor("Show all roles filtered")
	public Collection<Role> roles(String filter) throws InvalidSyntaxException {
		return userAdmin.getRoles(filter);
	}

	@Descriptor("Show a role by id")
	public Role role(String id) {
		return userAdmin.getRole(id);
	}

	@Descriptor("Add roles to group")
	public Group addtogrp(
			@Parameter(absentValue = "false", presentValue = "true", names = { "-n",
					"--nocreategroups" }) boolean noCreateGroups,
			@Descriptor("Clear the members of the group") @Parameter(absentValue = "false", presentValue = "true", names = {
					"-r", "--replace" }) boolean replace,
			String group, String... members) throws InvalidSyntaxException {
		Group g = userAdmin.getGroup(group);
		if (g == null)
			throw new IllegalArgumentException("No such group " + group);

		if (replace)
			userAdmin.clear(g);

		userAdmin.addToGroup(g, userAdmin.toRoles(noCreateGroups, members));
		return g;
	}

	@Descriptor("Remove members from group")
	public Group rmfrmgrp(
			@Descriptor("Group name") String group,
			@Descriptor("Members to remove, can use wildcards") String... members) throws InvalidSyntaxException {
		Group g = userAdmin.getGroup(group);
		if (g == null)
			throw new IllegalArgumentException("No such group");

		Role[] roles = g.getMembers();
		if (roles == null)
			return g;

		for (Role member : userAdmin.toRoles(false, members)) {
			g.removeMember(member);
		}
		return g;
	}

	@Descriptor("Export the current settings to a JSON file")
	public String exports() throws Exception {
		return userAdmin.exports();
	}

	@Descriptor("Import the current settings from a file path, a URI, or clipboard (use 'clip'). The format of the file must be JSON")
	public Set<Role> imports(String uri) throws Exception {
		String text = read(uri);

		return userAdmin.imports(text);
	}

	private String read(String uri) {
		if ("clip".equals(uri)) {
			try {
				return (String) Toolkit.getDefaultToolkit()
						.getSystemClipboard().getData(DataFlavor.stringFlavor);
			} catch (Exception e) {

			}
		}
		File f = new File(uri);
		if (f.isFile())
			try {
				return read(new FileInputStream(f));
			} catch (Exception e) {

			}
		try {
			URL url = new URL(uri);
			return read(url.openStream());
		} catch (Exception e) {
			// ignore
		}
		throw new IllegalArgumentException("Don't know how to read " + uri);
	}

	private String read(InputStream in) {
		return null;
	}

	@Descriptor("Check if the given user has the permission for the given action")
	public boolean implies(@Descriptor("The user") User user, @Descriptor("The requested action") String action) {
		return userAdmin.implies(user, action);
	}

	@Override
	public Object convert(Class<?> destType, Object sourceType) throws Exception {
		if (Role.class.isAssignableFrom(destType) && sourceType instanceof String) {
			try {
				return destType.cast(userAdmin.getRole((String) sourceType));
			} catch (Exception e) {
				// ignore if fails
			}
		}
		return null;
	}

	@Override
	public CharSequence format(Object role, int type, Converter converter) throws Exception {
		if (role instanceof Role) {
			Role r = (Role) role;
			switch (type) {
			case Converter.INSPECT:
				if (r instanceof Group)
					return userAdmin.inspect((Group) r);
				else if (r instanceof User)
					return userAdmin.inspect((User) r);
				else
					return r.toString();

			case Converter.LINE:
				if (r instanceof Group)
					return userAdmin.line((Group) r);
				else if (r instanceof User)
					return userAdmin.line((User) r);
				else
					return "# " + r.toString();

			case Converter.PART:
				String s = userAdmin.exists(r) ? "  " : "✝ ";
				return s + r.getName();
			}
		} else if (role instanceof Role[]) {
			return converter.format(Arrays.asList((Role[]) role), type, converter);
		}
		return null;
	}
}
