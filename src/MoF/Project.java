package MoF;

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
	
	public Project(long seed, FinderWindow window) {
		this(seed, window, "default");
	}
	
	public Project(long seed, FinderWindow window, String type) {
		SaveLoader.genType = type;
		saveLoaded = false;
		//Enter seed data:
		this.setSeed(seed);
		this.seedText = "Seed: " + seed;
		init();
	}
	
	public void tick() {
		map.repaint();
		minfo.repaint();
	}
	
	public Project(String seed, FinderWindow window, String type) {
		this(stringToLong(seed), window, type);
		this.seedText = "Seed: \"" + seed + "\" (" + this.seed +  ")";
		
		Google.track("seed/" + seed + "/" + this.seed);
	}
	
	public Project(String seed, FinderWindow window) {
		this(stringToLong(seed), window);
		this.seedText = "Seed: \"" + seed + "\" (" + this.seed +  ")";
		
		Google.track("seed/" + seed + "/" + this.seed);
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
	
	public Project(SaveLoader file) {
		saveLoaded = true;
		save = file;
		//Enter seed data:
		this.setSeed(file.seed);
		this.seedText = "Seed: " + seed;
		init();
		
		Google.track("seed/file/" + this.seed);
		List<Player> players = file.getPlayers();
		manager.setPlayerData(players);
		
	}
	private void init() {
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
				map.cleanUpdate();
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
	
	public Fragment getFragment(int x, int y) {
		Fragment frag = new Fragment(x,y,FRAGMENT_SIZE, this);
		manager.requestChunk(frag);
		
		return frag;
	}
	
	public void moveMapTo(int x, int y) {
		map.centerAndReset(x, y);
	}
	
}
