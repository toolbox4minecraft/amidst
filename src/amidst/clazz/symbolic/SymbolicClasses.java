package amidst.clazz.symbolic;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

import amidst.clazz.finder.ClassFinder;
import amidst.clazz.real.RealClass;
import amidst.clazz.real.RealClasses;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.logging.Log;

public class SymbolicClasses {
	public static Map<String, SymbolicClass> createClasses(
			ClassLoader classLoader,
			Map<SymbolicClassDeclaration, RealClass> realClassesBySymbolicClassDeclaration) {
		return new SymbolicClassGraphBuilder(classLoader,
				realClassesBySymbolicClassDeclaration).create();
	}

	public static Map<String, SymbolicClass> loadClasses(File jarFile,
			URLClassLoader classLoader, List<ClassFinder> finders) {
		Log.i("Reading " + jarFile.getName());
		List<RealClass> realClasses = RealClasses.fromJarFile(jarFile);
		Log.i("Jar load complete.");
		Log.i("Searching for classes...");
		Map<SymbolicClassDeclaration, RealClass> realClassesBySymbolicClassDeclaration = ClassFinder
				.findAllClasses(realClasses, finders);
		Log.i("Class search complete.");
		Log.i("Loading classes...");
		Map<String, SymbolicClass> result = SymbolicClasses.createClasses(
				classLoader, realClassesBySymbolicClassDeclaration);
		Log.i("Classes loaded.");
		return result;
	}

	private SymbolicClasses() {
	}
}
