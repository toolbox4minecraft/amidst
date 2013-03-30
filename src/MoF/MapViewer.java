package MoF;


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
		chart = new PieChart(Biome.colors.length,0);
		this.addMouseListener(this);
		resized = true;
		this.addComponentListener(this);
		this.addMouseWheelListener(this);
		menu = new JPopupMenu();
		if (proj.saveLoaded) {
			ArrayList<Player> pl = proj.save.getPlayers();
			for (int i = 0; i < pl.size(); i++) {
				JMenuItem tj = new JMenuItem("Move " + pl.get(i).getName() + " here.");
				
				tj.addActionListener(new MapListener(this, pl.get(i).getName()));
				menu.add(tj);
			}
		}
		this.proj = proj;
		
		
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		updateFragments();
		ArrayList<MapObject> markers = null;
		if (firstRun) {
			centerAt(0,0);
			firstRun = false;
		}
		for (int ey = 0; ey < fragYMax; ey++) {
			for (int ex = 0; ex < fragXMax; ex++) {
				Fragment tempFrag = frags.get(ey).get(ex);
				tempFrag.tempX = (int)(((tempFrag.x - fragX - 1)*Project.FRAGMENT_SIZE)*scale + (int)mX);
				tempFrag.tempY = (int)(((tempFrag.y - fragY - 1)*Project.FRAGMENT_SIZE)*scale + (int)mY);
				tempFrag.paint(g2d,
						tempFrag.tempX,
						tempFrag.tempY, 
						(int)(Project.FRAGMENT_SIZE*scale), 
						(int)(Project.FRAGMENT_SIZE*scale));
				if (tempFrag.marked) {
					g2d.setColor(Color.pink);
					g2d.drawRect(
							tempFrag.tempX, 
							tempFrag.tempY,
							(int)(Project.FRAGMENT_SIZE*scale), 
							(int)(Project.FRAGMENT_SIZE*scale));
				}
			}
		}
		for (int ey = 0; ey < fragYMax; ey++) {
			for (int ex = 0; ex < fragXMax; ex++) {
				Fragment tempFrag = frags.get(ey).get(ex);
				markers = tempFrag.objects;
				for (int i = 0; i < markers.size(); i++) {
					MapObject m = markers.get(i);
					BufferedImage img = m.marker;
					g2d.drawImage(img,
							(int)(tempFrag.tempX + m.rx*scale - (m.getWidth() >> 1)),
							(int)(tempFrag.tempY + m.ry*scale - (m.getHeight() >> 1)), 
							(int)(m.getWidth()), 
							(int)(m.getHeight()), 
							null);
				}
			}
		}
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
		
		if (resized) {
			resized = false;
			cleanUpdate();
		}
		updateChart();
	}
	public Project getProject() {
		return proj;
	}
	private void updateChart() {
		if (sizeChange) {
			chart.setSources(fragXMax*fragYMax);
		}
		if (dataChange) {
			chart.clearData();
			villageCount = 0;
			strongholdCount = 0;
			for (int y = 0; y < fragYMax; y++) {
				for (int x = 0; x < fragXMax; x++) {
					chart.addData(frags.get(y).get(x).stat);
					strongholdCount += frags.get(y).get(x).strongholdCount;
					villageCount += frags.get(y).get(x).villageCount;
				}
			}
		}
	}
	
	private void shiftFragmentX(int pos) {
		dataChange = true;
		int offset = (pos + 1) >> 1;
		int marker = getWriteX(fragX + offset + fragXMax - 1);
		
		fragX += pos;
		for (int i = 0; i < fragYMax; i++) {
			frags.get(getWriteY(fragY + i)).get(marker).disable();
			frags.get(getWriteY(fragY + i)).set(marker, proj.getFragment(fragX + (fragXMax - 1) * offset, fragY + i));
		}
	}
	private void shiftFragmentY(int pos) {
		dataChange = true;
		int offset = (pos + 1) >> 1;
		int marker = getWriteY(fragY + offset + fragYMax - 1);
		
		fragY += pos;
		ArrayList<Fragment> tempList = new ArrayList<Fragment>(fragXMax);
		for (int i = 0; i < fragXMax; i++)
			tempList.add(null);
		for (int i = 0; i < fragXMax; i++) {
			frags.get(marker).get(getWriteX(fragX + i)).disable();
			tempList.set(getWriteX(fragX + i), proj.getFragment(fragX + i, fragY + (fragYMax - 1) * offset));
		}
		
		frags.set(marker, tempList);
	}
	
	
	public void cleanUpdate() {
		System.gc();
		dataChange = true;
		sizeChange = true;
		for (int i = 0; i < fragYMax; i++) {
			for (int c = 0; c < fragXMax; c++) {
				frags.get(i).get(c).disable();
			}
		}
		
		
		//Resize arrays
		fragXMax = (int) (Math.round((double)this.getWidth()/((double)Project.FRAGMENT_SIZE*scale)) + 2);
		fragYMax = (int) (Math.round((double)this.getHeight()/((double)Project.FRAGMENT_SIZE*scale)) + 2);
		
		frags = new ArrayList<ArrayList<Fragment>>(fragYMax);
		for (int i = 0; i < fragYMax; i++) {
			frags.add(new ArrayList<Fragment>(fragXMax));
			for (int c = 0; c < fragXMax; c++) {
				frags.get(i).add(null);
			}
		}
		for (int i = fragY; i < fragY + fragYMax; i++) {
			for (int c = fragX; c < fragX + fragXMax; c++) {
				frags.get(getWriteY(i)).set(getWriteX(c), proj.getFragment(c, i));
			}
		}
	}
	public PixelInfo getCursorInformation(Point c) {
		PixelInfo p = null;
		
		if (c!=null) {
			if (fragXMax + fragYMax > 0) {
				double fx = (((c.x - mX)/(scale*Project.FRAGMENT_SIZE)) + 1 + fragX);
				double fy = (((c.y - mY)/(scale*Project.FRAGMENT_SIZE)) + 1 + fragY);
				int ix = (int)fx;
				int iy = (int)fy;
				if (fx < 0)
					ix--;
				if (fy < 0)
					iy--;
				Fragment curFrag = frags.get(getWriteY(iy)).get(getWriteX(ix));
				int ox = (int) ((fx - ix)*Project.FRAGMENT_SIZE);
				int oy = (int) ((fy - iy)*Project.FRAGMENT_SIZE);
				p = new PixelInfo(ix*Project.FRAGMENT_SIZE + ox, iy*Project.FRAGMENT_SIZE + oy, curFrag);
			}
		}
		
		return p;
	}
	public PixelInfo getCursorInformation() {
		
		Point c = this.getMousePosition();
		PixelInfo p = null;
		
		p = getCursorInformation(c);
		
		return p;
		
	}
	private void updateFragments() {
		double fragSize = scale*Project.FRAGMENT_SIZE;
		if (mX > fragSize) {
			mX -= fragSize; //Trigger Left Moving
			shiftFragmentX(-1);
		} else if (mX < 0) { 
			mX += fragSize; //Trigger Right Moving
			shiftFragmentX(1);
		}
		
		if (mY > fragSize) {
			mY -= fragSize; //Trigger Up Moving
			shiftFragmentY(-1);
		} else if (mY < 0) {
			mY += fragSize; //Trigger Down Moving
			shiftFragmentY(1);
		}
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
		
		cleanUpdate();
	}
	
	public void setScale(double scale) {
		if ((scale <= 8)&&(scale >= 0.25)) {
			this.scale = scale;
			sizeChange = true;
			cleanUpdate();
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
		int notches = e.getWheelRotation();
		double z = 2;
		if (notches == 1)
			z = 0.5;
		scaleBy(z);
	}
	@Override
	public void mouseClicked(MouseEvent me) {
		if (!me.isPopupTrigger()) {
			PixelInfo p = getCursorInformation();
			
			MapObject m =  p.getFrag().getObjectAt(p.getX(), p.getY());
			int rx = p.getX() - (p.getFrag().x * Project.FRAGMENT_SIZE);
			int ry = p.getY() - (p.getFrag().y * Project.FRAGMENT_SIZE);
			
			double dist = 3600;
			if (m!=null)
				dist = m.tempDist;
			for (int i = 0; i < offset.length; i+=2) {
				int ix = rx + offset[i  ]*40;
				int iy = ry + offset[i+1]*40;
				if (	((Math.abs(ix - (Project.FRAGMENT_SIZE >> 1))>(Project.FRAGMENT_SIZE >> 1))||(offset[i]==0))&
						((Math.abs(iy - (Project.FRAGMENT_SIZE >> 1))>(Project.FRAGMENT_SIZE >> 1))||(offset[i+1]==0))  ) {
					MapObject m2 = frags.get(getWriteY(p.getFrag().y + offset[i+1])).get(getWriteX(p.getFrag().x + offset[i])).getObjectAt(p.getX(), p.getY());
					if ((m2!=null)&&(m2.isSelectable())&&(m2.tempDist < dist)) {
						m = m2;
					}
					
				}
			}
			if (m!=null) {
				if (proj.curTarget!=null) {
					proj.curTarget.localScale = 1.0;
				}
				m.localScale = 2.0;
				proj.curTarget = m;
			} else {
				if (proj.curTarget!=null)
					proj.curTarget.localScale = 1.0;
				proj.curTarget = null;
			}
		}
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
			Point p = this.getMousePosition();
			tX = p.x;
			tY = p.y;
		}
	}
	private int tempX, tempY;
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()&&!MoF.DISABLE_SAVE) {
			if (proj.saveLoaded) {
				tempX = e.getX();
				tempY = e.getY();
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		} else {
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
		PixelInfo p = getCursorInformation(new Point(tempX, tempY));
		
		proj.movePlayer(name, p);
		
	}

	public void saveToFile(File f) {
		int fs = Project.FRAGMENT_SIZE;
		BufferedImage img = new BufferedImage(fragXMax*fs,fragYMax*fs,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		ArrayList<MapObject> markers = null;
		for (int ey = 0; ey < fragYMax; ey++) {
			for (int ex = 0; ex < fragXMax; ex++) {
				Fragment tempFrag = frags.get(ey).get(ex);
				tempFrag.tempX = (int)((tempFrag.x - fragX)*Project.FRAGMENT_SIZE);
				tempFrag.tempY = (int)((tempFrag.y - fragY)*Project.FRAGMENT_SIZE);
				tempFrag.paint(g2d,
						tempFrag.tempX,
						tempFrag.tempY, 
						(int)(Project.FRAGMENT_SIZE), 
						(int)(Project.FRAGMENT_SIZE));
			}
		}
		
		for (int ey = 0; ey < fragYMax; ey++) {
			for (int ex = 0; ex < fragXMax; ex++) {
				Fragment tempFrag = frags.get(ey).get(ex);
				markers = tempFrag.objects;
				for (int i = 0; i < markers.size(); i++) {
					MapObject m = markers.get(i);
					BufferedImage img2 = m.marker;
					g2d.drawImage(img2,
							(int)(tempFrag.tempX + m.rx - (m.getWidth() >> 1)),
							(int)(tempFrag.tempY + m.ry - (m.getHeight() >> 1)), 
							(int)(m.getWidth()), 
							(int)(m.getHeight()), 
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
