package amidst.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class ArrayCache<A> {

	private final ConcurrentLinkedQueue<A> arrays;
	private final IntFunction<A> constructor;
	private final ToIntFunction<A> lengthGetter;
	private final int initialSize;

	private ArrayCache(IntFunction<A> constructor, ToIntFunction<A> lengthGetter, int initialSize) {
		this.arrays = new ConcurrentLinkedQueue<>();
		this.constructor = constructor;
		this.lengthGetter = lengthGetter;
		this.initialSize = initialSize;
	}

	public<T, E extends Throwable> T withArrayFaillible(
			int minimalSize,
			FaillibleFunction<A, T, E> arrayMapper
		) throws E {
		int requiredSize = Math.max(initialSize, minimalSize);
		A dataArray = arrays.poll();

		if (dataArray == null) {
			// No arrays available in queue: create a fresh one
			dataArray = constructor.apply(requiredSize);
		} else {
			int cur = lengthGetter.applyAsInt(dataArray);

			// Array is too small: create a larger one
			if (cur < requiredSize) {
				do {
					cur = Math.max(1, cur*2);
				} while (cur < requiredSize);
				dataArray = constructor.apply(cur);
			}
		}

		try {
			return arrayMapper.apply(dataArray);
		} finally {
			// We're done with this array, put it back in the queue
			arrays.add(dataArray);
		}
	}

	public<T> T withArray(int minimalSize, Function<A, T> arrayMapper) {
		return this.withArrayFaillible(minimalSize, (FaillibleFunction<A, T, RuntimeException>) arrayMapper::apply);
	}

	public static ArrayCache<int[]> makeIntArrayCache(int initialSize) {
		return new ArrayCache<>(int[]::new, a -> a.length, initialSize);
	}
}
