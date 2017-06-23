package amidst.mojangapi.mocking;

import java.util.function.Consumer;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.coordinates.Coordinates;

@Immutable
public class FragmentCornerWalker {
	public static FragmentCornerWalker walkFragmentsAroundOrigin(int fragmentsAroundOrigin) {
		int blocksAroundOrigin = fragmentsAroundOrigin * Fragment.SIZE;
		Coordinates startCorner = Coordinates.from(-blocksAroundOrigin, -blocksAroundOrigin);
		Coordinates endCorner = startCorner.add(2 * blocksAroundOrigin, 2 * blocksAroundOrigin);
		return new FragmentCornerWalker(startCorner, endCorner);
	}

	private final Coordinates startCorner;
	private final Coordinates endCorner;

	public FragmentCornerWalker(Coordinates startCorner, Coordinates endCorner) {
		this.startCorner = startCorner;
		this.endCorner = endCorner;
	}

	public <T> void walk(Consumer<Coordinates> consumer) {
		for (int x = startCorner.getX(); x < endCorner.getX(); x += Fragment.SIZE) {
			for (int y = startCorner.getY(); y < endCorner.getY(); y += Fragment.SIZE) {
				consumer.accept(Coordinates.from(x, y));
			}
		}
	}
}
