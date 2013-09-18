package amidst.map;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class MipMapImage {
	private BufferedImage[] images;
	private int[][] imgDataBuffers;
	public int width, height;
	
	public MipMapImage(int width, int height, int levels, boolean transparent) {
		images = new BufferedImage[levels];
		imgDataBuffers = new int[levels][];

		this.width = width;
		this.height = height;
		
		int imageType = transparent?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB;
		for (int i = 0; i < levels; i++) {
			images[i] = new BufferedImage(width, height, imageType);
			width >>= 1;
			height >>= 1;
			imgDataBuffers[i] = ((DataBufferInt)images[i].getRaster().getDataBuffer()).getData();
		}
	}
	
	public void setData(int[] data) {
		System.arraycopy(data, 0, imgDataBuffers[0], 0, imgDataBuffers[0].length);
		for (int i = 1; i < imgDataBuffers.length; i++) {
			int bufferIndex = 0;
			for (int y = 0; y < images[i].getHeight(); y++) {
				for (int x = 0; x < images[i].getWidth(); x++) {
					int[] buffer = imgDataBuffers[i-1];
					int dataIndex = (x << 1) + images[i-1].getWidth()*(y << 1);
					int c1 = buffer[dataIndex];
					int c2 = buffer[dataIndex+1];
					dataIndex += images[i-1].getWidth();
					int c3 = buffer[dataIndex];
					int c4 = buffer[dataIndex+1];
					
					imgDataBuffers[i][bufferIndex] = blendColors(c1, c2, c3, c4);
					bufferIndex++;
				}
			}
		}
	}
	
	public int blendColors(int c1, int c2, int c3, int c4) {
		int a = 0, r = 0, g = 0, b = 0;
		a += (c1 >> 24) & 0xFF;
		a += (c2 >> 24) & 0xFF;
		a += (c3 >> 24) & 0xFF;
		a += (c4 >> 24) & 0xFF;
		a >>= 2;
		
		r += (c1 >> 16) & 0xFF;
		r += (c2 >> 16) & 0xFF;
		r += (c3 >> 16) & 0xFF;
		r += (c4 >> 16) & 0xFF;
		r >>= 2;
		
		g += (c1 >> 8) & 0xFF;
		g += (c2 >> 8) & 0xFF;
		g += (c3 >> 8) & 0xFF;
		g += (c4 >> 8) & 0xFF;
		g >>= 2;
		
		b += (c1) & 0xFF;
		b += (c2) & 0xFF;
		b += (c3) & 0xFF;
		b += (c4) & 0xFF;
		b >>= 2;
		return (a << 24) | (r << 16) | (g << 8) | (b);
	}
	
	public BufferedImage getImage(int level) {
		return images[level];
	}

	public void flush() {
		for (int i = 0; i < images.length; i++)
			images[i].flush();
	}
}
