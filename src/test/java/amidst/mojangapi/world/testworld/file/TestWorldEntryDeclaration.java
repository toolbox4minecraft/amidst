package amidst.mojangapi.world.testworld.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;

@Immutable
public class TestWorldEntryDeclaration<T> {
	private final String name;
	private final Class<T> clazz;
	private final BiConsumer<OutputStream, T> serializer;
	private final Function<InputStream, T> deserializer;
	private final Function<World, T> extractor;
	private final BiPredicate<T, T> equalityChecker;
	private final boolean skipEqualityCheck;

	public TestWorldEntryDeclaration(
			String name,
			Class<T> clazz,
			BiConsumer<OutputStream, T> serializer,
			Function<InputStream, T> deserializer,
			Function<World, T> extractor,
			BiPredicate<T, T> equalityChecker,
			boolean skipEqualityCheck) {
		this.name = name;
		this.clazz = clazz;
		this.serializer = serializer;
		this.deserializer = deserializer;
		this.extractor = extractor;
		this.equalityChecker = equalityChecker;
		this.skipEqualityCheck = skipEqualityCheck;
	}

	public String getName() {
		return name;
	}

	public void writeToStream(OutputStream os, Map<String, Object> data) {
		serializer.accept(os, extractFromDataMap(data));
	}

	public T readFromStream(InputStream is) {
		return deserializer.apply(is);
	}

	public T extractFromWorld(World world) {
		return extractor.apply(world);
	}

	public T extractFromDataMap(Map<String, Object> data) {
		return getAsClazz(getEntry(data));
	}

	private Object getEntry(Map<String, Object> data) {
		if (data.containsKey(name)) {
			return data.get(name);
		} else {
			throw new IllegalArgumentException("unknown entry: " + name);
		}
	}

	@SuppressWarnings("unchecked")
	private T getAsClazz(Object object) {
		if (isMatchingClazz(object.getClass())) {
			return (T) object;
		} else {
			throw new ClassCastException(clazz + " is not assignable from " + object.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	public <E> TestWorldEntryDeclaration<E> getAsEntryWithClazz(Class<E> clazz) {
		if (isMatchingClazz(clazz)) {
			return (TestWorldEntryDeclaration<E>) this;
		} else {
			throw new ClassCastException(clazz + " is not assignable from " + clazz);
		}
	}

	private boolean isMatchingClazz(Class<?> clazz) {
		return this.clazz.isAssignableFrom(clazz);
	}

	public boolean isDataMapAndWorldEntryEqual(Map<String, Object> data, World world) {
		if (skipEqualityCheck) {
			return true;
		} else {
			return equalityChecker.test(extractFromDataMap(data), extractFromWorld(world));
		}
	}
}
