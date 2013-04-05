package MoF;
import amidst.map.MapObject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;


public class MapInfoPanel extends JPanel {
	private static final long serialVersionUID = 45237076617899377L;
	private MapViewer map;
	private Color bgColor = new Color(230, 230, 230);
	
	
	
	public MapInfoPanel(MapViewer map) {
		this.map = map;
		this.setPreferredSize(new Dimension(200,3000));
		//this.setPreferredSize(new Dimension(200,200));
	}
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setColor(Color.white);
		g2d.drawString("Info Panel will be here!", 50, 50);
		tPaint(g2d); bPaint(g2d);
		g2d.dispose();
	}
	public void tPaint(Graphics2D g2d) {
		g2d.setColor(bgColor);
		g2d.fillRect(0, 0, 200, getHeight()-100);
		g2d.setColor(Color.black);
		g2d.drawLine(0, getHeight()-105, 200, getHeight()-105);
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		MapObject m = map.getSelectedObject();
		
		//Draw outer rectangle:
		g2d.drawRect(5, 15, 190, 70);
		g2d.setColor(bgColor);
		g2d.fillRect(15, 10, 105, 30);
		g2d.setColor(Color.black);
		g2d.setFont(new Font("arial", 15, 15));
		g2d.drawString("Selected Object", 15, 20);
		int yOffset  = 40;
		
		if (m==null) {
			g2d.setFont(new Font("arial", 12, 12));
			g2d.drawString("No Object Selected", 10,yOffset);
		} else {
			g2d.setFont(new Font("arial", 20, 20));
			g2d.drawString(m.getName(), 15,yOffset + 10);
			g2d.setFont(new Font("arial", 12, 12));
			g2d.drawString("Location: (" + m.x + ", " + m.y + ")", 15, yOffset + 30);
		}
		yOffset += 50;
		g2d.drawRect(5, yOffset + 15, 190, getHeight() - yOffset - 15 - 115);
		g2d.setColor(bgColor);
		g2d.fillRect(15, yOffset+10, 70, 30);
		g2d.setColor(Color.black);
		g2d.setFont(new Font("arial", 15, 15));
		g2d.drawString("View Data", 15, yOffset  + 20);
		
		PieChart chart = map.getChart();
		if (chart != null) {
			chart.paint(g2d, 15, yOffset + 45, 180, 180);
			g2d.setFont(new Font("arial", 20, 20));
			yOffset = getHeight() - 170;
			g2d.setColor(Color.black);
			g2d.drawString("Strongholds: " + map.strongholdCount, 15, yOffset + 10);
			g2d.drawString("Villages: " + map.villageCount, 15, yOffset + 40);
			
		}
		
	}
	
	public void bPaint(Graphics2D g2d) {
		PixelInfo p = map.getCursorInformation();
		
		int yOffset = getHeight() - 100;
		
		
		g2d.setColor(bgColor);
		g2d.fillRect(0, yOffset, 200, 100);
		
		if ((p!=null)&&(p.getBiome()!=255)) {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Color c = Biome.colors[p.getBiome()];
			g2d.setColor(c);
			g2d.fillRect(16, yOffset + 10, 32, 32);
			g2d.setColor(Color.black);
			g2d.setFont(new Font("arial", 20, 20));
			g2d.drawString(Biome.a[p.getBiome()].name, 55, yOffset + 34);
			g2d.drawString("X: " + p.getBlockX(), 16, yOffset + 70);
			g2d.drawString("Z: " + p.getBlockY(), 16, yOffset + 90);
		} else {
			
		}
		
	}
	public void dispose() {
		map = null;
	}
}
