package amidst.mojangapi.world.icon.type;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.Coordinates;

@ThreadSafe
public interface WorldIconTypeProvider<T> {
	DefaultWorldIconTypes get(int x, int y, T additionalData);

	default DefaultWorldIconTypes get(Coordinates location, T additionalData) {
		return get((int) location.getX(), (int) location.getY(), additionalData);
	}
}
