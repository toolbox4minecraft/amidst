package amidst.minecraft.world.object;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WorldObjectCollector implements WorldObjectConsumer {
	private List<WorldObject> worldObjects;

	@Override
	public void consume(WorldObject worldObject) {
		initListIfNecessary();
		worldObjects.add(worldObject);
	}

	private void initListIfNecessary() {
		if (worldObjects == null) {
			worldObjects = new LinkedList<WorldObject>();
		}
	}

	public List<WorldObject> get() {
		if (worldObjects == null) {
			return Collections.emptyList();
		} else {
			return worldObjects;
		}
	}
}
