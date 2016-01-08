package amidst.mojangapi.world.icon.type;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public interface WorldIconTypeProvider<T> {
	DefaultWorldIconTypes get(int x, int y, T additionalData);
}
