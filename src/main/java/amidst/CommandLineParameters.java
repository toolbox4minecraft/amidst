package amidst;

import java.nio.file.Path;
import java.util.Optional;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

/**
 * An instance of this class will be created to hold the command line
 * parameters. Afterwards, the assigned values should not be modified.
 */
@ThreadSafe
public class CommandLineParameters {
	// @formatter:off
	@Option(
	    name = "-mcpath",
	    usage = "location of the '.minecraft' directory.",
	    metaVar = "<directory>"
	)
	public volatile Path dotMinecraftDirectory;

	@Option(
	    name = "-mcjar",
	    usage = "location of the minecraft jar file",
	    metaVar = "<file>",
	    depends = { "-mcjson" },
	    forbids = { "-profile" }
	)
	public volatile Path minecraftJarFile;

	@Option(
	    name = "-mcjson",
	    usage = "location of the minecraft json file",
	    metaVar = "<file>",
	    depends = { "-mcjar" },
	    forbids = { "-profile" }
	)
	public volatile Path minecraftJsonFile;

	@Option(
	    name = "-profile",
	    usage = "name of profile to select",
	    metaVar = "<name>",
	    forbids = { "-mcjar", "-mcjson" }
	)
    public volatile String profileName;

	@Option(
	    name = "-biome-profiles",
	    usage = "location of the biome profile directory",
	    metaVar = "<directory>"
	)
	public volatile Path biomeProfilesDirectory;

	@Option(
	    name = "-history",
	    usage = "location of the seed history file",
	    metaVar = "<file>"
	)
	public volatile Path seedHistoryFile;

	@Option(
	    name = "-log",
	    usage = "location of the log file",
	    metaVar = "<file>"
	)
	public volatile Path logFile;

	@Option(
	    name = "-seed",
	    handler = SeedHandler.class,
	    usage = "initial seed to use",
        metaVar = "<string>"
	)
	public volatile WorldSeed initialSeed;

	@Option(
	    name = "-world-type",
	    handler = WorldTypeHandler.class,
	    usage = "world type for the initial seed",
	    metaVar = "<string>",
	    depends = { "-seed" }
	)
	public volatile WorldType initialWorldType;

	@Option(
	    name = "-help",
	    usage = "print usage information"
	)
	public volatile boolean printHelp;

	@Option(
	    name = "-version",
	    usage = "print version"
	)
	public volatile boolean printVersion;
	// @formatter:on

	public Optional<WorldOptions> getInitialWorldOptions() {
	    if (initialSeed == null) {
	        return Optional.empty();
	    }
	    return Optional.of(new WorldOptions(initialSeed, initialWorldType != null ? initialWorldType : WorldType.DEFAULT));
	}

	public Optional<LauncherProfile> getInitialLauncherProfile(MinecraftInstallation minecraftInstallation) {
	    if (profileName != null) {
	        return minecraftInstallation.tryGetLauncherProfileFromName(profileName);
	    }
	    if (minecraftJarFile != null) {
	        return minecraftInstallation.tryReadLauncherProfile(minecraftJarFile, minecraftJsonFile);
	    }
	    return Optional.empty();
	}

	public static class SeedHandler extends OptionHandler<WorldSeed> {
	    public SeedHandler(CmdLineParser cmdLineParser, OptionDef optionDef, Setter<WorldSeed> setter) {
	        super(cmdLineParser, optionDef, setter);
	    }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            WorldSeed seed = WorldSeed.fromUserInput(params.getParameter(0));
            setter.addValue(seed);
            return 1;
        }

        @Override
        public String getDefaultMetaVariable() {
            return "STRING";
        }
	}

   public static class WorldTypeHandler extends OptionHandler<WorldType> {
        public WorldTypeHandler(CmdLineParser cmdLineParser, OptionDef optionDef, Setter<WorldType> setter) {
            super(cmdLineParser, optionDef, setter);
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            String param = params.getParameter(0);
            WorldType type = WorldType.findInstance(param);
            if(type == null) {
                throw new CmdLineException(owner, "Invalid WorldType: '" + param + "'", null);
            }
            setter.addValue(type);
            return 1;
        }

        @Override
        public String getDefaultMetaVariable() {
            return "STRING";
        }
    }
}
