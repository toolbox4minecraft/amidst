package amidst.mojangapi.mocking;

import java.util.function.Consumer;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@Immutable
public class FragmentCornerWalker {
	public static FragmentCornerWalker walkFragmentsAroundOrigin(int fragmentsAroundOrigin) {
		long blocksAroundOrigin = fragmentsAroundOrigin * Fragment.SIZE;
		CoordinatesInWorld startCorner = CoordinatesInWorld.from(-blocksAroundOrigin, -blocksAroundOrigin);
		CoordinatesInWorld endCorner = startCorner.add(2 * blocksAroundOrigin, 2 * blocksAroundOrigin);
		return new FragmentCornerWalker(startCorner, endCorner);
	}

	private final CoordinatesInWorld startCorner;
	private final CoordinatesInWorld endCorner;

	public FragmentCornerWalker(CoordinatesInWorld startCorner, CoordinatesInWorld endCorner) {
		this.startCorner = startCorner;
		this.endCorner = endCorner;
	}

	public <T> void walk(Consumer<CoordinatesInWorld> consumer) {
		for (long x = startCorner.getX(); x < endCorner.getX(); x += Fragment.SIZE) {
			for (long y = startCorner.getY(); y < endCorner.getY(); y += Fragment.SIZE) {
				consumer.accept(CoordinatesInWorld.from(x, y));
			}
		}
	}
}
