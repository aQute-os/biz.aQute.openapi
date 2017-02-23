package aQute.openapi.generator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.dto.DTO;

public class Configuration extends DTO {

	public String		typePrefix		= "Generated";
	public String		baseName		= "OpenAPIBase";
	public String		packagePrefix	= "org.example.openapi";
	public List<String>	importsExtra	= new ArrayList<>();
	public String		license;
	public String		dtoType			= "OpenAPIBase.DTO";
	public String[]		tags;
	public boolean		privateFields	= false;
	public boolean		beans			= false;
}
