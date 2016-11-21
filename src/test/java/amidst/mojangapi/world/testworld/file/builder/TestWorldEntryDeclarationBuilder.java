package amidst.mojangapi.world.testworld.file.builder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.testworld.file.TestWorldDirectoryDeclaration;
import amidst.mojangapi.world.testworld.file.TestWorldEntryDeclaration;

@NotThreadSafe
public class TestWorldEntryDeclarationBuilder<T> {
	private final TestWorldDirectoryDeclarationBuilder parent;
	private final String name;
	private final Class<T> clazz;
	private BiConsumer<OutputStream, T> serializer;
	private Function<InputStream, T> deserializer;
	private Function<World, T> extractor;
	private BiPredicate<T, T> equalityChecker;
	private boolean skipEqualityCheck;

	public TestWorldEntryDeclarationBuilder(TestWorldDirectoryDeclarationBuilder parent, String name, Class<T> clazz) {
		this.parent = parent;
		this.name = name;
		this.clazz = clazz;
	}

	public TestWorldEntryDeclarationBuilder<T> serializer(BiConsumer<OutputStream, T> serializer) {
		this.serializer = serializer;
		return this;
	}

	public TestWorldEntryDeclarationBuilder<T> deserializer(Function<InputStream, T> deserializer) {
		this.deserializer = deserializer;
		return this;
	}

	public TestWorldEntryDeclarationBuilder<T> extractor(Function<World, T> extractor) {
		this.extractor = extractor;
		return this;
	}

	public TestWorldEntryDeclarationBuilder<T> equalityChecker(BiPredicate<T, T> equalityChecker) {
		this.skipEqualityCheck = false;
		this.equalityChecker = equalityChecker;
		return this;
	}

	public TestWorldEntryDeclarationBuilder<T> skipEqualityCheck() {
		this.skipEqualityCheck = true;
		this.equalityChecker = (a, b) -> {
			throw new UnsupportedOperationException("cannot check for equality");
		};
		return this;
	}

	TestWorldEntryDeclaration<T> constructThis() {
		Objects.requireNonNull(name);
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(serializer);
		Objects.requireNonNull(deserializer);
		Objects.requireNonNull(extractor);
		Objects.requireNonNull(equalityChecker);
		return new TestWorldEntryDeclaration<>(
				name,
				clazz,
				serializer,
				deserializer,
				extractor,
				equalityChecker,
				skipEqualityCheck);
	}

	public TestWorldDirectoryDeclaration create() {
		return parent.create();
	}

	public <E> TestWorldEntryDeclarationBuilder<E> entry(String name, Class<E> clazz) {
		return parent.entry(name, clazz);
	}
}
