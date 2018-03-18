package amidst.gameengineabstraction;

public enum GameEngineType {

	MINECRAFT  ("Minecraft", "MC", CoordinateSystem.RIGHT_HANDED),
	MINETEST   ("Minetest",  "MT", CoordinateSystem.LEFT_HANDED);
	
	private final String name;
	private final String abbreviatedName;
	private final CoordinateSystem coordinateSystem;

	private GameEngineType(String name, String abbreviated_name, CoordinateSystem coordinate_system) {
		this.name = name;
		this.coordinateSystem = coordinate_system;
		this.abbreviatedName = abbreviated_name;
	}

	public String getName() {
		return name;
	}

	public String getAbbreviatedName() {
		return abbreviatedName;
	}
	
	public CoordinateSystem getGameCoordinateSystem() {
		return coordinateSystem;
	}	
}
