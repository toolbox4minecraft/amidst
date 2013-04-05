package MoF;

import amidst.Options;
import amidst.map.MapMarkers;
import amidst.map.MapObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Fragment extends BufferedImage {
	public int x, y, range;
	public boolean active;
	public List<MapObject> objects;
	public int tempX, tempY;
	public byte[] data;
	public boolean marked = false;
	public List<BufferedImage> layers;
	public float stat[];
	public int strongholdCount = 0;
	public int villageCount = 0;
	
	public Fragment(int x, int y, int range) {
		super(range, range, BufferedImage.TYPE_INT_ARGB);
		this.range = range;
		this.x = x;
		this.y = y;
		active = true;
		Graphics2D g2d = this.createGraphics();
		g2d.setColor(Color.blue);
		g2d.fillRect(0, 0, Project.FRAGMENT_SIZE, Project.FRAGMENT_SIZE);
		objects = new ArrayList<MapObject>();
		layers = new ArrayList<BufferedImage>();
		stat = new float[Biome.colors.length];
	}
	
	public void dispose() {
		this.flush();
		for (BufferedImage layer : layers)
			layer.flush();
		this.objects.clear();
		this.objects = null;
		this.data = null;
		this.layers.clear();
		this.layers = null;
		this.stat = null;
		active = false;
	}
	
	public void disable() {
		this.active = false;
		for (BufferedImage layer : layers)
			layer.flush();
		flush();
	}
	
	public Graphics2D newLayer() {
		BufferedImage temp = new BufferedImage(Project.FRAGMENT_SIZE, Project.FRAGMENT_SIZE, BufferedImage.TYPE_INT_ARGB);
		layers.add(temp);
		
		return temp.createGraphics();
	}
	
	public void paint(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.drawImage(this, x, y, width, height, null);
		if (Options.instance.showGrid.isSelected() & (layers.size()>0))
			g2d.drawImage(layers.get(0), x, y, width, height, null);
		if (Options.instance.showSlimeChunks.isSelected() & (layers.size()>1))
			g2d.drawImage(layers.get(1), x, y, width, height, null);
	}
	
	public int getBiomeAt(int x, int y) {
		if ((data != null)&&(y*Project.FRAGMENT_SIZE + x < data.length))
			return data[y*Project.FRAGMENT_SIZE + x];
		else
			return 255;
	}
	
	public boolean isInside(int x, int y) {
		int sX = x >> 2;
		int sY = y >> 2;
		int fs = Project.FRAGMENT_SIZE;
		sX -= this.x*fs;
		sY -= this.y*fs;
		return (sX >= 0) & (sY >= 0) & (sX < fs) & (sY < fs);
	}
	
	public MapObject getObjectAt(int x, int y) {
		MapObject m = null;
		double td = 1600;
		for (MapObject object : objects) {
			if (object.isSelectable()) {
				int sx = x - (object.x >> 2);
				int sy = y - (object.y >> 2);
				double dist = sx * sx + sy * sy;
				if (dist < td) {
					td = dist;
					object.tempDist = dist;
					m = object;
				}
			}
		}
		return m;
	}
	
	public void addMapObject(MapObject m) {
		m.rx = (m.x >> 2) - (this.x * Project.FRAGMENT_SIZE);
		m.ry = (m.y >> 2) - (this.y * Project.FRAGMENT_SIZE);
		if (m.type == MapMarkers.STRONGHOLD) {
			strongholdCount++;
		} else if (m.type == MapMarkers.VILLAGE) {
			villageCount++;
		}
		objects.add(m);
	}
}
