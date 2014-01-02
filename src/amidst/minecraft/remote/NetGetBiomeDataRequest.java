package amidst.minecraft.remote;

public class NetGetBiomeDataRequest {
	public int x, y, width, height;
	public NetGetBiomeDataRequest() {
		
	}
	public NetGetBiomeDataRequest(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
