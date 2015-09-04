package amidst.clazz.symbolic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicFieldDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;

public class SymbolicClassGraphBuilder {
	private ClassLoader classLoader;
	private Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration;
	private Map<String, String> realClassNamesBySymbolicClassName = new HashMap<String, String>();
	private Map<String, SymbolicClass> symbolicClassesByRealClassName = new HashMap<String, SymbolicClass>();
	private Map<SymbolicClassDeclaration, SymbolicClassBuilder> symbolicClassBuildersBySymbolicClassDeclaration = new HashMap<SymbolicClassDeclaration, SymbolicClassBuilder>();

	public SymbolicClassGraphBuilder(
			ClassLoader classLoader,
			Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration) {
		this.classLoader = classLoader;
		this.realClassNamesBySymbolicClassDeclaration = realClassNamesBySymbolicClassDeclaration;
	}

	public Map<String, SymbolicClass> create() {
		createSymbolicClasses();
		addConstructorsMethodsAndFields();
		return createProduct();
	}

	private void createSymbolicClasses() {
		for (Entry<SymbolicClassDeclaration, String> entry : realClassNamesBySymbolicClassDeclaration
				.entrySet()) {
			SymbolicClassDeclaration declaration = entry.getKey();
			String symbolicClassName = declaration.getSymbolicClassName();
			String realClassName = entry.getValue();
			SymbolicClassBuilder builder = new SymbolicClassBuilder(
					classLoader, realClassNamesBySymbolicClassName,
					symbolicClassesByRealClassName,
					declaration.getSymbolicClassName(), realClassName);
			SymbolicClass symbolicClass = builder.create();
			realClassNamesBySymbolicClassName.put(symbolicClassName,
					realClassName);
			symbolicClassesByRealClassName.put(realClassName, symbolicClass);
			symbolicClassBuildersBySymbolicClassDeclaration.put(declaration,
					builder);
		}
	}

	private void addConstructorsMethodsAndFields() {
		for (Entry<SymbolicClassDeclaration, SymbolicClassBuilder> entry : symbolicClassBuildersBySymbolicClassDeclaration
				.entrySet()) {
			SymbolicClassDeclaration declaration = entry.getKey();
			SymbolicClassBuilder builder = entry.getValue();
			addConstructors(builder, declaration.getConstructors());
			addMethods(builder, declaration.getMethods());
			addFields(builder, declaration.getFields());
		}
	}

	private void addConstructors(SymbolicClassBuilder builder,
			List<SymbolicConstructorDeclaration> constructors) {
		for (SymbolicConstructorDeclaration constructor : constructors) {
			builder.addConstructor(constructor);
		}
	}

	private void addMethods(SymbolicClassBuilder builder,
			List<SymbolicMethodDeclaration> methods) {
		for (SymbolicMethodDeclaration method : methods) {
			builder.addMethod(method);
		}
	}

	private void addFields(SymbolicClassBuilder builder,
			List<SymbolicFieldDeclaration> fields) {
		for (SymbolicFieldDeclaration field : fields) {
			builder.addField(field);
		}
	}

	private Map<String, SymbolicClass> createProduct() {
		Map<String, SymbolicClass> result = new HashMap<String, SymbolicClass>();
		for (Entry<SymbolicClassDeclaration, SymbolicClassBuilder> entry : symbolicClassBuildersBySymbolicClassDeclaration
				.entrySet()) {
			result.put(entry.getKey().getSymbolicClassName(), entry.getValue()
					.create());
		}
		return result;
	}
}
