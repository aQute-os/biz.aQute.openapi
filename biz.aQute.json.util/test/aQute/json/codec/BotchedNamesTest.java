package aQute.json.codec;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class BotchedNamesTest {
	final static JSONCodec codec = new JSONCodec();

	static public class BotchedNames {
		public String	new$;
		public String	if$;
		public List<String>	enum$;
	}

	@Test
	public void testFieldsWithReservedKeyWordName() throws Exception {
		BotchedNames s = new BotchedNames();
		s.new$ = "NEW";
		s.if$ = "IF";
		s.enum$ = Arrays.asList("ENUM");
		assertEquals("{\"enum\":[\"ENUM\"],\"if\":\"IF\",\"new\":\"NEW\"}", codec.enc().put(s).toString());

		BotchedNames botchedNames = codec.dec().from("{\"enum\":[\"ENUM\"],\"if\":\"IF\",\"new\":\"NEW\"}").get(BotchedNames.class);
		assertEquals("IF", botchedNames.if$);
		assertEquals("NEW", botchedNames.new$);
		assertEquals("ENUM", botchedNames.enum$.get(0));
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
