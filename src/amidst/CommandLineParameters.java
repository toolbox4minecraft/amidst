package amidst;

import org.kohsuke.args4j.Option;

public class CommandLineParameters {
	@Option(name = "-history", usage = "Sets the path to seed history file.", metaVar = "<file>")
	public String historyPath;

	@Option(name = "-log", usage = "Sets the path to logging file.", metaVar = "<file>")
	public String logPath;

	@Option(name = "-mcpath", usage = "Sets the path to the .minecraft directory.", metaVar = "<path>")
	public String minecraftPath;

	@Option(name = "-mcjar", usage = "Sets the path to the minecraft .jar", metaVar = "<path>")
	public String minecraftJar;

	@Option(name = "-mcjson", usage = "Sets the path to the minecraft .json", metaVar = "<path>")
	public String minecraftJson;

	@Option(name = "-mclibs", usage = "Sets the path to the libraries/ folder", metaVar = "<path>")
	public String minecraftLibraries;
}
