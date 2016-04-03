package amidst.mojangapi.minecraftinterface.local;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.Map;

import amidst.clazz.Classes;
import amidst.clazz.real.JarFileParsingException;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.Log;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@Immutable
public class LocalMinecraftInterfaceBuilder {
	
	@NotNull
	private ClassTranslator getTranslator(RecognisedVersion recognisedVersion) {
		
		if (isEarlyBeta(recognisedVersion)) {
			return MinecraftClassTranslator_EarlyBetas.INSTANCE.get();
		} else {
			return MinecraftClassTranslator_Default.INSTANCE.get();
		}
	}
	
	@NotNull
	private MinecraftInterface createLocalMinecraftInterface_Default(
			Map<String, SymbolicClass> symbolicClassMap,
			RecognisedVersion recognisedVersion) {
		
		return new LocalMinecraftInterface_Default(
				symbolicClassMap.get(SymbolicNames.CLASS_INT_CACHE),
				symbolicClassMap.get(SymbolicNames.CLASS_BLOCK_INIT),
				symbolicClassMap.get(SymbolicNames.CLASS_GEN_LAYER),
				symbolicClassMap.get(SymbolicNames.CLASS_WORLD_TYPE),
				recognisedVersion);
	}

	@NotNull
	private MinecraftInterface createLocalMinecraftInterface_EarlyBetas(
			Map<String, SymbolicClass> symbolicClassMap,
			RecognisedVersion recognisedVersion) {
		
		return new LocalMinecraftInterface_EarlyBetas(
				symbolicClassMap.get(SymbolicNames.CLASS_BETA_WORLD),
				symbolicClassMap.get(SymbolicNames.CLASS_BETA_DIMENSION_CONCRETE), // Dimension class MUST be an instantiatable class
				symbolicClassMap.get(SymbolicNames.CLASS_BETA_BIOMEGENERATOR),
				symbolicClassMap.get(SymbolicNames.CLASS_BETA_BIOME),
				recognisedVersion);
	}
	
	public boolean isEarlyBeta(RecognisedVersion recognisedVersion) {
		return RecognisedVersion.isOlder(recognisedVersion, RecognisedVersion._b1_8_1);
	}
		
	@NotNull
	public MinecraftInterface create(VersionDirectory versionDirectory)
			throws LocalMinecraftInterfaceCreationException {
		try {
			URLClassLoader classLoader = versionDirectory.createClassLoader();
			RecognisedVersion recognisedVersion = RecognisedVersion
					.from(classLoader);
			Map<String, SymbolicClass> symbolicClassMap = Classes
					.createSymbolicClassMap(versionDirectory.getJar(),
							classLoader, getTranslator(recognisedVersion));
			Log.i("Minecraft load complete.");

			if (isEarlyBeta(recognisedVersion)) {
				return createLocalMinecraftInterface_EarlyBetas(symbolicClassMap, recognisedVersion);
			} else {			
				return createLocalMinecraftInterface_Default(symbolicClassMap, recognisedVersion);
			}
			
		} catch (MalformedURLException | ClassNotFoundException
				| FileNotFoundException | JarFileParsingException
				| SymbolicClassGraphCreationException e) {
			throw new LocalMinecraftInterfaceCreationException(
					"unable to create local minecraft interface", e);
		}
	}
}
