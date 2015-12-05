package amidst.minecraft.remote;

public class NetGetBiomeDataRequest {
	public int x, y, width, height;
	public boolean useQuarterResolution;

	public NetGetBiomeDataRequest() {
	}

	public NetGetBiomeDataRequest(int x, int y, int width, int height,
			boolean useQuarterResolution) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.useQuarterResolution = useQuarterResolution;
	}
}
