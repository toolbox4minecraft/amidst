package amidst.mojangapi.world.test;

import amidst.mojangapi.world.biome.BiomeData;
import org.junit.Assert;
import org.junit.Test;

public class TestBiomeData {

	private static int DATA_WIDTH = 16;
	private static int DATA_HEIGHT = 24;
	
	private BiomeData makeData() {
		int[] arr = new int[DATA_WIDTH*DATA_HEIGHT];
		for(int i = 0; i < arr.length; i++)
			arr[i] = i;
		
		return new BiomeData(arr, DATA_WIDTH, DATA_HEIGHT);
	}
	
	private void testView(BiomeData data, int offX, int offY, int w, int h) {
		BiomeData view = data.view(offX, offY, w, h);
		
		//Test that the view correctly map indexes
		for(int i = 0; i < w; i++) {
			for(int j = 0; j < h; j++) {
				Assert.assertEquals(data.get(i+offX, j+offY), view.get(i, j));
			}
		}
		
		view.makeOwned();
		//Test that the view correctly copied the data
		for(int i = 0; i < w; i++) {
			for(int j = 0; j < h; j++) {
				Assert.assertEquals(data.get(i+offX, j+offY), view.get(i, j));
			}
		}
	}
	
	private void testAllViews(BiomeData data) {
		int width = data.getWidth();
		int height = data.getHeight();
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				for(int w = 0; w < width-i; w++) {
					for(int h = 0; h < height-j; h++)
						testView(data, i, j, w, h);
				}
			}
		}
	
	}
	
	@Test
	public void testViews() {
		BiomeData data = makeData();
		
		testAllViews(data);
		testAllViews(data.view(5, 5, 10, 15));
	}
}
