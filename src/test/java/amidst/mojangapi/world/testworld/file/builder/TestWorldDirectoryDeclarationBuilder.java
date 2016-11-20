package amidst.mojangapi.world.testworld.file.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.testworld.file.TestWorldDirectoryDeclaration;
import amidst.mojangapi.world.testworld.file.TestWorldEntryDeclaration;

@NotThreadSafe
public class TestWorldDirectoryDeclarationBuilder {
	private final Map<String, TestWorldEntryDeclarationBuilder<?>> builders = new HashMap<>();

	public TestWorldDirectoryDeclaration create() {
		Map<String, TestWorldEntryDeclaration<?>> result = new HashMap<>();
		for (Entry<String, TestWorldEntryDeclarationBuilder<?>> entry : builders.entrySet()) {
			result.put(entry.getKey(), entry.getValue().constructThis());
		}
		return new TestWorldDirectoryDeclaration(Collections.unmodifiableMap(result));
	}

	public <T> TestWorldEntryDeclarationBuilder<T> entry(String name, Class<T> clazz) {
		TestWorldEntryDeclarationBuilder<T> builder = new TestWorldEntryDeclarationBuilder<>(this, name, clazz);
		builders.put(name, builder);
		return builder;
	}
}
