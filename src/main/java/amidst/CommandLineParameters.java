package amidst;

import org.kohsuke.args4j.Option;

import amidst.documentation.ThreadSafe;

/**
 * An instance of this class will be created to hold the command line
 * parameters. Afterwards, the assigned values should not be modified.
 */
@ThreadSafe
public class CommandLineParameters {
	@Option(name = "-history", usage = "Sets the path to seed history file.", metaVar = "<file>")
	public volatile String historyPath;

	@Option(name = "-log", usage = "Sets the path to logging file.", metaVar = "<file>")
	public volatile String logPath;

	@Option(name = "-mcpath", usage = "Sets the path to the .minecraft directory.", metaVar = "<path>")
	public volatile String minecraftPath;

	@Option(name = "-mcjar", usage = "Sets the path to the minecraft .jar", metaVar = "<path>")
	public volatile String minecraftJar;

	@Option(name = "-mcjson", usage = "Sets the path to the minecraft .json", metaVar = "<path>")
	public volatile String minecraftJson;

	@Option(name = "-mclibs", usage = "Sets the path to the libraries/ folder", metaVar = "<path>")
	public volatile String minecraftLibraries;
}
