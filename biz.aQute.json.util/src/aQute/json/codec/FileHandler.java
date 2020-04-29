package aQute.json.codec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Map;

import aQute.lib.base64.Base64;

public class FileHandler extends Handler {

	@Override
	public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
		File f = (File) object;
		if (!f.isFile())
			throw new RuntimeException("Encoding a file requires the file to exist and to be a normal file " + f);

		try(FileInputStream in = new FileInputStream(f)) {
			app.append('"');
			Base64.encode(in, app);
			app.append('"');
		}
	}

	@Override
	public Object decode(Decoder dec, String s) throws Exception {
		File tmp = File.createTempFile("json", ".bin");
		try (FileOutputStream fout = new FileOutputStream(tmp)) {
		
			Base64.decode(new StringReader(s), fout);
		}
		return tmp;
	}

}
