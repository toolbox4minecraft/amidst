package amidst.symbolicclass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.byteclass.ByteClass;
import amidst.byteclass.ConstructorDeclaration;
import amidst.byteclass.MethodDeclaration;
import amidst.byteclass.PropertyDeclaration;

public class MinecraftClassGraphBuilder {
	private ClassLoader classLoader;
	private Map<String, ByteClass> realClassesBySymbolicClassName;
	private Map<String, MinecraftClass> symbolicClassesByRealClassName = new HashMap<String, MinecraftClass>();
	private Map<String, MinecraftClassBuilder> symbolicClassBuildersBySymbolicClassName = new HashMap<String, MinecraftClassBuilder>();

	public MinecraftClassGraphBuilder(ClassLoader classLoader,
			Map<String, ByteClass> realClassesBySymbolicClassName) {
		this.classLoader = classLoader;
		this.realClassesBySymbolicClassName = realClassesBySymbolicClassName;
	}

	public Map<String, MinecraftClass> create() {
		populateSymbolicClassMaps();
		addConstructorsMethodsAndProperties();
		return createProduct();
	}

	private void populateSymbolicClassMaps() {
		for (Entry<String, ByteClass> entry : realClassesBySymbolicClassName
				.entrySet()) {
			String symbolicClassName = entry.getKey();
			String realClassName = entry.getValue().getRealClassName();
			MinecraftClassBuilder builder = new MinecraftClassBuilder(
					classLoader, symbolicClassesByRealClassName,
					symbolicClassName, realClassName);
			symbolicClassesByRealClassName.put(realClassName, builder.create());
			symbolicClassBuildersBySymbolicClassName.put(symbolicClassName,
					builder);
		}
	}

	private void addConstructorsMethodsAndProperties() {
		for (Entry<String, ByteClass> entry : realClassesBySymbolicClassName
				.entrySet()) {
			ByteClass realClass = entry.getValue();
			MinecraftClassBuilder builder = symbolicClassBuildersBySymbolicClassName
					.get(entry.getKey());
			addConstructors(builder, realClass.getConstructors());
			addMethods(builder, realClass.getMethods());
			addProperties(builder, realClass.getProperties());
		}
	}

	private void addConstructors(MinecraftClassBuilder builder,
			List<ConstructorDeclaration> constructors) {
		for (ConstructorDeclaration constructor : constructors) {
			builder.addConstructor(realClassesBySymbolicClassName, constructor);
		}
	}

	private void addMethods(MinecraftClassBuilder builder,
			List<MethodDeclaration> methods) {
		for (MethodDeclaration method : methods) {
			builder.addMethod(realClassesBySymbolicClassName, method);
		}
	}

	private void addProperties(MinecraftClassBuilder builder,
			List<PropertyDeclaration> properties) {
		for (PropertyDeclaration property : properties) {
			builder.addProperty(property);
		}
	}

	private Map<String, MinecraftClass> createProduct() {
		Map<String, MinecraftClass> result = new HashMap<String, MinecraftClass>();
		for (Entry<String, MinecraftClassBuilder> entry : symbolicClassBuildersBySymbolicClassName
				.entrySet()) {
			result.put(entry.getKey(), entry.getValue().create());
		}
		return result;
	}
}
