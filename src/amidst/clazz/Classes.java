package amidst.clazz;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.real.RealClass;
import amidst.clazz.real.RealClasses;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicClasses;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.translator.ClassTranslator;
import amidst.logging.Log;

public class Classes {
	public static Map<String, SymbolicClass> createSymbolicClassMap(
			File jarFile, URLClassLoader classLoader, ClassTranslator translator) {
		Log.i("Reading " + jarFile.getName());
		List<RealClass> realClasses = RealClasses.fromJarFile(jarFile);
		Log.i("Jar load complete.");
		Log.i("Searching for classes...");
		Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration = translator
				.translate(realClasses);
		Log.i("Class search complete.");
		Log.i("Loading classes...");
		Map<String, SymbolicClass> result = SymbolicClasses.from(
				realClassNamesBySymbolicClassDeclaration, classLoader);
		Log.i("Classes loaded.");
		return result;
	}

	public static boolean ensureExactlyOneMatches(File jarFile,
			ClassTranslator translator) {
		Log.i("Reading " + jarFile.getName());
		List<RealClass> realClasses = RealClasses.fromJarFile(jarFile);
		Log.i("Jar load complete.");
		Log.i("Searching for classes...");
		Map<SymbolicClassDeclaration, List<RealClass>> map = translator
				.translateToAllMatching(realClasses);
		Log.i("Class search complete.");

		boolean result = true;
		for (Entry<SymbolicClassDeclaration, List<RealClass>> entry : map
				.entrySet()) {
			if (entry.getValue().isEmpty()) {
				Log.w(entry.getKey().getSymbolicClassName()
						+ " has no matching class");
				result = false;
			} else if (entry.getValue().size() != 1) {
				StringBuilder builder = new StringBuilder();
				for (RealClass realClass : entry.getValue()) {
					builder.append(", ").append(realClass.getRealClassName());
				}
				Log.w(entry.getKey().getSymbolicClassName()
						+ " has multiple matching classes: "
						+ builder.toString().substring(2));
				result = false;
			}
		}
		return result;
	}
}
