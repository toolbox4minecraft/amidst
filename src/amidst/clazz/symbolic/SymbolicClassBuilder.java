package amidst.clazz.symbolic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.clazz.ConstructorDeclaration;
import amidst.clazz.MethodDeclaration;
import amidst.clazz.ParameterDeclarationList.Entry;
import amidst.clazz.PropertyDeclaration;
import amidst.clazz.real.RealClass;
import amidst.logging.Log;

public class SymbolicClassBuilder {
	private static enum StatelessResources {
		INSTANCE;

		private Map<String, Class<?>> primitivesMap = createPrimitivesMap();

		private Map<String, Class<?>> createPrimitivesMap() {
			Map<String, Class<?>> result = new HashMap<String, Class<?>>();
			result.put("byte", byte.class);
			result.put("int", int.class);
			result.put("float", float.class);
			result.put("short", short.class);
			result.put("long", long.class);
			result.put("double", double.class);
			result.put("boolean", boolean.class);
			result.put("char", char.class);
			result.put("String", String.class);
			return result;
		}
	}

	private Map<String, SymbolicConstructor> constructorsBySymbolicName = new HashMap<String, SymbolicConstructor>();
	private Map<String, SymbolicMethod> methodsBySymbolicName = new HashMap<String, SymbolicMethod>();
	private Map<String, SymbolicProperty> propertiesBySymbolicName = new HashMap<String, SymbolicProperty>();

	private ClassLoader classLoader;
	private Map<String, SymbolicClass> symbolicClassesByRealClassName;
	private SymbolicClass product;

	public SymbolicClassBuilder(ClassLoader classLoader,
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			String symbolicClassName, String realClassName) {
		this.classLoader = classLoader;
		this.symbolicClassesByRealClassName = symbolicClassesByRealClassName;
		this.product = new SymbolicClass(symbolicClassName, realClassName,
				loadClass(realClassName), constructorsBySymbolicName,
				methodsBySymbolicName, propertiesBySymbolicName);
	}

	public SymbolicClass create() {
		return product;
	}

	public Class<?> loadClass(String realClassName) {
		try {
			return classLoader.loadClass(realClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Error loading a class ("
					+ realClassName + ")", e);
		}
	}

	public void addConstructor(
			Map<String, RealClass> realClassesBySymbolicClassName,
			ConstructorDeclaration declaration) {
		SymbolicConstructor constructor = createConstructor(classLoader,
				product, realClassesBySymbolicClassName, declaration);
		constructorsBySymbolicName.put(declaration.getSymbolicName(),
				constructor);
	}

	public void addMethod(
			Map<String, RealClass> realClassesBySymbolicClassName,
			MethodDeclaration declaration) {
		SymbolicMethod method = createMethod(classLoader,
				symbolicClassesByRealClassName, product,
				realClassesBySymbolicClassName, declaration);
		methodsBySymbolicName.put(declaration.getSymbolicName(), method);
	}

	public void addProperty(PropertyDeclaration declaration) {
		SymbolicProperty property = createProperty(
				symbolicClassesByRealClassName, product, declaration);
		propertiesBySymbolicName.put(declaration.getSymbolicName(), property);
	}

	public static SymbolicConstructor createConstructor(
			ClassLoader classLoader, SymbolicClass parent,
			Map<String, RealClass> realClassesBySymbolicClassName,
			ConstructorDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(classLoader,
					realClassesBySymbolicClassName, declaration.getParameters()
							.getEntries());
			Constructor<?> constructor = getConstructor(parent.getClazz(),
					parameterClasses);
			return new SymbolicConstructor(parent, symbolicName, constructor);
		} catch (SecurityException e) {
			throwRuntimeException(parent, symbolicName, e, "constructor");
		} catch (NoSuchMethodException e) {
			throwRuntimeException(parent, symbolicName, e, "constructor");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(parent, symbolicName, e, "constructor");
		}
		return null;
	}

	public static SymbolicMethod createMethod(ClassLoader classLoader,
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			SymbolicClass parent,
			Map<String, RealClass> realClassesBySymbolicClassName,
			MethodDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(classLoader,
					realClassesBySymbolicClassName, declaration.getParameters()
							.getEntries());
			Method method = getMethod(parent.getClazz(), realName,
					parameterClasses);
			SymbolicClass returnType = getType(symbolicClassesByRealClassName,
					method.getReturnType());
			return new SymbolicMethod(symbolicName, realName, method,
					returnType);
		} catch (SecurityException e) {
			warn(parent, symbolicName, e, "method");
		} catch (NoSuchMethodException e) {
			warn(parent, symbolicName, e, "method");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(parent, symbolicName, e, "method");
		}
		return null;
	}

	public static SymbolicProperty createProperty(
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			SymbolicClass parent, PropertyDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		try {
			Field field = getField(parent.getClazz(), realName);
			SymbolicClass type = getType(symbolicClassesByRealClassName,
					field.getType());
			return new SymbolicProperty(parent, symbolicName, realName, field,
					type);
		} catch (SecurityException e) {
			throwRuntimeException(parent, symbolicName, e, "property");
		} catch (NoSuchFieldException e) {
			throwRuntimeException(parent, symbolicName, e, "property");
		}
		return null;
	}

	private static Constructor<?> getConstructor(Class<?> clazz,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Constructor<?> result = clazz.getConstructor(parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private static Method getMethod(Class<?> clazz, String realName,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Method result = clazz.getDeclaredMethod(realName, parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private static Field getField(Class<?> clazz, String realName)
			throws NoSuchFieldException {
		Field result = clazz.getDeclaredField(realName);
		result.setAccessible(true);
		return result;
	}

	private static Class<?>[] getParameterClasses(ClassLoader classLoader,
			Map<String, RealClass> realClassesBySymbolicClassName,
			List<Entry> entries) throws ClassNotFoundException {
		Class<?>[] result = new Class<?>[entries.size()];
		for (int i = 0; i < entries.size(); i++) {
			result[i] = getParameterClass(classLoader,
					realClassesBySymbolicClassName, entries.get(i));
		}
		return result;
	}

	private static Class<?> getParameterClass(ClassLoader classLoader,
			Map<String, RealClass> realClassesBySymbolicClassName, Entry entry)
			throws ClassNotFoundException {
		Class<?> result = StatelessResources.INSTANCE.primitivesMap.get(entry
				.getType());
		if (result != null) {
			return result;
		} else if (entry.isSymbolic()) {
			RealClass realClass = realClassesBySymbolicClassName.get(entry
					.getType());
			if (realClass != null) {
				return classLoader.loadClass(realClass.getRealClassName());
			} else {
				throw new ClassNotFoundException(
						"cannot resolve symbolic class name: "
								+ entry.getType());
			}
		} else {
			return classLoader.loadClass(entry.getType());
		}
	}

	private static SymbolicClass getType(
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			Class<?> type) {
		String result = type.getName();
		if (result.contains(".")) {
			String[] typeSplit = result.split("\\.");
			result = typeSplit[typeSplit.length - 1];
		}
		return symbolicClassesByRealClassName.get(result);
	}

	private static void throwRuntimeException(SymbolicClass parent,
			String symbolicName, Exception e, String featureType) {
		throw new RuntimeException(errorMessage(parent, symbolicName, e,
				featureType), e);
	}

	private static void warn(SymbolicClass parent, String symbolicName,
			Exception e, String featureType) {
		Log.w(errorMessage(parent, symbolicName, e, featureType));
		e.printStackTrace();
	}

	private static String errorMessage(SymbolicClass parent,
			String symbolicName, Exception e, String featureType) {
		return e.getClass().getSimpleName() + " on ("
				+ parent.getSymbolicName() + " / " + parent.getRealName()
				+ ") " + featureType + " (" + symbolicName + ")";
	}
}
