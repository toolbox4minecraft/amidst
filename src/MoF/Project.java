package MoF;

import amidst.Options;
import amidst.map.MapObject;
import amidst.map.MapObjectPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

@Deprecated //TODO: we should remove this and integrate it into Options
public class Project extends JPanel {
	private static final long serialVersionUID = 1132526465987018165L;
	
	public MapViewer map;
	public static int FRAGMENT_SIZE = 256;
	public ChunkManager manager;
	private Timer timer;
	public MapObject curTarget;
	
	public boolean saveLoaded;
	public SaveLoader save;
	
	public Project(String seed) {
		this(stringToLong(seed));
		Options.instance.seedText = seed;
		
		Google.track("seed/" + seed + "/" + Options.instance.seed);
	}
	
	public Project(long seed) {
		this(seed, SaveLoader.Type.DEFAULT);
	}
	
	public Project(SaveLoader file) {
		this(file.seed, SaveLoader.genType);
		saveLoaded = true;
		save = file;
		
		Google.track("seed/file/" + Options.instance.seed);
		List<MapObjectPlayer> players = file.getPlayers();
		manager.setPlayerData(players);
	}
	
	public Project(String seed, SaveLoader.Type type) {
		this(stringToLong(seed), type);
		
		Google.track("seed/" + seed + "/" + Options.instance.seed);
	}
	
	public Project(long seed, SaveLoader.Type type) {
		SaveLoader.genType = type;
		saveLoaded = false;
		//Enter seed data:
		Options.instance.seed = seed;
		
		manager = new ChunkManager(seed);
		manager.start();
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		
		//Create MapViewer
		map = new MapViewer(this);
		add(map, BorderLayout.CENTER);
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
	}
	
	public void dispose() {
		map.dispose();
		map = null;
		manager.dispose();
		manager = null;
		timer.cancel();
		timer = null;
		curTarget = null;
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
	
	
	public void moveMapTo(long x, long y) {
		map.centerAt(x, y);
	}
}
