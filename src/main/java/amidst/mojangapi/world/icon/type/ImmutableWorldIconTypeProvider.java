package amidst.mojangapi.world.icon.type;

import amidst.documentation.Immutable;

@Immutable
public class ImmutableWorldIconTypeProvider implements WorldIconTypeProvider<Void> {
	private final DefaultWorldIconTypes worldIconType;

	public ImmutableWorldIconTypeProvider(DefaultWorldIconTypes worldIconType) {
		this.worldIconType = worldIconType;
	}

	@Override
	public DefaultWorldIconTypes get(int x, int y, Void additionalData) {
		return worldIconType;
	}
}
