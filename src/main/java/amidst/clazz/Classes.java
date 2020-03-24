package amidst.clazz;

import java.io.FileNotFoundException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.real.JarFileParsingException;
import amidst.clazz.real.RealClass;
import amidst.clazz.real.RealClasses;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.clazz.symbolic.SymbolicClasses;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;

@Immutable
public enum Classes {
	;

	public static Map<String, SymbolicClass> createSymbolicClassMap(
			Path path,
			URLClassLoader classLoader,
			ClassTranslator translator)
			throws FileNotFoundException,
			JarFileParsingException,
			SymbolicClassGraphCreationException,
			ClassNotFoundException {
		AmidstLogger.info("Reading {}", path.getFileName());
		List<RealClass> realClasses = RealClasses.fromJarFile(path);
		AmidstLogger.info("Jar load complete.");
		AmidstLogger.info("Searching for classes...");
		Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration = translator
				.translate(realClasses);
		AmidstLogger.info("Class search complete.");
		AmidstLogger.info("Loading classes...");
		Map<String, SymbolicClass> result = SymbolicClasses.from(realClassNamesBySymbolicClassDeclaration, classLoader);
		AmidstLogger.info("Classes loaded.");
		return result;
	}

	public static Map<SymbolicClassDeclaration, Integer> countMatches(Path jarFile, ClassTranslator translator)
			throws FileNotFoundException,
			JarFileParsingException {
		AmidstLogger.info("Checking {}", jarFile.getFileName());
		List<RealClass> realClasses = RealClasses.fromJarFile(jarFile);
		Map<SymbolicClassDeclaration, List<RealClass>> map = translator.translateToAllMatching(realClasses);
		Map<SymbolicClassDeclaration, Integer> result = new HashMap<>();
		for (Entry<SymbolicClassDeclaration, List<RealClass>> entry : map.entrySet()) {
			result.put(entry.getKey(), entry.getValue().size());
			if (entry.getValue().isEmpty()) {
				AmidstLogger.warn("{} has no matching class", entry.getKey().getSymbolicClassName());
			} else if (entry.getValue().size() > 1) {
				StringBuilder builder = new StringBuilder();
				for (RealClass realClass : entry.getValue()) {
					builder.append(", ").append(realClass.getRealClassName());
				}
				AmidstLogger.warn("{} has multiple matching classes: {}",
						entry.getKey().getSymbolicClassName(), builder.toString().substring(2));
			}
		}
		return result;
	}
}
