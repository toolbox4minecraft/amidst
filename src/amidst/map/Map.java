package amidst.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Stack;


import MoF.ChunkManager;
import amidst.Amidst;

import amidst.Log;

public class Map {
	private static final boolean START = true, END = false;
	private static final AffineTransform iMat = new AffineTransform();
	private FragmentManager fManager;
	
	private Fragment startNode = new Fragment();
	
	private double drawPadding = Fragment.SIZE;
	
	private double scale = 1.0, startX, startY;
	
	private int sBlockX = 0;
	private int eBlockX = 0;
	private int tileWidth, tileHeight;
	
	private boolean isLoaded = false;
	
	private Object resizeLock = new Object();
	private AffineTransform mat;
	private ChunkManager chunkManager;
	
	// TODO : This must be changed with the removal of ChunkManager
	public Map(ChunkManager manager) {
		chunkManager = manager;
		fManager = new FragmentManager();
		mat = new AffineTransform();
	}
	
	public void draw(Graphics2D g) {
		if (!isLoaded) return;
		// TODO : Change this to directly reference the window that renders it.
		int width = Amidst.getActiveWindow().getWidth();
		int height = Amidst.getActiveWindow().getHeight();
		
		double size = ((double)Fragment.SIZE)*scale;
		int w = (width) / (int)size + 2;
		int h = (height) / (int)size + 2;
		
		while (tileWidth <  w) addColumn(END);
		while (tileWidth >  w) removeColumn(END);
		while (tileHeight < h) addRow(END);
		while (tileHeight > h) removeRow(END);
		
		while (startX >     0) { startX -= size; addColumn(START); removeColumn(END);   }
		while (startX < -size) { startX += size; addColumn(END);   removeColumn(START); }
		while (startY >     0) { startY -= size; addRow(START);    removeRow(END);      }
		while (startY < -size) { startY += size; addRow(END);      removeRow(START);    }
		
		//g.setColor(Color.pink);
		//g.fillRect(5, 5, width - 10, height - 10);
		
		Fragment frag = startNode;
		size = (double)Fragment.SIZE;
		if (frag.hasNext) {
			Fragment corner = frag.nextFragment;
			double drawX = startX;
			double drawY = startY;

			mat.setToIdentity();
			mat.translate(drawX, drawY);
			mat.scale(scale, scale);
			while (frag.hasNext) {
				frag = frag.nextFragment;
				frag.draw(g, mat);
				mat.translate(size, 0);
				if (frag.endOfLine) {
					mat.translate(-size * w, size);
				}
			}
			
		}
		g.setTransform(iMat);
		/*if (frag != null) {
			frag.draw(g, 0, 0, Fragment.SIZE);
		}*/
	}
	public void addStart(int x, int y) {
		synchronized (resizeLock) {
			Fragment start = fManager.requestFragment(x, y);
			start.endOfLine = true;
			startNode.setNext(start);
			tileWidth = 1;
			tileHeight = 1;
		}
	}
	public void clear() {
		
	}
	public void addColumn(boolean start) {
		synchronized (resizeLock) {
			int x = 0;
			Fragment frag = startNode;
			if (start) {
				x = frag.nextFragment.blockX - Fragment.SIZE;
				Fragment newFrag = fManager.requestFragment(x, frag.nextFragment.blockY);
				newFrag.setNext(startNode.nextFragment);
				startNode.setNext(newFrag);
			}
			while (frag.hasNext) {
				frag = frag.nextFragment;
				if (frag.endOfLine) {
					if (start) {
						if (frag.hasNext) {
							Fragment newFrag = fManager.requestFragment(x, frag.blockY + Fragment.SIZE);
							newFrag.setNext(frag.nextFragment);
							frag.setNext(newFrag);
							frag = newFrag;
						}
					} else {
						Fragment newFrag = fManager.requestFragment(frag.blockX + Fragment.SIZE, frag.blockY);
						
						if (frag.hasNext) {
							newFrag.setNext(frag.nextFragment);
						}
						newFrag.endOfLine = true;
						frag.endOfLine = false;
						frag.setNext(newFrag);	
						frag = newFrag;
						
					}
				}
			}
			tileWidth++;
		}
	}
	public void removeRow(boolean start) {
		synchronized (resizeLock) {
			if (start) {
				for (int i = 0; i < tileWidth; i++) {
					Fragment frag = startNode.nextFragment;
					frag.remove();
					fManager.returnFragment(frag);
				}
			} else {
				Fragment frag = startNode;
				while (frag.hasNext)
					frag = frag.nextFragment;
				for (int i = 0; i < tileWidth; i++) {
					frag.remove();
					fManager.returnFragment(frag);
					frag = frag.prevFragment;
				}
			}
			tileHeight--;
		}
	}
	public void addRow(boolean start) {
		synchronized (resizeLock) {
			Fragment frag = startNode;
			int y;
			if (start) {
				frag = startNode.nextFragment;
				y = frag.blockY - Fragment.SIZE;
			} else {
				while (frag.hasNext)
					frag = frag.nextFragment;
				y = frag.blockY + Fragment.SIZE;
			}
			
			tileHeight++;
			Fragment newFrag = fManager.requestFragment(startNode.nextFragment.blockX, y);
			Fragment chainFrag = newFrag;
			for (int i = 1; i < tileWidth; i++) {
				Fragment tempFrag = fManager.requestFragment(chainFrag.blockX + Fragment.SIZE, chainFrag.blockY);
				chainFrag.setNext(tempFrag);
				chainFrag = tempFrag;
				if (i == (tileWidth - 1))
					chainFrag.endOfLine = true;
			}
			if (start) {
				chainFrag.setNext(frag);
				startNode.setNext(newFrag);
			} else {
				frag.setNext(newFrag);
			}
		}

	}
	public void removeColumn(boolean start) {
		synchronized (resizeLock) {
			Fragment frag = startNode;
			if (start) {
				fManager.returnFragment(frag.nextFragment);
				startNode.nextFragment.remove();
			}
			while (frag.hasNext) {
				frag = frag.nextFragment;
				if (frag.endOfLine) {
					if (start) {
						if (frag.hasNext) {
							Fragment tempFrag = frag.nextFragment;
							tempFrag.remove();
							fManager.returnFragment(tempFrag);
						}
					} else {
						frag.prevFragment.endOfLine = true;
						frag.remove();
						fManager.returnFragment(frag);
						frag = frag.prevFragment;
					}
				}
			}
			tileWidth--;
		}
	}
	
	public void moveBy(double x, double y) {
		startX += x;
		startY += y;
	}
	
	public void centerOn(int x, int y) {
		
	}
	
	public void addLayer(Layer layer) {
		layer.setChunkManager(chunkManager);
		fManager.addLayer(layer);
	}
	
	public void load() {
		fManager.load();
		fManager.start();

		addStart(0, 0);
		isLoaded = true;

	}
	public void setZoom(double scale) {
		this.scale = scale;
	}
	public double getZoom() {
		return scale;
	}
	public double getScaledX(double oldScale, double newScale, double x) {
		double baseX = x - startX;
		double dif = baseX - (baseX/oldScale) * newScale;
		return dif;
	}
	public double getScaledY(double oldScale, double newScale, double y) {
		double baseY = y - startY;
		double dif = baseY - (baseY/oldScale) * newScale;
		return dif;
	}
	public void close() {
		isLoaded = false;
		synchronized (resizeLock) {
			fManager.close();
		}
	}
}
