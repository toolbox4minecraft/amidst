package amidst.utilities;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

import amidst.documentation.Immutable;

@Immutable
public class JavaUtils {
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(List<? extends T> list,
			Class<? extends T> clazz) {
		T[] array = (T[]) Array.newInstance(clazz, list.size());
		return list.toArray(array);
	}

	public static <T> void addAll(Set<T> set, T[] array) {
		for (T entry : array) {
			set.add(entry);
		}
	}
}
