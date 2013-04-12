package MoF;


import amidst.Options;
import amidst.map.Map;
import amidst.map.MapObject;
import amidst.map.MapObjectPlayer;
import amidst.map.layers.BiomeLayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class MapViewer extends JComponent implements MouseListener, MouseWheelListener, ComponentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8309927053337294612L;
	private Project proj;
	
	private int fragXMax = 0, fragYMax = 0, fragX = 0, fragY = 0;
	
	private boolean mTracking = false, resized = false;
	private double mX = 0, mY = 0, tX = 0, tY = 0;
	private PieChart chart;
	private ArrayList<ArrayList<Fragment>> frags;
	private JPopupMenu  menu = new JPopupMenu();
	private double scale = 1;
	private boolean dataChange, sizeChange, firstRun;
	public int strongholdCount, villageCount;
	
	private Map testMap;
	private float lastMouseX, lastMouseY;
	private float panSpeedX, panSpeedY;
	private boolean mouseDown;
	
	private int zoomLevel = 0, zoomTicksRemaining = 0;
	private float targetZoom = 1.0f, curZoom = 1.0f;
	private float zoomMouseX, zoomMouseY;
	
	
	private static int offset[] = new int[] {
		-1,-1,
		-1, 0,
		-1, 1,
		 0,-1,
		 0, 1,
		 1,-1,
		 1, 0,
		 1, 1
	};
	
	public void dispose() {
		System.out.println("DISPOSING OF MAPVIEWER");
		for (int ey = 0; ey < fragYMax; ey++) {
			for (int ex = 0; ex < fragXMax; ex++) {
				Fragment tempFrag = frags.get(ey).get(ex);
				tempFrag.dispose();
			}
			frags.get(ey).clear();
		}
		frags.clear();
		frags = null;
		chart.dispose();
		menu.removeAll();
		proj = null;
		
	}
	
	MapViewer(Project proj) {
		dataChange = true;
		sizeChange = true;
		firstRun = true;
		this.addMouseListener(this);
		resized = true;
		this.addComponentListener(this);
		this.addMouseWheelListener(this);
		this.proj = proj;
		
		testMap = new Map(proj.manager);
		testMap.addLayer(new BiomeLayer());
		testMap.load();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2d.setColor(new Color(25, 25, 25));
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(new Font("arial", Font.BOLD, 15));
		g2d.drawString(proj.seedText, 20, 30);
		if (mTracking) { 
			Point p = this.getMousePosition();
			if (p!=null) {
				mX += (p.x - tX);
				mY += (p.y - tY);
				tX = p.x;
				tY = p.y;
			}
		}
		if (zoomTicksRemaining-- > 0) {
			float lastZoom = curZoom;
			curZoom = (targetZoom + curZoom) * 0.5f;
			
			double targetZoomX = testMap.getScaledX(lastZoom, curZoom, zoomMouseX);
			double targetZoomY = testMap.getScaledY(lastZoom, curZoom, zoomMouseY);
			testMap.moveBy(targetZoomX, targetZoomY);
			testMap.setZoom(curZoom);
		}
		
		if (mouseDown) {
			Point curMouse = this.getMousePosition();
			if (curMouse != null) {
				float difX = curMouse.x - lastMouseX;
				float difY = curMouse.y - lastMouseY;
				// TODO : Scale with time
				panSpeedX = difX * 0.2f;
				panSpeedY = difY * 0.2f;
				
				lastMouseX += panSpeedX;
				lastMouseY += panSpeedY;
			}
		}
		panSpeedX *= 0.95f;
		panSpeedY *= 0.95f;
		
		
		
		testMap.moveBy(panSpeedX, panSpeedY);
		
		
		testMap.draw(g2d);
	}
	public Project getProject() {
		return proj;
	}
	
	
	public void centerAt(double x, double y) {
		double adjX, adjY, zx, zy;
		zx = x;
		zy = y;
		scale = 1;
		int fs = Project.FRAGMENT_SIZE;
		adjX = Math.floor(zx/fs) - 1;
		adjY = Math.floor(zy/fs) - 1;
		mX = -1*(Math.floor(zx - getWidth()/(2)) - (adjX + 1)*fs);
		mY = -1*(Math.floor(zy - getHeight()/(2)) - (adjY + 1)*fs);
		fragX = (int)adjX;
		fragY = (int)adjY;
	}
	public void centerAndReset(double x, double y) {
		centerAt(x,y);
		
	}
	
	public void setScale(double scale) {
		if ((scale <= 8)&&(scale >= 0.25)) {
			this.scale = scale;
			sizeChange = true;
		}
	}
	public void scaleBy(double scale) {
		setScale(this.scale*scale);
	}
	
	private int getWriteX(int in) {
		int ex = in % fragXMax;
		if (ex < 0)
			ex += fragXMax;
		return ex;
	}
	private int getWriteY(int in) {
		int ey = in % fragYMax;
		if (ey < 0)
			ey += fragYMax;
		return ey;
	}
	@SuppressWarnings("unused")
	private int getWrite(int in, int max) {
		int ew = in % max;
		if (ew < 0)
			ew += max;
		return ew;
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point curMouse = getMousePosition();
		zoomMouseX = curMouse.x;
		zoomMouseY = curMouse.y;
		int notches = e.getWheelRotation();
		
		if (notches > 0) {
			if (zoomLevel < 10) {
				targetZoom /= 1.1;
				zoomLevel++;
				zoomTicksRemaining = 100;
			}
		} else {
			if (zoomLevel > -10) {
				targetZoom *= 1.1;
				zoomLevel--;
				zoomTicksRemaining = 100;
			}
		}
		
		double z = 2;
		if (notches == 1)
			z = 0.5;
		scaleBy(z);
	}
	@Override
	public void mouseClicked(MouseEvent me) {
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent arg0) {

		if (!arg0.isMetaDown()) {
			mTracking = true;
			Point curMouse = this.getMousePosition();
			tX = curMouse.x;
			tY = curMouse.y;
			
			lastMouseX = curMouse.x;
			lastMouseY = curMouse.y;
			mouseDown = true;
		}
	}
	private int tempX, tempY;
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && Options.instance.saveEnabled) {
			if (proj.saveLoaded) {
				tempX = e.getX();
				tempY = e.getY();
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		} else {
			mouseDown = false;
			mTracking = false;
		}
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		resized = true;
		
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
	}

	public MapObject getSelectedObject() {
		return proj.curTarget;
	}

	public PieChart getChart() {
		return chart;
	}

	public void setChart(PieChart chart) {
		this.chart = chart;
	}

	public void movePlayer(String name, ActionEvent e) {
		//PixelInfo p = getCursorInformation(new Point(tempX, tempY));
		
		//proj.movePlayer(name, p);
	}

	public void saveToFile(File f) {
		int fs = Project.FRAGMENT_SIZE;
		BufferedImage img = new BufferedImage(fragXMax*fs,fragYMax*fs,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		List<MapObject> markers;
		for (int ey = 0; ey < fragYMax; ey++) {
			for (int ex = 0; ex < fragXMax; ex++) {
				Fragment tempFrag = frags.get(ey).get(ex);
				tempFrag.tempX = (tempFrag.x - fragX)*Project.FRAGMENT_SIZE;
				tempFrag.tempY = (tempFrag.y - fragY)*Project.FRAGMENT_SIZE;
				tempFrag.paint(g2d,
						tempFrag.tempX,
						tempFrag.tempY,
						Project.FRAGMENT_SIZE,
						Project.FRAGMENT_SIZE);
			}
		}
		
		for (int ey = 0; ey < fragYMax; ey++) {
			for (int ex = 0; ex < fragXMax; ex++) {
				Fragment tempFrag = frags.get(ey).get(ex);
				markers = tempFrag.objects;
				for (MapObject m : markers) {
					g2d.drawImage(m.getImage(),
							tempFrag.tempX + m.rx - (m.getWidth() >> 1),
							tempFrag.tempY + m.ry - (m.getHeight() >> 1),
							m.getWidth(),
							m.getHeight(),
							null);
				}
			}
		}
		try {
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		g2d.dispose();
		img.flush();
		
	}
}
