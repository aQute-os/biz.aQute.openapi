package biz.aQute.openapi.runtime.test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import gen.defaultdatetime.dates.DefaultdatetimeDates;
import gen.manualconversion.dates.ManualconversionDates;
import gen.modifieddatetime.ModifieddatetimeBase;
import gen.modifieddatetime.dates.ModifieddatetimeDates;


public class DateTimeEncodingsTest extends Assert
{

   @Rule
   public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

   @Test
   public void testDefaultDateTimeEncoding() throws Exception
   {
      class X extends DefaultdatetimeDates
      {

         @Override
         protected Dates putDates(Dates token) throws Exception
         {
            System.out.println("Date         " + token.date);
            System.out.println("DateTime     " + token.dateTime);

            token.error = "ok\n";
            if ( ! token.date.equals( LocalDate.of(1970,1,1)))
               token.error += "Invalid date\n";
            if ( ! token.dateTime.equals( Instant.ofEpochMilli(0)))
               token.error += "Invalid date-time "+token.dateTime+"\n";
            return token;
         }

      }
      ;
      rule.add(new X());

      String offset = rule.put("/v1/dates", "{'date':'1970-01-01', 'dateTime':'1970-01-01T00:00:00Z' }");
      assertEquals("{'date':'1970-01-01','dateTime':'1970-01-01T00:00:00Z','error':'ok\\n'}", offset);

      String minus0 = rule.put("/v1/dates", "{'date':'1970-01-01', 'dateTime':'1970-01-01T00:00:00Z' }");
      assertEquals("{'date':'1970-01-01','dateTime':'1970-01-01T00:00:00Z','error':'ok\\n'}", minus0);

      String zulu = rule.put("/v1/dates", "{'date':'1970-01-01', 'dateTime':'1970-01-01T00:00:00Z' }");
      assertEquals("{'date':'1970-01-01','dateTime':'1970-01-01T00:00:00Z','error':'ok\\n'}", zulu);

   }

   @Test
   public void testSpecialEncodingForDates() throws Exception
   {
      class X extends ModifieddatetimeDates
      {

         @Override
         protected ModifieddatetimeDates.Dates putDates(ModifieddatetimeDates.Dates token) throws Exception
         {
            System.out.println("Date         " + token.date);
            System.out.println("DateTime     " + token.dateTime);

            token.error = "ok\n";
            if ( ! token.date.equals( LocalDate.of(1970,1,1)))
               token.error += "Invalid date\n";
            if ( ! token.dateTime.equals( Instant.ofEpochMilli(0)))
               token.error += "Invalid date-time "+token.dateTime+"\n";
            return token;
         }

      }
      ;
      rule.add(new X());

      String offset = rule.put("/v1/dates", "{'date':'1970-001', 'dateTime':'1970-01-01T00:00:00.000Z' }");
      assertEquals("{'date':'1970-001','dateTime':'1970-01-01T00:00:00.000Z','error':'ok\\n'}", offset);

      String minus0 = rule.put("/v1/dates", "{'date':'1970-001', 'dateTime':'1970-01-01T00:00:00.000Z' }");
      assertEquals("{'date':'1970-001','dateTime':'1970-01-01T00:00:00.000Z','error':'ok\\n'}", minus0);

      String zulu = rule.put("/v1/dates", "{'date':'1970-001', 'dateTime':'1970-01-01T00:00:00.000Z' }");
      assertEquals("{'date':'1970-001','dateTime':'1970-01-01T00:00:00.000Z','error':'ok\\n'}", zulu);

   }

   @Test
   public void testManualEncodingWithInstant() throws Exception
   {
      class X extends ManualconversionDates
      {

         @Override
         protected ManualconversionDates.Dates putDates(ManualconversionDates.Dates token) throws Exception
         {
            System.out.println("Date         " + token.date);
            System.out.println("DateTime     " + token.dateTime);

            token.error = "ok\n";
            if ( ! token.date.equals( LocalDate.of(1970,1,1)))
               token.error += "Invalid date\n";
            if ( ! token.dateTime.equals( Instant.ofEpochMilli(0)))
               token.error += "Invalid date-time "+token.dateTime+"\n";
            return token;
         }


      }
      ;
      rule.add(new X());

      String offset = rule.put("/v1/dates", "{'date':'1970-01-01', 'dateTime':'1970-01-01T00:00:00Z' }");
      assertEquals("{'date':'1970-01-01','dateTime':'1970-01-01T00:00:00.000Z','error':'ok\\n'}", offset);

      String minus0 = rule.put("/v1/dates", "{'date':'1970-01-01', 'dateTime':'1970-01-01T00:00:00Z' }");
      assertEquals("{'date':'1970-01-01','dateTime':'1970-01-01T00:00:00.000Z','error':'ok\\n'}", minus0);

      String zulu = rule.put("/v1/dates", "{'date':'1970-01-01', 'dateTime':'1970-01-01T00:00:00Z' }");
      assertEquals("{'date':'1970-01-01','dateTime':'1970-01-01T00:00:00.000Z','error':'ok\\n'}", zulu);

   }


   @Test
   public void testDateConversionMethod_YYYY_DDD() {
	   LocalDate date = ModifieddatetimeBase.toDate("1972-001");
	   assertEquals( "1972-01-01", date.toString());
	   assertEquals( "1972-001", ModifieddatetimeBase.fromDate(date));

   }

   @Test
   public void testDateTimeConversionMethod_yyyy_MM_dd_T_HH_mm_ss__SSS_X() {
	   Instant dateTime = ModifieddatetimeBase.toDateTime("1972-01-01T02:03:04.045Z");
	   assertEquals( "1972-01-01T02:03:04.045Z", dateTime.toString());

	   dateTime = ModifieddatetimeBase.toDateTime("1972-01-01T02:03:04Z");
	   assertEquals( "1972-01-01T02:03:04Z", dateTime.toString());

	   assertEquals("1972-01-01T02:03:04.000Z", ModifieddatetimeBase.fromDateTime(dateTime));

   }


}
