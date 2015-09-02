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

	private Class<?> loadClass(String realClassName) {
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
		SymbolicConstructor constructor = createConstructor(
				realClassesBySymbolicClassName, declaration);
		constructorsBySymbolicName.put(declaration.getSymbolicName(),
				constructor);
	}

	public void addMethod(
			Map<String, RealClass> realClassesBySymbolicClassName,
			MethodDeclaration declaration) {
		SymbolicMethod method = createMethod(realClassesBySymbolicClassName,
				declaration);
		methodsBySymbolicName.put(declaration.getSymbolicName(), method);
	}

	public void addProperty(PropertyDeclaration declaration) {
		SymbolicProperty property = createProperty(declaration);
		propertiesBySymbolicName.put(declaration.getSymbolicName(), property);
	}

	private SymbolicConstructor createConstructor(
			Map<String, RealClass> realClassesBySymbolicClassName,
			ConstructorDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(
					realClassesBySymbolicClassName, declaration.getParameters()
							.getEntries());
			Constructor<?> constructor = getConstructor(product.getClazz(),
					parameterClasses);
			return new SymbolicConstructor(product, symbolicName, constructor);
		} catch (SecurityException e) {
			throwRuntimeException(symbolicName, e, "constructor");
		} catch (NoSuchMethodException e) {
			throwRuntimeException(symbolicName, e, "constructor");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(symbolicName, e, "constructor");
		}
		return null;
	}

	private SymbolicMethod createMethod(
			Map<String, RealClass> realClassesBySymbolicClassName,
			MethodDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(
					realClassesBySymbolicClassName, declaration.getParameters()
							.getEntries());
			Method method = getMethod(product.getClazz(), realName,
					parameterClasses);
			SymbolicClass returnType = getType(method.getReturnType());
			return new SymbolicMethod(symbolicName, realName, method,
					returnType);
		} catch (SecurityException e) {
			warn(symbolicName, e, "method");
		} catch (NoSuchMethodException e) {
			warn(symbolicName, e, "method");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(symbolicName, e, "method");
		}
		return null;
	}

	private SymbolicProperty createProperty(PropertyDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		try {
			Field field = getField(product.getClazz(), realName);
			SymbolicClass type = getType(field.getType());
			return new SymbolicProperty(product, symbolicName, realName, field,
					type);
		} catch (SecurityException e) {
			throwRuntimeException(symbolicName, e, "property");
		} catch (NoSuchFieldException e) {
			throwRuntimeException(symbolicName, e, "property");
		}
		return null;
	}

	private Constructor<?> getConstructor(Class<?> clazz,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Constructor<?> result = clazz.getConstructor(parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private Method getMethod(Class<?> clazz, String realName,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Method result = clazz.getDeclaredMethod(realName, parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private Field getField(Class<?> clazz, String realName)
			throws NoSuchFieldException {
		Field result = clazz.getDeclaredField(realName);
		result.setAccessible(true);
		return result;
	}

	private Class<?>[] getParameterClasses(
			Map<String, RealClass> realClassesBySymbolicClassName,
			List<Entry> entries) throws ClassNotFoundException {
		Class<?>[] result = new Class<?>[entries.size()];
		for (int i = 0; i < entries.size(); i++) {
			result[i] = getParameterClass(realClassesBySymbolicClassName,
					entries.get(i));
		}
		return result;
	}

	private Class<?> getParameterClass(
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

	private SymbolicClass getType(Class<?> type) {
		String result = type.getName();
		if (result.contains(".")) {
			String[] typeSplit = result.split("\\.");
			result = typeSplit[typeSplit.length - 1];
		}
		return symbolicClassesByRealClassName.get(result);
	}

	private void throwRuntimeException(String symbolicName, Exception e,
			String featureType) {
		throw new RuntimeException(errorMessage(symbolicName, e, featureType),
				e);
	}

	private void warn(String symbolicName, Exception e, String featureType) {
		Log.w(errorMessage(symbolicName, e, featureType));
		e.printStackTrace();
	}

	private String errorMessage(String symbolicName, Exception e,
			String featureType) {
		return e.getClass().getSimpleName() + " on ("
				+ product.getSymbolicName() + " / " + product.getRealName()
				+ ") " + featureType + " (" + symbolicName + ")";
	}
}
