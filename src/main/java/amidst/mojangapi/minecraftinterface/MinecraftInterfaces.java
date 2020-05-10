package amidst.mojangapi.minecraftinterface;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiFunction;

import amidst.clazz.Classes;
import amidst.clazz.real.JarFileParsingException;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.minecraftinterface.legacy.LegacyClassTranslator;
import amidst.mojangapi.minecraftinterface.legacy.LegacyMinecraftInterface;
import amidst.mojangapi.minecraftinterface.legacy._1_13ClassTranslator;
import amidst.mojangapi.minecraftinterface.legacy._1_13MinecraftInterface;
import amidst.mojangapi.minecraftinterface.local.DefaultClassTranslator;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterface;

public enum MinecraftInterfaces {
    ;

    @NotNull
    public static MinecraftInterface fromLocalProfile(LauncherProfile launcherProfile)
            throws MinecraftInterfaceCreationException {
        try {
            URLClassLoader classLoader = launcherProfile.newClassLoader();
            RecognisedVersion recognisedVersion = RecognisedVersion.from(classLoader);
            Factory factory = fromVersion(recognisedVersion);
            Map<String, SymbolicClass> symbolicClassMap = Classes
                    .createSymbolicClassMap(launcherProfile.getJar(), classLoader, factory.classTranslator);
            MinecraftInterface minecraftInterface = factory.factory.apply(symbolicClassMap, recognisedVersion);

            AmidstLogger.info("Minecraft load complete.");
            return minecraftInterface;
        } catch (
                ClassNotFoundException
                | JarFileParsingException
                | SymbolicClassGraphCreationException
                | IOException e) {
            throw new MinecraftInterfaceCreationException("unable to create local minecraft interface", e);
        }
    }
    
	/**
	 * A method used for creating an external MinecraftInterface.
	 * 
	 * @param classDir A path object pointing to a directory which has all of
	 * the class files required to create an external MinecraftInterface.
	 * This includes the classes ExternalMinecraftInterface and
	 * ExternalClassTranslator.
	 * 
	 * @return An instance of the provided ExternalMinecraftInterface.
	 * @throws Exception
	 */
	public static MinecraftInterface createExternalMinecraftInterface(LauncherProfile launcherProfile, Path classDir) throws Exception {
		URLClassLoader mcClassLoader = launcherProfile.newClassLoader();
		
		URLClassLoader interfaceClassLoader = URLClassLoader.newInstance(new URL[] { classDir.toUri().toURL() },
				MinecraftInterfaces.class.getClassLoader());
		ClassTranslator externalClassTranslator = (ClassTranslator) Class.forName("ExternalClassTranslator", true, interfaceClassLoader).getMethod("get").invoke(null);
		
		Map<String, SymbolicClass> symbolicClassMap = Classes
                .createSymbolicClassMap(launcherProfile.getJar(), mcClassLoader, externalClassTranslator);
		
		return (MinecraftInterface) Class.forName("ExternalMinecraftInterface", true, interfaceClassLoader).getConstructor(new Class<?>[] { Map.class }).newInstance(symbolicClassMap);
	}

    @NotNull
    public static ClassTranslator getClassTranslatorFromVersion(RecognisedVersion recognisedVersion) {
        return fromVersion(recognisedVersion).classTranslator;
    }

    @NotNull
    private static Factory fromVersion(RecognisedVersion version) {
        if(RecognisedVersion.isOlderOrEqualTo(version, LegacyMinecraftInterface.LAST_COMPATIBLE_VERSION)) {
            return new Factory(LegacyClassTranslator.get(), LegacyMinecraftInterface::new);
        } else if(RecognisedVersion.isOlderOrEqualTo(version, _1_13MinecraftInterface.LAST_COMPATIBLE_VERSION)) {
            return new Factory(_1_13ClassTranslator.get(), _1_13MinecraftInterface::new);
        } else {
            return new Factory(DefaultClassTranslator.get(), LocalMinecraftInterface::new);
        }
    }

    private static class Factory {
        public ClassTranslator classTranslator;
        public BiFunction<Map<String, SymbolicClass>, RecognisedVersion, MinecraftInterface> factory;

        public Factory(ClassTranslator classTranslator,
                BiFunction<Map<String, SymbolicClass>, RecognisedVersion, MinecraftInterface> factory) {
            this.classTranslator = classTranslator;
            this.factory = factory;
        }
    }
}
