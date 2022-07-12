package aQute.openapi.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.dto.DTO;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.lib.env.Env;

public class Configuration extends DTO {

	public static final String OPENAPI = "-openapi";

	public enum Options {
		NOREQUIREMENT, NOVALIDATION
	}

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
	public boolean		tagsMustBeSet	= false;
	public Set<Options>	options			= new HashSet<>();

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
			attrs.remove("typePrefix");
			attrs.remove("baseName");
			attrs.remove("packagePrefix");

			if (attrs.containsKey("importsExtra")) {
				for (String part : split(attrs.remove("importsExtra"))) {
					c.importsExtra.add(part);
				}
			}

			c.license = attrs.getOrDefault("license", c.license);
			c.dtoType = attrs.getOrDefault("dtoType", c.dtoType);
			attrs.remove("license");
			attrs.remove("dtoType");

			if (attrs.containsKey("tags")) {
				c.tags = split(attrs.remove("tags"));
			}

			c.privateFields = truthy(attrs.remove("privateFields"));
			c.beans = truthy(attrs.remove("beans"));

			c.dateTimeFormat = attrs.getOrDefault("dateTimeFormat", c.dateTimeFormat);
			c.dateTimeClass = attrs.getOrDefault("dateTimeClass", c.dateTimeClass);
			attrs.remove("dateTimeFormat");
			attrs.remove("dateTimeClass");

			if (attrs.containsKey("conversions")) {
				c.conversions = split(attrs.remove("conversions"));
			}

			c.tagsMustBeSet = truthy(attrs.remove("tagsMustBeSet"));
			c.uisupport = truthy(attrs.remove("uisupport"));
			c.versionSources = truthy(attrs.remove("versionSources"));

			attrs.keySet().forEach(c::option);
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

	public void option(String s) {
		if (s == null)
			return;

		s = s.toUpperCase();
		Options opt = Options.valueOf(s);
		options.add(opt);
	}
}
