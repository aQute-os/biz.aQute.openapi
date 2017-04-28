package aQute.json.codec;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BotchedNamesTest {
	final static JSONCodec codec = new JSONCodec();

	static public class BotchedNames {
		public String	new$;
		public String	if$;
	}

	@Test
	public void testFieldsWithReservedKeyWordName() throws Exception {
		BotchedNames s = new BotchedNames();
		s.new$ = "NEW";
		s.if$ = "IF";
		assertEquals("{\"if\":\"IF\",\"new\":\"NEW\"}", codec.enc().put(s).toString());

		BotchedNames botchedNames = codec.dec().from("{\"if\":\"IF\",\"new\":\"NEW\"}").get(BotchedNames.class);
		assertEquals("IF", botchedNames.if$);
		assertEquals("NEW", botchedNames.new$);
	}

	static public class RenamedFields {
		public String a$2Db;
	}

	@Test
	public void testFieldsWithReservedCharacters() throws Exception {
		RenamedFields s = new RenamedFields();
		s.a$2Db = "A-B";
		assertEquals("{\"a-b\":\"A-B\"}", codec.enc().put(s).toString());
	}

}
