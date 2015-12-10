package amidst.clazz.symbolic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicFieldDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList.ParameterDeclaration;
import amidst.documentation.Immutable;
import amidst.logging.Log;

public class SymbolicClassBuilder {
	@Immutable
	private static enum StatelessResources {
		INSTANCE;

		private final Map<String, Class<?>> primitivesMap = createPrimitivesMap();

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
	private Map<String, SymbolicField> fieldsBySymbolicName = new HashMap<String, SymbolicField>();

	private ClassLoader classLoader;
	private Map<String, String> realClassNamesBySymbolicClassName;
	private Map<String, SymbolicClass> symbolicClassesByRealClassName;
	private SymbolicClass product;

	public SymbolicClassBuilder(ClassLoader classLoader,
			Map<String, String> realClassNamesBySymbolicClassName,
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			String symbolicClassName, String realClassName) {
		this.classLoader = classLoader;
		this.realClassNamesBySymbolicClassName = realClassNamesBySymbolicClassName;
		this.symbolicClassesByRealClassName = symbolicClassesByRealClassName;
		this.product = new SymbolicClass(symbolicClassName, realClassName,
				loadClass(realClassName), constructorsBySymbolicName,
				methodsBySymbolicName, fieldsBySymbolicName);
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

	public void addConstructor(SymbolicConstructorDeclaration declaration) {
		constructorsBySymbolicName.put(declaration.getSymbolicName(),
				createConstructor(declaration));
	}

	public void addMethod(SymbolicMethodDeclaration declaration) {
		methodsBySymbolicName.put(declaration.getSymbolicName(),
				createMethod(declaration));
	}

	public void addField(SymbolicFieldDeclaration declaration) {
		fieldsBySymbolicName.put(declaration.getSymbolicName(),
				createField(declaration));
	}

	private SymbolicConstructor createConstructor(
			SymbolicConstructorDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(declaration
					.getParameters().getDeclarations());
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

	private SymbolicMethod createMethod(SymbolicMethodDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(declaration
					.getParameters().getDeclarations());
			Method method = getMethod(product.getClazz(), realName,
					parameterClasses);
			SymbolicClass returnType = getType(method.getReturnType());
			return new SymbolicMethod(product, symbolicName, realName, method,
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

	private SymbolicField createField(SymbolicFieldDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		try {
			Field field = getField(product.getClazz(), realName);
			SymbolicClass type = getType(field.getType());
			return new SymbolicField(product, symbolicName, realName, field,
					type);
		} catch (SecurityException e) {
			throwRuntimeException(symbolicName, e, "field");
		} catch (NoSuchFieldException e) {
			throwRuntimeException(symbolicName, e, "field");
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
			List<ParameterDeclaration> declarations)
			throws ClassNotFoundException {
		Class<?>[] result = new Class<?>[declarations.size()];
		for (int i = 0; i < declarations.size(); i++) {
			result[i] = getParameterClass(declarations.get(i));
		}
		return result;
	}

	private Class<?> getParameterClass(ParameterDeclaration declaration)
			throws ClassNotFoundException {
		Class<?> result = StatelessResources.INSTANCE.primitivesMap
				.get(declaration.getType());
		if (result != null) {
			return result;
		} else if (declaration.isSymbolic()) {
			String realClassName = realClassNamesBySymbolicClassName
					.get(declaration.getType());
			if (realClassName != null) {
				return classLoader.loadClass(realClassName);
			} else {
				throw new ClassNotFoundException(
						"cannot resolve symbolic class name: "
								+ declaration.getType());
			}
		} else {
			return classLoader.loadClass(declaration.getType());
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
	}

	private String errorMessage(String symbolicName, Exception e,
			String featureType) {

		// TODO: find a way to remove this ugly workaround
		String initializeAllBiomeGeneratorsWorkaroundText = "";
		if (symbolicName.equals("initializeAllBiomeGenerators")) {
			initializeAllBiomeGeneratorsWorkaroundText = ". Don't worry about this warning. It is normal in current Minecraft versions.";
		}

		return e.getClass().getSimpleName() + " on ("
				+ product.getSymbolicName() + " / " + product.getRealName()
				+ ") " + featureType + " (" + symbolicName + ")"
				+ initializeAllBiomeGeneratorsWorkaroundText;
	}
}
