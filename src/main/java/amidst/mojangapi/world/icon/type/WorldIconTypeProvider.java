package amidst.mojangapi.world.icon.type;

public interface WorldIconTypeProvider {
	DefaultWorldIconTypes get(int x, int y);
}
