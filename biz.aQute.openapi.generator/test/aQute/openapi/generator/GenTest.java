package aQute.openapi.generator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import aQute.lib.io.IO;

public class GenTest {
	File tmp = IO.getFile("test");

	@Before
	public void setUp() {
		tmp.mkdirs();
	}

	@Test
	public void testAll() throws Exception {
		File files = IO.getFile("test/aQute/openapi/generator/files");
		Configuration c = new Configuration();
		c.dateFormat = "ISO_DATE";
		c.dateTimeFormat = "YYYY-MM-dd";
		File output = IO.getFile("gen-sources");
		for (File f : files.listFiles()) {
			if (f.getName().endsWith(".json")) {
				System.out.println("______________________________________________________");
				System.out.println("        " + f);
				System.out.println("______________________________________________________");
				String pack = f.getName().substring(0, f.getName().length() - 5);
				c.packagePrefix = pack;
				OpenAPIGenerator g = new OpenAPIGenerator(f, c);
				g.report(System.out);
				g.generate(output);
			}
		}
	}

	@Test
	public void testOne() throws Exception {
		File file = IO.getFile("test/aQute/openapi/generator/files/de.sma.igana.rest.api.v1.json");
		Configuration c = new Configuration();
		File output = IO.getFile("target/gen-sources");
		String pack = file.getName().substring(0, file.getName().length() - 5);
		c.packagePrefix = pack;
		c.tags = new String[] {
				"AccessTokenApi:*", "*:OnlyOne", "TermsOfUse:!"
		};
		OpenAPIGenerator g = new OpenAPIGenerator(file, c);
		g.report(System.out);
		g.generate(output);
	}

	@Test
	public void testPetStore() throws Exception {
		File file = IO.getFile("test/aQute/openapi/generator/files/pet.store.json");
		Configuration c = new Configuration();
		File output = IO.getFile("target/gen-sources");
		String pack = file.getName().substring(0, file.getName().length() - 5);
		c.packagePrefix = pack;
		OpenAPIGenerator g = new OpenAPIGenerator(file, c);
		g.report(System.out);
		g.generate(output);
	}

	@Test
	public void testX() {
		Clock c = Clock.fixed(Instant.parse("2017-03-01T12:00:00Z"), ZoneId.of("CET"));
		testClock(c, "Zero ms");
		c = Clock.fixed(Instant.parse("2017-03-01T12:00:00.001Z"), ZoneId.of("CET"));
		testClock(c, "Non Zero ms");
	}

	private void testClock(Clock c, String title) {
		System.out.println("\n" + title + " " + c);

		OffsetDateTime od = OffsetDateTime.now(c);
		ZonedDateTime zd = ZonedDateTime.now(c);
		Instant instant = Instant.now(c);
		LocalDateTime ld = LocalDateTime.now(c);

		System.out.println("Default OffsetDateTime                                    " + od);
		System.out.println("Default ZonedDateTime                                     " + od);
		System.out.println("Default Instant                                           " + instant);
		System.out.println("Default Local Date Time                                   " + instant);
		System.out.println("ZonedDateTime as ISO_INSTANT                              "
				+ zd.format(DateTimeFormatter.ISO_INSTANT));
		System.out.println("OffsetDateTime as ISO_INSTANT                             "
				+ od.format(DateTimeFormatter.ISO_INSTANT));
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		System.out.println("OffsetDateTime with yyyy-MM-dd'T'HH:mm:ss.SSSXXX          " + od.format(df));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date date = new Date(c.instant().toEpochMilli());
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String format = dateFormat.format(date);
		System.out.println("SimpleDateFormat(UTC) with yyyy-MM-dd'T'HH:mm:ss.SSSXXX   " + format);
		
		DateTimeFormatter fi = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));
		System.out.println("ODT DateTimeFormat with yyyy-MM-dd'T'HH:mm:ss.SSSXXX      " + od.format(fi));
		System.out.println("ZDT DateTimeFormat with yyyy-MM-dd'T'HH:mm:ss.SSSXXX      " + zd.format(fi));
		System.out.println("LDT DateTimeFormat with yyyy-MM-dd'T'HH:mm:ss.SSSXXX      "
				+ ld.format(fi.withZone(ZoneId.of("UTC"))));
		System.out.println("Ins DateTimeFormat with yyyy-MM-dd'T'HH:mm:ss.SSSXXX      " + fi.format(instant));
		// System.out.println("Ins DateTimeFormat with
		// yyyy-MM-dd'T'HH:mm:ss.SSSXXX " + fi.format(instant));
	}
}
