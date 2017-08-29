package aQute.openapi.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.osgi.dto.DTO;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.lib.env.Env;

public class Configuration extends DTO {

	public static final String	OPENAPI			= "-openapi";

	public String		typePrefix		= "Generated";
	public String		baseName		= "OpenAPIBase";
	public String		packagePrefix	= "org.example.openapi";
	public List<String>	importsExtra	= new ArrayList<>();
	public String		license;
	public String		dtoType			= "OpenAPIBase.DTO";
	public String[]		tags;
	public boolean		privateFields	= false;
	public boolean		beans			= false;
	public String		dateFormat;
	public String		dateTimeFormat;
	public String		dateTimeClass;
	public String[]		conversions;
	public String		openapiFile		= "openapi.json";
	public boolean		uisupport		= false;
	public boolean		versionSources	= true;

	public static List<Configuration> from(Env env) {
		List<Configuration> result = new ArrayList<>();

		Parameters parameters = OSGiHeader.parseHeader(env.getProperty(OPENAPI));
		System.out.println(parameters);
		for (Entry<String,Attrs> e : parameters.entrySet()) {

			Configuration c = new Configuration();
			c.openapiFile = e.getKey();

			Attrs attrs = e.getValue();

			c.typePrefix = attrs.getOrDefault("typePrefix", c.typePrefix);
			c.baseName = attrs.getOrDefault("baseName", c.baseName);
			c.packagePrefix = attrs.getOrDefault("packagePrefix", c.packagePrefix);

			if (attrs.containsKey("importsExtra")) {
				for (String part : split(attrs.get("importsExtra"))) {
					c.importsExtra.add(part);
				}
			}

			c.license = attrs.getOrDefault("license", c.license);
			c.dtoType = attrs.getOrDefault("dtoType", c.dtoType);

			if (attrs.containsKey("tags")) {
				c.tags = split(attrs.get("tags"));
			}

			c.privateFields = truthy(attrs.get("privateFields"));
			c.beans = truthy(attrs.get("beans"));

			c.dateTimeFormat = attrs.getOrDefault("dateTimeFormat", c.dateTimeFormat);
			c.dateTimeClass = attrs.getOrDefault("dateTimeClass", c.dateTimeClass);

			if (attrs.containsKey("conversions")) {
				c.conversions = split(attrs.get("conversions"));
			}

			c.uisupport = truthy(attrs.get("privateFields"));
			c.versionSources = truthy(attrs.get("dateTimeClass"));

			result.add(c);
		}
		return result;
	}

	private static boolean truthy(String string) {
		if (string == null || string.equalsIgnoreCase("false") || string.equalsIgnoreCase("off")
				|| string.equalsIgnoreCase("0") || string.isEmpty())
			return false;
		return true;
	}

	private static String[] split(String string) {
		if (string == null)
			return new String[0];

		return string.split("\\s*,\\s*");
	}
}
