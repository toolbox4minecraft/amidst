package MoF;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Fragment extends  BufferedImage {
	public int x, y, range;
	public boolean active;
	public ArrayList<MapObject> objects;
	public int tempX, tempY;
	public byte[] data;
	public boolean marked = false;
	public ArrayList<BufferedImage> layers;
	private Project proj;
	public float stat[];
	public int strongholdCount = 0;
	public int villageCount = 0;
	
	public Fragment(int x, int y, int range, Project proj) {
		super(range,range,BufferedImage.TYPE_INT_ARGB);
		this.range = range;
		this.x = x;
		this.y = y;
		active = true;
    	Graphics2D g2d = this.createGraphics();
    	g2d.setColor(Color.blue);
    	g2d.fillRect(0, 0, Project.FRAGMENT_SIZE, Project.FRAGMENT_SIZE);
    	objects = new ArrayList<MapObject>();
    	layers = new ArrayList<BufferedImage>();
    	this.proj = proj;
    	stat = new float[Biome.colors.length];
	}
	public void dispose() {

		this.flush();
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).flush();
		}
		this.objects.clear();
		this.objects = null;
		this.data = null;
		this.layers.clear();
		this.layers = null;
		this.proj = null;
		this.stat = null;
		active = false;
	}
	public void disable() {
		this.active = false;
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).flush();
		}
		flush();
	}
	
	public Graphics2D newLayer() {
		BufferedImage temp = new BufferedImage(Project.FRAGMENT_SIZE, Project.FRAGMENT_SIZE, BufferedImage.TYPE_INT_ARGB);
		layers.add(temp);
		
		return temp.createGraphics();
	}
	
	public void paint(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.drawImage(this, x, y, width, height, null);
		if (proj.grid&(layers.size()>0))
			g2d.drawImage(layers.get(0), x, y, width, height, null);
		if (proj.slimes&(layers.size()>1))
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
		if ((sX >= 0) & (sY >= 0) & (sX < fs) & (sY < fs)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public MapObject getObjectAt(int x, int y) {
		MapObject m = null;
		double td = 1600;
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).isSelectable()) {
				int sx = x - (objects.get(i).x >> 2);
				int sy = y - (objects.get(i).y >> 2);
				double dist = sx*sx + sy*sy;
				if (dist < td) {
					td = dist;
					objects.get(i).tempDist = dist;
					m = objects.get(i);
				}
			}
		}
		return m;
	}
	
	public void addMapObject(MapObject m) {
		int sX = m.x >> 2;
		int sY = m.y >> 2;
		int fs = Project.FRAGMENT_SIZE;
		sX -= this.x*fs;
		sY -= this.y*fs;
		m.rx = sX;
		m.ry = sY;
		if (m.type.toLowerCase().equals("stronghold")) {
			strongholdCount++;
		} else if (m.type.toLowerCase().equals("village")) {
			villageCount++;
		}
		objects.add(m);
	}
}
