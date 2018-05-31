package biz.aQute.openapi.security.dummy.provider;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/*
 * Example JUNit test case
 *
 */

public class ProviderImplTest {

	/*
	 * Example test method
	 */

	@Test
	public void simple() {
		SecurityProviderImpl impl = new SecurityProviderImpl();
		assertNotNull(impl);
	}

}
