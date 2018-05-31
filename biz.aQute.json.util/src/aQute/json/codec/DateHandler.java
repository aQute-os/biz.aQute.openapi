package aQute.json.codec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class DateHandler extends Handler {

	@Override
	public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
		Instant instant = Instant.ofEpochMilli(((Date) object).getTime());
		StringHandler.string(app, instant.toString());
	}

	@Override
	public Object decode(Decoder dec, String s) throws Exception {
		return new Date(Instant.parse(s).toEpochMilli());
	}

	@Override
	public Object decode(Decoder dec, Number s) throws Exception {
		return new Date(s.longValue());
	}

}
