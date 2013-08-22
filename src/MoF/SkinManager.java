package MoF;

import amidst.map.MapObjectPlayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import javax.imageio.ImageIO;

public class SkinManager extends Thread {
	private Stack<MapObjectPlayer> players;
	public boolean active;
	public SkinManager() {
		players = new Stack<MapObjectPlayer>();
		active = true;
	}
	
	public void addPlayer(MapObjectPlayer p) {
		players.push(p);
	}
	public void run() {
		while (this.active) {
			try {
				if (players.isEmpty()) {
					Thread.sleep(50L);
				} else {
					MapObjectPlayer p = players.pop();
					try {
						URL url = new URL("http://s3.amazonaws.com/MinecraftSkins/" + p.getName() + ".png");
						BufferedImage img = ImageIO.read(url);
						BufferedImage pimg = new BufferedImage(20,20,BufferedImage.TYPE_INT_ARGB);
						Graphics2D g2d = pimg.createGraphics();
						g2d.setColor(Color.black);
						g2d.fillRect(0, 0, 20, 20);
						g2d.drawImage(img, 2, 2, 18, 18, 8, 8, 16, 16, null);
						g2d.dispose();
						img.flush();
						p.setMarker(pimg);
						Thread.sleep(20L);
					} catch (MalformedURLException e2) {
					} catch (IOException e) {
					}
				}
			} catch (InterruptedException e) {
			
			}
		}
		if (!this.active) {
			dispose();
		}
	}
	
	public void dispose() {
		players.clear();
		players = null;
	}
}
