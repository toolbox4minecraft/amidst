package amidst.map;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ByteArrayHub {
	private File path;
	private long key;
	private byte unitSize;
	private int maxUnits;
	private int dataHop;
	public int activeFragments = 0;
	
	private byte[] data;
	private byte[] returnCache;
	
	private int unitOffset;
	
	public ByteArrayHub(long key, byte unitSize, int maxUnits, File basePath) {
		this.unitSize = unitSize;
		this.maxUnits = maxUnits;
		this.key = key;
		
		unitOffset = maxUnits*unitSize;
		
		path = new File(basePath, Long.toHexString(key).toUpperCase() + ".acache");
		data = new byte[unitOffset*ByteArrayCache.CACHE_MAX_SIZE + ByteArrayCache.HEADER_SIZE];
		returnCache = new byte[unitSize*maxUnits];
		if (path.exists()) {
			GZIPInputStream inStream;
			try {
				inStream = new GZIPInputStream(new FileInputStream(path));
				byte[] readBuffer = new byte[1024];
				int len;
				int offset = 0;
				while ((len = inStream.read(readBuffer)) != -1) {
					System.arraycopy(readBuffer, 0, data, offset, len);
					offset += len;
				}
				//Log.i("@@@@@ " + inStream.read(data));
				inStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public boolean exists(int id) {
		return (((data[id >> 3] >> (id % 8)) & 0x1) == 0x1);
	}
	public byte[] get(int id) {
		System.arraycopy(data, ByteArrayCache.HEADER_SIZE + unitOffset*id, returnCache, 0, unitOffset);
		return returnCache;
	}
	public void put(int id, byte[] indata) {
		System.arraycopy(indata, 0, data, ByteArrayCache.HEADER_SIZE + unitOffset*id, unitOffset);
		data[id >> 3] |= 0x1 << (id % 8);
	}
	
	public void unload() {
		try {
			BufferedOutputStream outStream = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(path)));
			outStream.write(data, 0, data.length);
			outStream.flush();
			outStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public long getKey() {
		return key;
	}

}
