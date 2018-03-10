package amidst.gameengineabstraction;

public enum GameEngineType {

	MINECRAFT ("Minecraft"),
	MINETESTv7 ("Minetest v7");
	
	private final String name;

	private GameEngineType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
