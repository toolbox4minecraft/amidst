package amidst.minecraft.remote;

public class NetGetBiomeDataRequest {
	public int x, y, width, height;
	public boolean useQuarterResolutionMap;
	public NetGetBiomeDataRequest() {
		
	}
	public NetGetBiomeDataRequest(int x, int y, int width, int height, boolean useQuarterResolutionMap) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.useQuarterResolutionMap = useQuarterResolutionMap;
	}
}
