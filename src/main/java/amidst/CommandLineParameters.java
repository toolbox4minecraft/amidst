package amidst;

import org.kohsuke.args4j.Option;

import amidst.documentation.ThreadSafe;

/**
 * An instance of this class will be created to hold the command line
 * parameters. Afterwards, the assigned values should not be modified.
 */
@ThreadSafe
public class CommandLineParameters {
	// @formatter:off
	@Option(name = "-mcpath",                 usage = "location of the '.minecraft' directory.",             metaVar = "<directory>")
	public volatile String dotMinecraftDirectory;

	@Option(name = "-mcjar",                  usage = "location of the minecraft jar file",                  metaVar = "<file>",       depends = { "-mcjson" })
	public volatile String minecraftJarFile;
	
	@Option(name = "-mcjson",                 usage = "location of the minecraft json file",                 metaVar = "<file>",       depends = { "-mcjar" })
	public volatile String minecraftJsonFile;

	@Option(name = "-biome-profiles",         usage = "location of the biome profile directory",             metaVar = "<directory>")
	public volatile String biomeProfilesDirectory;

	@Option(name = "-history",                usage = "location of the seed history file",                   metaVar = "<file>")
	public volatile String seedHistoryFile;

	@Option(name = "-log",                    usage = "location of the log file",                            metaVar = "<file>")
	public volatile String logFile;
	
	@Option(name = "-help",                   usage = "print usage information")
	public volatile boolean printHelp;

	@Option(name = "-version",                usage = "print version")
	public volatile boolean printVersion;
	// @formatter:on
}
