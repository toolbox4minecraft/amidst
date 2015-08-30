package amidst.utilties;

import java.lang.reflect.Array;
import java.util.List;

public class JavaUtils {
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(List<? extends T> list,
			Class<? extends T> clazz) {
		T[] array = (T[]) Array.newInstance(clazz, list.size());
		return list.toArray(array);
	}
}
