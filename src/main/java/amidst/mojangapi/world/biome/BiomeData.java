package amidst.mojangapi.world.biome;

import amidst.util.BiomeFunction;
import amidst.util.BiomePredicate;

/**
 * This class encapsulate a buffer containing the biome data of a
 * rectangular region.
 * 
 * The 2D data is stored in row-major order to be directly compatible
 * with the arrays handed by the {@link MinecraftInterface}.
 * 
 * This class supports the creation of views into sub-regions of the
 * buffer, without copying any data. However, these views don't own their
 * buffer, and thus will be invalidated when the original buffer is modified.
 * If you need to store the buffer somewhere, make sure to call {@link #makeOwned()}
 * to ensure that the buffer owns its contents.
 */
public class BiomeData {
	//We use an int array so we can directly reference the
	//Minecraft arrays, without any copying.
	private int[] data;
	
	private int stride; //number of elems between two rows
	private int width;  //size of a row
	private int height; //number of rows
	private int offset; //offset before first element
	
	private boolean isOwned;
	
	private BiomeData(int[] data, int width, int height, int stride, int offset, boolean isOwned) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.stride = stride;
		this.offset = offset;
		this.isOwned = isOwned;
		
		if(stride < width)
			throw new IllegalArgumentException("invalid stride");

		if(offset < 0 || width < 0 || height < 0 || translateIndex(width-1, height-1) >= data.length)
			throw new IllegalArgumentException("invalid width/height/offset");
	}
	
	public BiomeData(int[] data, int width, int height) {		
		this(data == null ? new int[width*height] : data, width, height, width, 0, true);
	}
	
	public BiomeData view() {
		return new BiomeData(data, width, height, stride, offset, false);
	}
	
	public BiomeData view(int offX, int offY, int w, int h) {
		if(offX < 0 || offY < 0 || offX+w > width || offY+h > height)
			throw new IllegalArgumentException("invalid offsets");
		
		return new BiomeData(data, w, h, stride, translateIndex(offX, offY), false);
	}
	
	public void copyFrom(BiomeData other) {
		width = other.width;
		height = other.height;
		stride = other.stride;
		offset = other.offset;
		
		if(isOwned) {
			if(other.getSize() > data.length)
				data = new int[other.getSize()];
			
			fillData(other.data);
		} else {
			data = other.data;
		}
	}
	
	public boolean isOwned() {
		return isOwned;
	}
	
	public void makeOwned() {
		if(isOwned)
			return;

		int[] old = data;
		data = new int[getWidth()*getHeight()];
		fillData(old);
		
		isOwned = true;
	}
	
	private void fillData(int[] src) {
		if(stride == width) {
			//no gaps: we can do a single copy
			System.arraycopy(src, translateIndex(0, 0), data, 0, getSize());
		} else {
			for(int j = 0; j < height; j++)
				System.arraycopy(src, translateIndex(0, j), data, j*width, width);
		}
		
		width = getWidth();
		height = getHeight();
		stride = width;
		offset = 0;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getSize() {
		return getWidth()*getHeight();
	}
	
	public short get(int x, int y) {
		return (short) data[translateIndex(x, y)];
	}
	
	public<T> T findFirst(BiomeFunction<T> fn) {
		int start = offset;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				T obj = fn.apply(x, y, (short) data[start+x]);
				if(obj != null)
					return obj;
			}
			start += stride;
		}
		
		return null;
	}
	
	public boolean checkAll(BiomePredicate pred) {
		int start = offset;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(!pred.test(x, y, (short) data[start+x]))
					return false;
			}
			start += stride;
		}

		return true;
	}
	
	private int translateIndex(int x, int y) {
		return offset + y*stride + x;
	}
	
}
