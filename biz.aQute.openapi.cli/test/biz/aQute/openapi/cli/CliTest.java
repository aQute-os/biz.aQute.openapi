package biz.aQute.openapi.cli;

import org.junit.Test;

import aQute.openapi.cli.OpenAPICLI;

public class CliTest {

	@Test
	public void testCli() {
		OpenAPICLI.main(new String[] {
				"-b", "../biz.aQute.openapi.authenticate.example", "-et", "generate"
		});
	}
}
