package amidst.mojangapi.world.icon;

public class ImmutableWorldIconTypeProvider implements WorldIconTypeProvider {
	private final DefaultWorldIconTypes worldIconType;

	public ImmutableWorldIconTypeProvider(DefaultWorldIconTypes worldIconType) {
		this.worldIconType = worldIconType;
	}

	@Override
	public DefaultWorldIconTypes get(int x, int y) {
		return worldIconType;
	}
}
