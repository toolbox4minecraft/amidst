package amidst.mojangapi.world.icon.type;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public interface WorldIconTypeProvider {
	DefaultWorldIconTypes get(int x, int y);
}
