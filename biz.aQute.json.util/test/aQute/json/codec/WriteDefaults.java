package aQute.json.codec;

import org.junit.Test;

public class WriteDefaults {

	static class WithDefaults {
		public double d;
	}

	@Test
	public void testWriteDefaults() throws Exception {
		String string = new JSONCodec().enc().put(new WithDefaults()).toString();
		System.out.println(string);
	}
}
