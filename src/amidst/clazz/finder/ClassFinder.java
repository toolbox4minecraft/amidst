package amidst.clazz.finder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.clazz.real.RealClass;
import amidst.clazz.real.detector.RealClassDetector;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.logging.Log;

public class ClassFinder {
	public static CFBuilder builder() {
		return CFBuilder.builder();
	}

	public static Map<SymbolicClassDeclaration, RealClass> findAllClasses(
			List<RealClass> realClasses, List<ClassFinder> finders) {
		Map<SymbolicClassDeclaration, RealClass> result = new HashMap<SymbolicClassDeclaration, RealClass>();
		for (ClassFinder finder : finders) {
			RealClass realClass = finder.find(realClasses);
			if (realClass != null) {
				if (!result.containsKey(finder.declaration)) {
					result.put(finder.declaration, realClass);
				}
				Log.debug("Found: " + realClass.getRealClassName() + " as "
						+ finder.declaration.getSymbolicClassName());
			} else {
				Log.debug("Missing: "
						+ finder.declaration.getSymbolicClassName());
			}
		}
		return result;
	}

	private RealClassDetector detector;
	private SymbolicClassDeclaration declaration;

	public ClassFinder(RealClassDetector detector,
			SymbolicClassDeclaration declaration) {
		this.detector = detector;
		this.declaration = declaration;
	}

	public boolean find(RealClass realClass) {
		return detector.detect(realClass);
	}

	public RealClass find(List<RealClass> realClasses) {
		for (RealClass realClass : realClasses) {
			if (find(realClass)) {
				return realClass;
			}
		}
		return null;
	}
}
