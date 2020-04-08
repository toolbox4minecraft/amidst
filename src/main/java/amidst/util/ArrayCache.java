package amidst.util;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public class ArrayCache<T> {

	private final ThreadLocal<T> array;
	private final IntFunction<T> constructor;
	private final ToIntFunction<T> lengthGetter;

	private ArrayCache(IntFunction<T> constructor, ToIntFunction<T> lengthGetter, int initialSize) {
		this.constructor = constructor;
		this.lengthGetter = lengthGetter;
		this.array = new ThreadLocal<T>() {
			@Override
			protected T initialValue() {
                return constructor.apply(initialSize);
			}
		};
	}

	public T getArray(int minimalSize) {
		T dataArray = array.get();

		int cur = lengthGetter.applyAsInt(dataArray);
		if (minimalSize <= cur)
			return dataArray;

		while (cur < minimalSize)
			cur *= 2;

		dataArray = constructor.apply(cur);
		array.set(dataArray);
		return dataArray;
	}

	public static ArrayCache<int[]> makeIntArrayCache(int initialSize) {
		return new ArrayCache<>(int[]::new, a -> a.length, initialSize);
	}
}
