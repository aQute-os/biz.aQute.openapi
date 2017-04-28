package aQute.json.codec;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Assert;
import org.junit.Test;

import aQute.json.codec.JSONCodec;

public class DateTest extends Assert {

	public static class WithDate {
		public LocalDate		date;
		public LocalDateTime	time;
		public OffsetDateTime	offset;
		public ZonedDateTime	zoned;
		public Instant			instant;
	}

	public static String sample = "{ 'date':'2017-02-22', 'time':'1985-04-12T23:20:50', 'offset':'1985-04-12T23:20:50Z', 'zoned':'1985-04-12T23:20:50+01:00', 'instant':145678123124 }";

	@Test
	public void testDateDecode() throws Exception {
		WithDate d = new JSONCodec().dec().from(sample.replace('\'', '"')).get(WithDate.class);
		assertEquals(LocalDate.of(2017, 2, 22), d.date);
		assertEquals(LocalDateTime.of(1985, 4, 12, 23, 20, 50, 0), d.time);
		assertEquals(OffsetDateTime.of(1985, 4, 12, 23, 20, 50, 0, ZoneOffset.UTC), d.offset);
		assertEquals(ZonedDateTime.of(1985, 4, 12, 23, 20, 50, 0, ZoneOffset.ofHours(1)), d.zoned);
		assertEquals(Instant.ofEpochMilli(145678123124L), d.instant);
	}

	@Test
	public void testDateEncode() throws Exception {
		WithDate d = new WithDate();
		d.date = LocalDate.of(2017, 2, 22);
		d.time = LocalDateTime.of(1985, 4, 12, 23, 20, 50, 0);
		d.offset = OffsetDateTime.of(1985, 4, 12, 23, 20, 50, 0, ZoneOffset.UTC);
		d.zoned = ZonedDateTime.of(1985, 4, 12, 23, 20, 50, 0, ZoneOffset.ofHours(1));
		d.instant = Instant.ofEpochMilli(145678123124L);
		String string = new JSONCodec().enc().put(d).toString();
		assertEquals("{\"date\":\"2017-02-22\",\"instant\":\"1974-08-14T02:08:43.124Z\",\"offset\":\"1985-04-12T23:20:50Z\",\"time\":\"1985-04-12T23:20:50\",\"zoned\":\"1985-04-12T23:20:50+01:00\"}", string);
	}
}
