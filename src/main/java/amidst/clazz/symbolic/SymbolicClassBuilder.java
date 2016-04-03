package amidst.clazz.symbolic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicFieldDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclaration;
import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;

/**
 * This class should only be used by the class {@link SymbolicClassGraphBuilder}
 */
@NotThreadSafe
public class SymbolicClassBuilder {
	
	private class WildcardReferenceParameter { }
	
	private static Map<String, Class<?>> createPrimitivesMap() {
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
		result.put("*", WildcardReferenceParameter.class);
		return result;
	}

	private static final Map<String, Class<?>> PRIMITIVES_MAP = createPrimitivesMap();

	private final Map<String, SymbolicConstructor> constructorsBySymbolicName = new HashMap<String, SymbolicConstructor>();
	private final Map<String, SymbolicMethod> methodsBySymbolicName = new HashMap<String, SymbolicMethod>();
	private final Map<String, SymbolicField> fieldsBySymbolicName = new HashMap<String, SymbolicField>();

	private final ClassLoader classLoader;
	private final Map<String, String> realClassNamesBySymbolicClassName;
	private final Map<String, SymbolicClass> symbolicClassesByRealClassName;
	private final SymbolicClass product;

	public SymbolicClassBuilder(ClassLoader classLoader,
			Map<String, String> realClassNamesBySymbolicClassName,
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			String symbolicClassName, String realClassName)
			throws ClassNotFoundException {
		this.classLoader = classLoader;
		this.realClassNamesBySymbolicClassName = realClassNamesBySymbolicClassName;
		this.symbolicClassesByRealClassName = symbolicClassesByRealClassName;
		this.product = new SymbolicClass(symbolicClassName, realClassName,
				loadClass(realClassName), constructorsBySymbolicName,
				methodsBySymbolicName, fieldsBySymbolicName);
	}

	public SymbolicClass getProduct() {
		return product;
	}

	private Class<?> loadClass(String realClassName)
			throws ClassNotFoundException {
		return classLoader.loadClass(realClassName);
	}

	public void addConstructor(SymbolicConstructorDeclaration declaration)
			throws SymbolicClassGraphCreationException {
		try {
			constructorsBySymbolicName.put(declaration.getSymbolicName(),
					createConstructor(declaration));
		} catch (NoSuchMethodException | ClassNotFoundException e) {
			declaration.handleMissing(e, product.getSymbolicName(),
					product.getRealName());
		}
	}

	public void addMethod(SymbolicMethodDeclaration declaration)
			throws SymbolicClassGraphCreationException {
		try {
			methodsBySymbolicName.put(declaration.getSymbolicName(),
					createMethod(declaration));
		} catch (NoSuchMethodException | ClassNotFoundException e) {
			declaration.handleMissing(e, product.getSymbolicName(),
					product.getRealName());
		}
	}

	public void addField(SymbolicFieldDeclaration declaration)
			throws SymbolicClassGraphCreationException {
		try {
			fieldsBySymbolicName.put(declaration.getSymbolicName(),
					createField(declaration));
		} catch (NoSuchFieldException e) {
			declaration.handleMissing(e, product.getSymbolicName(),
					product.getRealName());
		}
	}

	private SymbolicConstructor createConstructor(
			SymbolicConstructorDeclaration declaration)
			throws ClassNotFoundException, NoSuchMethodException {
		String symbolicName = declaration.getSymbolicName();
		Class<?>[] parameterClasses = getParameterClasses(declaration
				.getParameters().getDeclarations());
		Constructor<?> constructor = getConstructor(product.getClazz(),
				parameterClasses);
		return new SymbolicConstructor(product, symbolicName, constructor);
	}

	private SymbolicMethod createMethod(SymbolicMethodDeclaration declaration)
			throws ClassNotFoundException, NoSuchMethodException {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		Class<?>[] parameterClasses = getParameterClasses(declaration
				.getParameters().getDeclarations());
		Method method = getMethod(product.getClazz(), realName,
				parameterClasses);
		SymbolicClass returnType = getType(method.getReturnType());
		return new SymbolicMethod(product, symbolicName, realName, method,
				returnType);
	}

	private SymbolicField createField(SymbolicFieldDeclaration declaration)
			throws NoSuchFieldException {
		
		String fieldSymbolicName = declaration.getSymbolicName();
		String fieldRealName    = null; // Ensure switch statement assigns a value to this. Compiler static checking isn't smart enough, so I have to assign null here.
		Field field             = null;	// Ensure switch statement assigns a value to this. Compiler static checking isn't smart enough, so I have to assign null here.	
		SymbolicClass fieldType = null; // Ensure switch statement assigns a value to this. Compiler static checking isn't smart enough, so I have to assign null here.
		
		switch (declaration.getDeclarationType()) {
		
			case FIELDNAME_REAL_NAME:
			
				fieldRealName = declaration.getDeclaration();
				field = getFieldByRealName(product.getClazz(), declaration.getDeclaration());
				fieldType = getType(field.getType());
				break;
				
			case FIELDTYPE_BY_SYMBOLIC_CLASSNAME:
				
				String symbolicTypeName = declaration.getDeclaration();
				String realClassName = realClassNamesBySymbolicClassName.get(symbolicTypeName);				
				if (realClassName == null) throw new NoSuchFieldException("Could not find type '" + symbolicTypeName + "' in order to look for field");
				
				field = getFieldByType(product.getClazz(), realClassName);
				fieldType = getType(field.getType());
				fieldRealName = field.getName();
				break;
				
			case FIELDTYPE_BY_REAL_TYPE:

				field = getFieldByType(product.getClazz(), declaration.getDeclaration());
				fieldType = getType(field.getType());
				fieldRealName = field.getName();
				break;			
		}
		return new SymbolicField(product, fieldSymbolicName, fieldRealName, field, fieldType);
	}

	private Constructor<?> getConstructor(Class<?> clazz,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		
		Constructor<?> result;		
		try {		
			result = clazz.getConstructor(parameterClasses);
		} catch (NoSuchMethodException ex) {
			// perhaps one of the parameters is a wildcard reference 
			result = matchConstructor(clazz, parameterClasses);
			if (result == null) throw ex;
		}
			
			
		result.setAccessible(true);
		return result;
	}

	private Method getMethod(Class<?> clazz, String realName,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		
		Method result;		
		try {
			result = clazz.getDeclaredMethod(realName, parameterClasses);
		} catch (NoSuchMethodException ex) {
			// perhaps one of the parameters is a wildcard reference 
			result = matchDeclaredMethod(clazz, realName, parameterClasses);
			if (result == null) throw ex;
		}
		
		result.setAccessible(true);
		return result;
	}

	private Constructor<?> matchConstructor(Class<?> clazz, Class<?>[] parameterClasses) {
		
		Constructor<?> result = null;
		int matches = 0;
		
		for(Constructor<?> constructor: clazz.getConstructors()) {
			
			if (constructor.getParameterCount() == parameterClasses.length) {
				boolean match = true;
				int i = 0;
				for(Parameter param: constructor.getParameters()) {
					if (parameterClasses[i] != WildcardReferenceParameter.class) {
						if (param.getType() != parameterClasses[i]) {
							match = false;
							break;
						}						
					}
					i++;
				}
				
				if (match) {
					matches++;					
					if (result == null) {
						Log.i("constructor successfully wildcard-matched for '" + clazz.getName() + "'");
						result = constructor;
					} else {
						Log.w(matches + " matching constructor found for '" + clazz.getName() + "', assuming the first one.");
					}					
				}
			}
		}
		return result;
	}
	
	private Method matchDeclaredMethod(Class<?> clazz, String name, Class<?>[] parameterClasses) {
		
		Method result = null;
		int result_MatchIndex = 0;
		int matchesFound = 0;
		
		for(Method method: clazz.getDeclaredMethods()) {
			
			if (method.getName() == name && method.getParameterCount() == parameterClasses.length) {
				boolean match = true;
				int i = 0;
				for(Parameter param: method.getParameters()) {
					if (parameterClasses[i] != WildcardReferenceParameter.class) {
						if (param.getType() != parameterClasses[i]) {
							match = false;
							break;
						}						
					}
					i++;
				}
				
				if (match) {
					matchesFound++;
					if (result == null && !methodHasAlreadyBeenMatched(method)) {					
						result = method;
						result_MatchIndex = matchesFound;							
					}
				}
			}		
		}
		
		String methodDesc = clazz.getName() + "." + name;
		if (result == null) {
			if (matchesFound == 0) {
				Log.w("No matches found for " + methodDesc);
			} else {
				Log.w(matchesFound + " matching methods found for " + methodDesc + ", but none that aren't already assigned to a symbolic method.");
			}
		} else {
			if (matchesFound == 1) {
				Log.i("method successfully wildcard-matched for " + methodDesc);
			} else {
				Log.i(
					matchesFound + " matching methods found for " + methodDesc + 
					", assuming method #" + result_MatchIndex + 
					" (" + (result_MatchIndex - 1) + " earlier matching methods already assigned to a symbolic method)"
				);				
			}
		}
		
		return result;
	}
	
	private boolean methodHasAlreadyBeenMatched(Method method) {
		
		for(SymbolicMethod symbolicMethod : methodsBySymbolicName.values()) {
			if (symbolicMethod.matches(method)) return true;
		}
		return false;		
	}
	
	private Field getFieldByRealName(Class<?> clazz, String realName)
			throws NoSuchFieldException {
		Field result = clazz.getDeclaredField(realName);
		result.setAccessible(true);
		return result;
	}
		
	private Field getFieldByType(Class<?> clazz, String typeName)
			throws NoSuchFieldException {
				
		Class<?> primitiveClass = PRIMITIVES_MAP.get(typeName);
		if (primitiveClass != null) typeName = primitiveClass.getName();
		
		for(Field field: clazz.getFields()) {
			
			if (field.getType().getName().equals(typeName)) {
				
				field.setAccessible(true);
				return field;				
			}
		}
		throw new NoSuchFieldException();
	}

	private Class<?>[] getParameterClasses(
			List<SymbolicParameterDeclaration> declarations)
			throws ClassNotFoundException {
		Class<?>[] result = new Class<?>[declarations.size()];
		for (int i = 0; i < declarations.size(); i++) {
			result[i] = getParameterClass(declarations.get(i));
		}
		return result;
	}

	private Class<?> getParameterClass(SymbolicParameterDeclaration declaration)
			throws ClassNotFoundException {
		Class<?> result = PRIMITIVES_MAP.get(declaration.getType());
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
}
