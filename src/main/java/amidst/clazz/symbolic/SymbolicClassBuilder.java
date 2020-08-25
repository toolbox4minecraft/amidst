package amidst.clazz.symbolic;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicFieldDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclaration;
import amidst.documentation.NotThreadSafe;

/**
 * This class should only be used by the class {@link SymbolicClassGraphBuilder}
 */
@NotThreadSafe
public class SymbolicClassBuilder {
	private static Map<String, Class<?>> createPrimitivesMap() {
		Map<String, Class<?>> result = new HashMap<>();
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

	private static final Map<String, Class<?>> PRIMITIVES_MAP = createPrimitivesMap();

	private final Map<String, SymbolicConstructor> constructorsBySymbolicName = new HashMap<>();
	private final Map<String, SymbolicMethod> methodsBySymbolicName = new HashMap<>();
	private final Map<String, SymbolicField> fieldsBySymbolicName = new HashMap<>();

	private final ClassLoader classLoader;
	private final Map<String, String> realClassNamesBySymbolicClassName;
	private final Map<String, SymbolicClass> symbolicClassesByRealClassName;
	private final SymbolicClass product;

	public SymbolicClassBuilder(
			ClassLoader classLoader,
			Map<String, String> realClassNamesBySymbolicClassName,
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			String symbolicClassName,
			String realClassName)
			throws ClassNotFoundException {
		this.classLoader = classLoader;
		this.realClassNamesBySymbolicClassName = realClassNamesBySymbolicClassName;
		this.symbolicClassesByRealClassName = symbolicClassesByRealClassName;
		this.product = new SymbolicClass(
				symbolicClassName,
				realClassName,
				loadClass(realClassName),
				constructorsBySymbolicName,
				methodsBySymbolicName,
				fieldsBySymbolicName);
	}

	public SymbolicClass getProduct() {
		return product;
	}

	private Class<?> loadClass(String realClassName) throws ClassNotFoundException {
		return classLoader.loadClass(realClassName);
	}

	public void addConstructor(SymbolicConstructorDeclaration declaration) throws SymbolicClassGraphCreationException {
		try {
			constructorsBySymbolicName.put(declaration.getSymbolicName(), createConstructor(declaration));
		} catch (NoSuchMethodException | ClassNotFoundException e) {
			declaration.handleMissing(e, product.getSymbolicName(), product.getRealName());
		}
	}

	public void addMethod(SymbolicMethodDeclaration declaration) throws SymbolicClassGraphCreationException {
		try {
			methodsBySymbolicName.put(declaration.getSymbolicName(), createMethod(declaration));
		} catch (NoSuchMethodException | ClassNotFoundException e) {
			declaration.handleMissing(e, product.getSymbolicName(), product.getRealName());
		}
	}

	public void addField(SymbolicFieldDeclaration declaration) throws SymbolicClassGraphCreationException {
		try {
			fieldsBySymbolicName.put(declaration.getSymbolicName(), createField(declaration));
		} catch (NoSuchFieldException e) {
			declaration.handleMissing(e, product.getSymbolicName(), product.getRealName());
		}
	}

	private SymbolicConstructor createConstructor(SymbolicConstructorDeclaration declaration)
			throws ClassNotFoundException,
			NoSuchMethodException {
		String symbolicName = declaration.getSymbolicName();
		Class<?>[] parameterClasses = getParameterClasses(declaration.getParameters().getDeclarations());
		Constructor<?> constructor = getConstructor(product.getClazz(), parameterClasses);
		return new SymbolicConstructor(product, symbolicName, constructor);
	}

	private SymbolicMethod createMethod(SymbolicMethodDeclaration declaration)
			throws ClassNotFoundException,
			NoSuchMethodException {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		Class<?>[] parameterClasses = getParameterClasses(declaration.getParameters().getDeclarations());
		Method method = getMethod(product.getClazz(), realName, parameterClasses);
		SymbolicClass returnType = getTypeOrSupertype(method.getReturnType());
		return new SymbolicMethod(product, symbolicName, realName, method, returnType);
	}

	private SymbolicField createField(SymbolicFieldDeclaration declaration) throws NoSuchFieldException {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		Field field = getField(product.getClazz(), realName);
		SymbolicClass type = getTypeOrSupertype(field.getType());
		return new SymbolicField(product, symbolicName, realName, field, type);
	}

	private Constructor<?> getConstructor(Class<?> clazz, Class<?>[] parameterClasses) throws NoSuchMethodException {
		Constructor<?> result = clazz.getConstructor(parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private Method getMethod(Class<?> clazz, String realName, Class<?>[] parameterClasses)
			throws NoSuchMethodException {
		Method result = clazz.getDeclaredMethod(realName, parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private Field getField(Class<?> clazz, String realName) throws NoSuchFieldException {
		Field result = clazz.getDeclaredField(realName);
		result.setAccessible(true);
		return result;
	}

	private Class<?>[] getParameterClasses(List<SymbolicParameterDeclaration> declarations)
			throws ClassNotFoundException {
		Class<?>[] result = new Class<?>[declarations.size()];
		for (int i = 0; i < declarations.size(); i++) {
			SymbolicParameterDeclaration declaration = declarations.get(i);
			result[i] = getArrayClass(getParameterClass(declaration), declaration.getArrayDimensions());
		}
		return result;
	}

	private Class<?> getParameterClass(SymbolicParameterDeclaration declaration) throws ClassNotFoundException {
		Class<?> result = PRIMITIVES_MAP.get(declaration.getType());
		if (result != null) {
			return result;
		} else if (declaration.isSymbolic()) {
			String realClassName = realClassNamesBySymbolicClassName.get(declaration.getType());
			if (realClassName != null) {
				return classLoader.loadClass(realClassName);
			} else {
				throw new ClassNotFoundException("cannot resolve symbolic class name: " + declaration.getType());
			}
		} else {
			return classLoader.loadClass(declaration.getType());
		}
	}
	
	public static Class<?> getArrayClass(Class<?> elementType, int dimensions) {
	    return dimensions == 0 ? elementType : Array.newInstance(elementType, new int[dimensions]).getClass();
	}

	private SymbolicClass getType(Class<?> type) {
		String result = type.getName();
		if (result.contains(".")) {
			String[] typeSplit = result.split("\\.");
			result = typeSplit[typeSplit.length - 1];
		}
		return symbolicClassesByRealClassName.get(result);
	}

	private SymbolicClass getTypeOrSupertype(Class<?> type) {
		// Check the class hierarchy of 'type' to find a known class
		Class<?> clazz = type;
		while (clazz != null) {
			SymbolicClass symClass = getType(clazz);
			if (symClass != null) {
				return symClass;
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}
}
