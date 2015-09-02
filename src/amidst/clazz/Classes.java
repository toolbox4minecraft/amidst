package amidst.clazz;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

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
}
