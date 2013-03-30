package MoF;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MapMarker {
	public static BufferedImage village, stronghold, spawn, player,slime, nether, pyramid, witch;
	public static int init() {
		try {
		    village = ImageIO.read(MoF.getURL("images/village.png"));
		    stronghold = ImageIO.read(MoF.getURL("images/stronghold.png"));
		    slime = ImageIO.read(MoF.getURL("images/slime.png"));
		    player = ImageIO.read(MoF.getURL("images/player.png"));
		    nether = ImageIO.read(MoF.getURL("images/netherhold.png"));
		    pyramid = ImageIO.read(MoF.getURL("images/pyramid.png"));
		    witch = ImageIO.read(MoF.getURL("images/witch.png"));
		} catch (IOException e) {
			System.out.println(e);
		}
		return 0;
	}
}
