package amidst.clazz;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLClassLoader;
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
import amidst.logging.Log;

@Immutable
public enum Classes {
	;

	public static Map<String, SymbolicClass> createSymbolicClassMap(
			File jarFile,
			URLClassLoader classLoader,
			ClassTranslator translator) throws FileNotFoundException, JarFileParsingException,
			SymbolicClassGraphCreationException, ClassNotFoundException {
		Log.i("Reading " + jarFile.getName());
		List<RealClass> realClasses = RealClasses.fromJarFile(jarFile);
		Log.i("Jar load complete.");
		Log.i("Searching for classes...");
		Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration = translator
				.translate(realClasses);
		Log.i("Class search complete.");
		Log.i("Loading classes...");
		Map<String, SymbolicClass> result = SymbolicClasses.from(realClassNamesBySymbolicClassDeclaration, classLoader);
		Log.i("Classes loaded.");
		return result;
	}

	public static Map<SymbolicClassDeclaration, Integer> countMatches(File jarFile, ClassTranslator translator)
			throws FileNotFoundException, JarFileParsingException {
		Log.i("Checking " + jarFile.getName());
		List<RealClass> realClasses = RealClasses.fromJarFile(jarFile);
		Map<SymbolicClassDeclaration, List<RealClass>> map = translator.translateToAllMatching(realClasses);
		Map<SymbolicClassDeclaration, Integer> result = new HashMap<SymbolicClassDeclaration, Integer>();
		for (Entry<SymbolicClassDeclaration, List<RealClass>> entry : map.entrySet()) {
			result.put(entry.getKey(), entry.getValue().size());
			if (entry.getValue().isEmpty()) {
				Log.w(entry.getKey().getSymbolicClassName() + " has no matching class");
			} else if (entry.getValue().size() > 1) {
				StringBuilder builder = new StringBuilder();
				for (RealClass realClass : entry.getValue()) {
					builder.append(", ").append(realClass.getRealClassName());
				}
				Log.w(entry.getKey().getSymbolicClassName() + " has multiple matching classes: "
						+ builder.toString().substring(2));
			}
		}
		return result;
	}
}
