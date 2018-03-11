package amidst.gameengineabstraction;

public enum GameEngineType {

	MINECRAFT  ("Minecraft", CoordinateSystem.RIGHT_HANDED),
	MINETEST   ("Minetest",  CoordinateSystem.LEFT_HANDED);
	
	private final String name;
	private final CoordinateSystem coordinateSystem;

	private GameEngineType(String name, CoordinateSystem coordinate_system) {
		this.name = name;
		this.coordinateSystem = coordinate_system;
	}

	public String getName() {
		return name;
	}
	
	public CoordinateSystem getGameCoordinateSystem() {
		return coordinateSystem;
	}	
}
