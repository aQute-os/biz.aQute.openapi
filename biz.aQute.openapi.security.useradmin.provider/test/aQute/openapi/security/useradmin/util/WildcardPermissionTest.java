package aQute.openapi.security.useradmin.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

@SuppressWarnings("deprecation")
public class WildcardPermissionTest {

	@Test
	public void testWildcardPermissionExact() {
		assertThat(WildcardPermission.caseSensitive("abc").implies("abc"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:def").implies("abc:def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:def:ghi").implies("abc:def:ghi"), equalTo(true));
	}

	@Test
	public void testCaseInsensitive() {
		assertThat(WildcardPermission.caseSensitive("abc").implies("ABC"), equalTo(false));
		assertThat(WildcardPermission.ignoreCase("abc").implies("ABC"), equalTo(true));
		assertThat(WildcardPermission.ignoreCase("ABC").implies("ABC"), equalTo(true));
		assertThat(WildcardPermission.ignoreCase("ABC").implies("abc"), equalTo(true));
		assertThat(WildcardPermission.ignoreCase("a\u004bc").implies("A\u006bC"), equalTo(true));
		assertThat(WildcardPermission.ignoreCase("a\u006bc").implies("A\u004bC"), equalTo(true));
		assertThat(WildcardPermission.ignoreCase("a\u1e60c").implies("A\u1e61C"), equalTo(true));
	}

	@Test
	public void testWildcardPermissionWildcards() {
		assertThat(WildcardPermission.caseSensitive("*").implies("abc"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("*:def").implies("abc"), equalTo(false));
		assertThat(WildcardPermission.caseSensitive("*:*").implies("abc:def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("*:*:*").implies("abc:def:ghi"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:*").implies("abc:def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:*:ghi").implies("abc:def:ghi"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("*:*:ghi:jkl:*").implies("abc:def:ghi:jkl"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("*:def:*:jkl:*").implies("abc:def:ghi:jkl"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("*:*:*:jkl:*").implies("abc:def:ghi:jkl"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("*:*:*:*:*").implies("abc:def:ghi:jkl"), equalTo(true));
	}

	@Test
	public void testWildcardPermissionWildcardsWithShorterTarget() {
		assertThat(WildcardPermission.caseSensitive("*:*").implies("abc"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:*").implies("abc"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:*:foo").implies("abc"), equalTo(false));
	}

	@Test
	public void testDoubleWildcard() {
		assertThat(WildcardPermission.caseSensitive("**").implies("abc"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("**").implies("abc:def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("**").implies("abc:def:ghi"), equalTo(true));

		assertThat(WildcardPermission.caseSensitive("abc:**").implies("abc:"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:**").implies("abc:def:ghi"), equalTo(true));
	}

	@Test
	public void testWildcardPermissionWithOptions() {
		assertThat(WildcardPermission.caseSensitive("abc,def,ghi").implies("def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc,def,ghi").implies("jkl"), equalTo(false));
		assertThat(WildcardPermission.caseSensitive("abc:def,ghi").implies("abc:def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:def,ghi").implies("abc:jkl"), equalTo(false));
		assertThat(WildcardPermission.caseSensitive("abc:def,ghi").implies("abc:ghi"), equalTo(true));

		assertThat(WildcardPermission.caseSensitive("abc:def,ghi:jkl").implies("abc:ghi:jkl"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:def,ghi:jkl").implies("abc:mno:jkl"), equalTo(false));
		assertThat(WildcardPermission.caseSensitive("abc:def,ghi:jkl").implies("abc:def:jkl"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("*:def,ghi:*").implies("abc:def:jkl"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("*:def,ghi,jkl,mno:*").implies("abc:def:jkl"), equalTo(true));
	}

	@Test
	public void testEscaping() {
		assertThat(WildcardPermission.caseSensitive("abc\\,def").implies("abc,def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc\\*def").implies("abc*def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc\\:def").implies("abc:def"), equalTo(true));
		assertThat(WildcardPermission.caseSensitive("abc:def\\::foo").implies("abc:def\\::foo"), equalTo(true));
	}

	@Test
	public void testWildcardValidity() {
		assertThat(WildcardPermission.isValid("a*c"), equalTo("A wildcard must not be preceded with TEXT"));
		assertThat(WildcardPermission.isValid("a\\"), equalTo("Ends with a single backslash"));
		assertThat(WildcardPermission.isValid("abc:"), equalTo("Last part is empty"));
		assertThat(WildcardPermission.isValid("abc:*,ghi"),
				equalTo("A wildcard must be followed by a ':' or end of string"));
		assertThat(WildcardPermission.isValid("abc::ghi"), equalTo("Empty part"));
		assertThat(WildcardPermission.isValid("**:ghi"), equalTo("A double wildcard must be the last part"));
		assertThat(WildcardPermission.isValid("**b"), equalTo("A wildcard must not be followed by text"));
		assertThat(WildcardPermission.isValid("*b"), equalTo("A wildcard must not be followed by text"));

	}

	protected List<Set<String>> parts(String wildcardString) {

		if (wildcardString == null || wildcardString.isEmpty()) {
			throw new IllegalArgumentException(
					"Wildcard string cannot be null or empty. Make sure permission strings are properly formatted.");
		}

		String[] partsx = wildcardString.split(":");

		List<Set<String>> parts = new ArrayList<Set<String>>();

		for (String part : partsx) {
			Set<String> subparts = new HashSet<>(Arrays.asList(part.split(",")));

			if (subparts.isEmpty()) {
				throw new IllegalArgumentException(
						"Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted.");
			}
			parts.add(subparts);
		}

		if (parts.isEmpty()) {
			throw new IllegalArgumentException(
					"Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
		}
		return parts;
	}

	@Test
	public void testShiro() {
		System.out.println(parts("abc:def"));
		System.out.println(parts("abc*:def"));
		System.out.println(parts("abc,*,def"));
		System.out.println(parts("abc:"));
		System.out.println(parts("abc:::::"));
	}
}
