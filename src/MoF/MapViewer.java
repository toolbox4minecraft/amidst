package MoF;


import amidst.Log;
import amidst.Options;
import amidst.map.IconLayer;
import amidst.map.Layer;
import amidst.map.Map;
import amidst.map.MapObject;
import amidst.map.layers.BiomeLayer;
import amidst.map.layers.GridLayer;
import amidst.map.layers.SlimeLayer;
import amidst.map.layers.VillageLayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class MapViewer extends JComponent implements MouseListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8309927053337294612L;
	private Project proj;
	
	private PieChart chart;
	private JPopupMenu menu = new JPopupMenu();
	private double scale = 1;
	public int strongholdCount, villageCount;
	
	private Map worldMap;
	private MapObject selectedObject = null;
	private Point lastMouse;
	private Point2D.Double panSpeed;
	
	private boolean isMouseInside = false;
	private int zoomLevel = 0, zoomTicksRemaining = 0;
	private float targetZoom = 1.0f, curZoom = 1.0f;
	private Point zoomMouse;
	
	private Color textColor = new Color(1f, 1f, 1f),
				  panelColor = new Color(0.2f, 0.2f, 0.2f, 0.7f);
	private Font textFont = new Font("arial", Font.BOLD, 15);
	
	
	public void dispose() {
		Log.debug("DISPOSING OF MAPVIEWER");
		worldMap.dispose();
		chart.dispose();
		menu.removeAll();
		proj = null;
	}
	
	MapViewer(Project proj) {
		chart = new PieChart(0, 0); //TODO: do something with chart
		panSpeed = new Point2D.Double();
		this.proj = proj;
		
		worldMap = new Map(proj.manager, 
				new Layer[] {
					new BiomeLayer(),
					new SlimeLayer(),
					new GridLayer()
				},
				new IconLayer[] {
					new VillageLayer()
				}
		); //TODO: implement more layers
		
		addMouseListener(this);
		addMouseWheelListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		

		if (zoomTicksRemaining-- > 0) {
			float lastZoom = curZoom;
			curZoom = (targetZoom + curZoom) * 0.5f;
			
			Point2D.Double targetZoom = worldMap.getScaled(lastZoom, curZoom, zoomMouse);
			worldMap.moveBy(targetZoom);
			worldMap.setZoom(curZoom);
		}

		Point curMouse = getMousePosition();
		isMouseInside = (curMouse != null);
		if (lastMouse != null) {
			if (curMouse != null) {
				double difX = curMouse.x - lastMouse.x;
				double difY = curMouse.y - lastMouse.y;
				// TODO : Scale with time
				panSpeed.setLocation(difX * 0.2f, difY * 0.2f);
			}
			
			lastMouse.translate((int) panSpeed.x, (int)panSpeed.y);
		}
		
		panSpeed.x *= 0.95f;
		panSpeed.y *= 0.95f;
		
		worldMap.moveBy(panSpeed.x, panSpeed.y);
		
		worldMap.draw(g2d);
		
		FontMetrics textMetrics = g2d.getFontMetrics(textFont);
		
		
		boolean hasSelection = (selectedObject != null);
		g2d.setColor(panelColor);
		g2d.fillRect(10, 10, textMetrics.stringWidth(Options.instance.getSeedMessage()) + 20, 30);
		
		String selectionMessage = "";
		if (hasSelection) {
			selectionMessage = selectedObject.getName() + " [" + selectedObject.rx + ", " + selectedObject.ry + "]";
			g2d.fillRect(10, 45, 45 + textMetrics.stringWidth(selectionMessage), 35);
			// TODO: Use the text size request method
		}
		
		Point mouseLocation = null;
		String mouseLocationText = null; 
		int stringWidth = 0;
		
		if (isMouseInside) {
			mouseLocation = worldMap.screenToLocal(curMouse);
			int biome = worldMap.getBiomeData(mouseLocation);
			// TODO: Biome is an obsolete class
			String biomeName = Biome.a[biome].name;
			mouseLocationText = biomeName + " [" + mouseLocation.x + ", " + mouseLocation.y + "]";
			stringWidth = textMetrics.stringWidth(mouseLocationText);
			g2d.fillRect(getWidth() - (25 + stringWidth), 10, (15 + stringWidth), 30);
		}
		g2d.setColor(textColor);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(textFont);
		g2d.drawString(Options.instance.getSeedMessage(), 20, 30);
		
		
		if (hasSelection) {
			g2d.drawImage(selectedObject.getImage(), 15, 50, null);
			g2d.drawString(selectionMessage, 50, 68);
		}
		
		if (isMouseInside) {
			g2d.drawString(mouseLocationText, getWidth() - (18 + stringWidth), 30);
		}
	}
	
	public void centerAt(double x, double y) {
		worldMap.centerOn(x, y);
	}
	
	public void centerAndReset(double x, double y) {
		scale = 1;
		centerAt(x, y);
	}
	
	public void setScale(double scale) {
		if ((scale <= 8) && (scale >= 0.25)) {
			this.scale = scale;
		}
	}
	public void scaleBy(double scale) {
		setScale(this.scale*scale);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomMouse = getMousePosition();
		int notches = e.getWheelRotation();
		
		if (notches > 0) {
			if (zoomLevel < 20) {
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
	public void mouseClicked(MouseEvent e) {
		if (!e.isMetaDown()) {
			Point mouse = getMousePosition();
			MapObject object = worldMap.getObjectAt(mouse, 50.0);
			if (object != null)
				object.localScale = 1.5;
			
			if (selectedObject != null)
				selectedObject.localScale = 1.0;
			selectedObject = object;
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.isMetaDown())
			lastMouse = getMousePosition();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && ReflectionInfo.instance.version.saveEnabled()) {
			if (proj.saveLoaded) {
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		} else lastMouse = null;
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
		/*TODO
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
		*/
	}
}
