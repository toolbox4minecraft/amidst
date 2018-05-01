package amidst.mojangapi.minecraftinterface;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Map;

import amidst.clazz.Classes;
import amidst.clazz.real.JarFileParsingException;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.LauncherProfile;
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
			ClassTranslator translator = getClassTranslatorFromVersion(recognisedVersion);
			Map<String, SymbolicClass> symbolicClassMap = Classes
					.createSymbolicClassMap(launcherProfile.getJar(), classLoader, translator);
			MinecraftInterface minecraftInterface = fromSymbolicMapAndVersion(symbolicClassMap, recognisedVersion);
			
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
	
	private static ClassTranslator getClassTranslatorFromVersion(RecognisedVersion version) {
		return DefaultClassTranslator.INSTANCE.get();
	}
	
	private static MinecraftInterface fromSymbolicMapAndVersion(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion version) {
		return new LocalMinecraftInterface(symbolicClassMap, version);
	}	
}
