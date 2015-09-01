package amidst.minecraft.local;

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
	private Map<String, ByteClass> byteClassesByMinecraftClassName;
	private Map<String, MinecraftClass> minecraftClassesByByteClassName = new HashMap<String, MinecraftClass>();
	private Map<String, MinecraftClassBuilder> minecraftClassBuildersByMinecraftClassName = new HashMap<String, MinecraftClassBuilder>();

	public MinecraftClassGraphBuilder(ClassLoader classLoader,
			Map<String, ByteClass> byteClassesByMinecraftClassName) {
		this.classLoader = classLoader;
		this.byteClassesByMinecraftClassName = byteClassesByMinecraftClassName;
	}

	public Map<String, MinecraftClass> create() {
		populateMinecraftClassMaps();
		addPropertiesMethodsAndConstructors();
		return createProduct();
	}

	private void populateMinecraftClassMaps() {
		for (Entry<String, ByteClass> entry : byteClassesByMinecraftClassName
				.entrySet()) {
			String minecraftClassName = entry.getKey();
			String byteClassName = entry.getValue().getByteClassName();
			MinecraftClassBuilder builder = new MinecraftClassBuilder(
					classLoader, minecraftClassesByByteClassName,
					minecraftClassName, byteClassName);
			minecraftClassesByByteClassName
					.put(byteClassName, builder.create());
			minecraftClassBuildersByMinecraftClassName.put(minecraftClassName,
					builder);
		}
	}

	private void addPropertiesMethodsAndConstructors() {
		for (Entry<String, ByteClass> entry : byteClassesByMinecraftClassName
				.entrySet()) {
			ByteClass byteClass = entry.getValue();
			MinecraftClassBuilder builder = minecraftClassBuildersByMinecraftClassName
					.get(entry.getKey());
			addConstructors(builder, byteClass.getConstructors());
			addMethods(builder, byteClass.getMethods());
			addProperties(builder, byteClass.getProperties());
		}
	}

	private void addConstructors(MinecraftClassBuilder builder,
			List<ConstructorDeclaration> constructors) {
		for (ConstructorDeclaration constructor : constructors) {
			builder.addConstructor(byteClassesByMinecraftClassName, constructor);
		}
	}

	private void addMethods(MinecraftClassBuilder builder,
			List<MethodDeclaration> methods) {
		for (MethodDeclaration method : methods) {
			builder.addMethod(byteClassesByMinecraftClassName, method);
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
		for (Entry<String, MinecraftClassBuilder> entry : minecraftClassBuildersByMinecraftClassName
				.entrySet()) {
			result.put(entry.getKey(), entry.getValue().create());
		}
		return result;
	}
}
