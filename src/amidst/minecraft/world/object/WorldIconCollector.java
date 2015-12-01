package amidst.minecraft.world.object;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WorldIconCollector implements WorldIconConsumer {
	private List<WorldIcon> worldObjects;

	@Override
	public void consume(WorldIcon worldObject) {
		initListIfNecessary();
		worldObjects.add(worldObject);
	}

	private void initListIfNecessary() {
		if (worldObjects == null) {
			worldObjects = new LinkedList<WorldIcon>();
		}
	}

	public List<WorldIcon> get() {
		if (worldObjects == null) {
			return Collections.emptyList();
		} else {
			return worldObjects;
		}
	}
}
