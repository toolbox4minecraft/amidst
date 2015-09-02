package amidst.clazz.symbolic;

import java.util.HashMap;
import java.util.Map;

import amidst.clazz.ConstructorDeclaration;
import amidst.clazz.MethodDeclaration;
import amidst.clazz.PropertyDeclaration;
import amidst.clazz.real.RealClass;

public class SymbolicClassBuilder {
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
		SymbolicConstructor constructor = SymbolicClasses.createConstructor(
				classLoader, product, realClassesBySymbolicClassName,
				declaration);
		constructorsBySymbolicName.put(declaration.getSymbolicName(),
				constructor);
	}

	public void addMethod(
			Map<String, RealClass> realClassesBySymbolicClassName,
			MethodDeclaration declaration) {
		SymbolicMethod method = SymbolicClasses.createMethod(classLoader,
				symbolicClassesByRealClassName, product,
				realClassesBySymbolicClassName, declaration);
		methodsBySymbolicName.put(declaration.getSymbolicName(), method);
	}

	public void addProperty(PropertyDeclaration declaration) {
		SymbolicProperty property = SymbolicClasses.createProperty(
				symbolicClassesByRealClassName, product, declaration);
		propertiesBySymbolicName.put(declaration.getSymbolicName(), property);
	}

	public SymbolicClass create() {
		return product;
	}
}
