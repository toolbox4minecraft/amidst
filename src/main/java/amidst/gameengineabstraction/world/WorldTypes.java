package amidst.gameengineabstraction.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import amidst.mojangapi.world.WorldType;

public class WorldTypes {

	private final WorldType[] selectable_AsArray;
	private final List<WorldType> selectable_AsList;
	private final String[] availableSettings;
	
    /**
     * @param selectable_world_types Not all game engines (or versions) will support all WorldTypes 
	 * so specify the list of WorldType which should be presented to the user
	 * for the current game-engine/version.
     */
	public WorldTypes(WorldType[] selectable_world_types) {
		this.selectable_AsArray = selectable_world_types;
		this.selectable_AsList = Arrays.asList(selectable_AsArray);
		this.availableSettings = Stream.concat(
				Stream.of(WorldType.PROMPT_EACH_TIME),
				selectable_AsList.stream()
						.map(worldType -> worldType.getName()))
						.toArray(String[]::new);
	}

	public List<WorldType> getSelectable() {
		return selectable_AsList;
	}
	
	public WorldType[] getSelectableArray() {
		return selectable_AsArray;
	}
	
	public String[] getWorldTypeSettingAvailableValues() {
		return availableSettings;
	}
	
	public boolean contains(WorldType worldType) {
		return selectable_AsList.contains(worldType);
	}

	public boolean contains(String worldTypeName) {
		for(WorldType worldType: selectable_AsList) {
			if (worldType.getName().equals(worldTypeName)) return true;
		}
		return false;
	}	
}
