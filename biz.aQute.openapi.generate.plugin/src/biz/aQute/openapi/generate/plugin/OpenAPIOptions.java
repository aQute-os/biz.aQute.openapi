package biz.aQute.openapi.generate.plugin;
import java.io.File;
import java.util.List;

import aQute.bnd.service.generate.Options;

public interface OpenAPIOptions extends Options {

	String basename(String baseName);

	boolean beans(boolean beans);

	String[] conversions(String[] conversions);

	String dateformat(String dateFormat);

	String datetimeclass(String dateTimeClass);

	String dateTimeformat(String dateTimeFormat);

	String dtotype(String dtoType);

	List<String> imports();

	String license();

	boolean mandatorytags(boolean tagsMustBeSet);

	String packageprefix(String packagePrefix);

	boolean privatefields(boolean privateFields);

	File output();

	String[] tags(String[] tags);

	String typeprefix(String typePrefix);

	boolean uisupport(boolean uisupport);

	boolean versionsources(boolean versionSources);

	boolean autoname();
}
