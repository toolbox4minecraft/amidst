package MoF;

import amidst.map.MapObject;
import amidst.map.MapObjectPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class Project extends JPanel {
	private static final long serialVersionUID = 1132526465987018165L;
	private long seed;
	
	public MapViewer map;
	private MapInfoPanel minfo;
	public static int FRAGMENT_SIZE = 256;
	public ChunkManager manager;
	private Timer timer;
	public MapObject curTarget;
	
	public boolean saveLoaded;
	public SaveLoader save;
	
	public String seedText;
	
	public Project(String seed) {
		this(stringToLong(seed));
		this.seedText = "Seed: \"" + seed + "\" (" + this.seed +  ")";
		
		Google.track("seed/" + seed + "/" + this.seed);
	}
	
	public Project(long seed) {
		this(seed, SaveLoader.Type.DEFAULT);
	}
	
	public Project(SaveLoader file) {
		this(file.seed, SaveLoader.genType);
		saveLoaded = true;
		save = file;
		
		Google.track("seed/file/" + this.seed);
		List<MapObjectPlayer> players = file.getPlayers();
		manager.setPlayerData(players);
	}
	
	public Project(String seed, SaveLoader.Type type) {
		this(stringToLong(seed), type);
		this.seedText = "Seed: \"" + seed + "\" (" + this.seed +  ")";
		
		Google.track("seed/" + seed + "/" + this.seed);
	}
	
	public Project(long seed, SaveLoader.Type type) {
		SaveLoader.genType = type;
		saveLoaded = false;
		//Enter seed data:
		this.seed = seed;
		this.seedText = "Seed: " + seed;
		
		manager = new ChunkManager(seed);
		manager.start();
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		
		//Create MapViewer
		map = new MapViewer(this);
		
		add(map, BorderLayout.CENTER);
		minfo = new MapInfoPanel(map);
		add(minfo, BorderLayout.EAST);
		
		//Debug
		this.setBackground(Color.BLUE);
		
		//Timer:
		timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				tick();
			}
		}, 20, 20);
	}
	
	public void tick() {
		map.repaint();
		minfo.repaint();
	}
	
	public void dispose() {
		map.dispose();
		map = null;
		manager.dispose();
		manager = null;
		timer.cancel();
		timer = null;
		curTarget = null;
		minfo.dispose();
		minfo = null;
		save = null;
		
	}
	
	private static long stringToLong(String seed) {
		long ret;
		try {
			ret = Long.parseLong(seed);
		} catch (NumberFormatException err) { 
			ret = seed.hashCode();
		}
		return ret;
	}
	
	public void movePlayer(String name, PixelInfo p) {
		for (int i = 0; i < save.getPlayers().size(); i++) {
			if (name.toLowerCase().equals(save.getPlayers().get(i).getName().toLowerCase())) {
				save.getPlayers().get(i).setPosition(p.getBlockX(), p.getBlockY());
			}
		}
	}
	public long getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = stringToLong(seed);
	}
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	
	public void moveMapTo(int x, int y) {
		map.centerAndReset(x, y);
	}
}
