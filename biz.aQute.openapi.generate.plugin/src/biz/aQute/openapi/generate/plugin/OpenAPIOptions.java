package biz.aQute.openapi.generate.plugin;
import java.io.File;
import java.util.List;

import aQute.bnd.service.generate.Options;

public interface OpenAPIOptions extends Options {

	String basename(String baseName);

	boolean beans();

	String[] conversions();

	String dateformat();

	String datetimeclass();

	String dateTimeformat();

	String dtotype(String dtoType);

	List<String> imports();

	String license();

	boolean mandatorytags();

	String packageprefix(String packagePrefix);

	boolean privatefields();

	File output();

	String[] tags();

	String typeprefix(String typePrefix);

	boolean uisupport();

	boolean versionsources();

	boolean autoname();
}
