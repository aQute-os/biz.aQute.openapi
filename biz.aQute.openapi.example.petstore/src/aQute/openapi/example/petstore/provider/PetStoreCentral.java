package aQute.openapi.example.petstore.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jdbc.DataSourceFactory;

@Component(service = PetStoreCentral.class)
public class PetStoreCentral {

	@Reference
	DataSourceFactory	jdbc;
	DataSource			ds;

	@Activate
	void activate() throws SQLException, IOException {
		try {
			Properties props = new Properties();
			props.put(DataSourceFactory.JDBC_DATABASE_NAME, "./generated/petstore");

			ds = jdbc.createDataSource(props);
			try (Connection connection = ds.getConnection();) {
				executBatch(connection, "schema.sql");
				executBatch(connection, "data.sql");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void executBatch(Connection connection, String name) throws IOException, SQLException {
		String schema = read(PetStoreCentral.class.getResourceAsStream(name));
		try (Statement statement = connection.createStatement()) {
			statement.addBatch(schema);
			int[] result = statement.executeBatch();
		}
	}

	private String read(InputStream in) throws IOException {
		try (InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);) {
			StringBuilder sb = new StringBuilder();
			int c;
			while ((c = r.read()) >= 0)
				sb.append((char) c);

			return sb.toString();
		}
	}
}
