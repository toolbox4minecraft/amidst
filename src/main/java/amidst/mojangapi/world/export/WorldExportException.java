package amidst.mojangapi.world.export;

public class WorldExportException extends Exception {
	private static final long serialVersionUID = 8652575123411388901L;
	
	public WorldExportException() {
		super();
	}
	
	public WorldExportException(String message) {
		super(message);
	}
}
