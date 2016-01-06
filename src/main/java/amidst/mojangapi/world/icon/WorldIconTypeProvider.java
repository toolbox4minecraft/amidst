package amidst.mojangapi.world.icon;

public interface WorldIconTypeProvider {
	DefaultWorldIconTypes get(int x, int y);
}
