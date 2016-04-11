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
	private final ClassTranslator translator;

	public LocalMinecraftInterfaceBuilder(ClassTranslator translator) {
		this.translator = translator;
	}

	@NotNull
	public MinecraftInterface create(VersionDirectory versionDirectory) throws LocalMinecraftInterfaceCreationException {
		try {
			URLClassLoader classLoader = versionDirectory.createClassLoader();
			RecognisedVersion recognisedVersion = RecognisedVersion.from(classLoader);
			Map<String, SymbolicClass> symbolicClassMap = Classes.createSymbolicClassMap(
					versionDirectory.getJar(),
					classLoader,
					translator);
			Log.i("Minecraft load complete.");
			return new LocalMinecraftInterface(
					symbolicClassMap.get(SymbolicNames.CLASS_INT_CACHE),
					symbolicClassMap.get(SymbolicNames.CLASS_BLOCK_INIT),
					symbolicClassMap.get(SymbolicNames.CLASS_GEN_LAYER),
					symbolicClassMap.get(SymbolicNames.CLASS_WORLD_TYPE),
					recognisedVersion);
		} catch (MalformedURLException | ClassNotFoundException | FileNotFoundException | JarFileParsingException
				| SymbolicClassGraphCreationException e) {
			throw new LocalMinecraftInterfaceCreationException("unable to create local minecraft interface", e);
		}
	}
}
