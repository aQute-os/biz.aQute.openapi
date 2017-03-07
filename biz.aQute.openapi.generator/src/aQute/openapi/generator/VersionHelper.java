package aQute.openapi.generator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionHelper {
	public static String	DIGIT_S		= "\\d+";
	public static Pattern	VERSION_P	= Pattern.compile(																//
			"[^\\d]*(?<major>\\d+)((\\.(?<minor>\\d+))(\\.(?<icro>\\d+))?)?([.-](?<qualifier>.+))?", Pattern.COMMENTS);

	public final int		major;
	public final int		minor;
	public final int		micro;
	public final String		qualifier;

	public VersionHelper(int major, int minor, int micro, String qualifeir) {
		this.major = major;
		this.minor = minor;
		this.micro = micro;
		qualifier = qualifeir;

	}

	public VersionHelper(String version) {
		Matcher matcher = VERSION_P.matcher(version);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid version");
		}
		major = get(matcher.group("major"), 0);
		minor = get(matcher.group("minor"), 0);
		micro = get(matcher.group("major"), 0);
		qualifier = matcher.group("qualifier");
	}

	public VersionHelper getWithoutQualifier() {
		return new VersionHelper(major, minor, micro, null);
	}

	@Override
	public String toString() {
		return major + "." + minor + "." + micro + (qualifier == null ? "" : "." + qualifier);
	}

	private int get(String group, int i) {
		return group == null ? i : Integer.parseInt(group);
	}

	public String getFilter() {
		return String.format("(&(version>=%s.%s)(!(version>=%s.0)))", major, minor, major + 1);
	}
}
