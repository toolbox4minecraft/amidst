package MoF;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.MapObject;
import amidst.minecraft.MinecraftUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JPanel;

@Deprecated //TODO: we should remove this and integrate it into Options
public class Project extends JPanel {
	private static final long serialVersionUID = 1132526465987018165L;
	
	public MapViewer map;
	public static int FRAGMENT_SIZE = 256;
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
		this(seed, SaveLoader.Type.DEFAULT.getName());
	}
	
	public Project(SaveLoader file) {
		this(file.seed, SaveLoader.genType.getName(), file);
		
		Google.track("seed/file/" + Options.instance.seed);
	}
	
	public Project(String seed, String type) {
		this(stringToLong(seed), type);
		
		Google.track("seed/" + seed + "/" + Options.instance.seed);
	}
	
	public Project(long seed, String type) {
		this(seed, type, null);
	}
	
	private void logSeedHistory(long seed) {
		File historyFile = new File("./history.txt");
		if (Options.instance.historyPath != null) {
			historyFile = new File(Options.instance.historyPath);
			if (!historyFile.exists()) {
				try {
					historyFile.createNewFile();
				} catch (IOException e) {
					Log.w("Unable to create history file: " + historyFile);
					e.printStackTrace();
					return;
				}
			}
		}
		
		if (historyFile.exists() && historyFile.isFile()) {
			FileWriter writer = null;
			try {
				writer = new FileWriter(historyFile, true);
				writer.append(new Timestamp(new Date().getTime()).toString() + " " + seed + "\r\n");
			} catch (IOException e) {
				Log.w("Unable to write to history file.");
				e.printStackTrace();
			} finally {
				try {
					if (writer != null)
						writer.close();
				} catch (IOException e) {
					Log.w("Unable to close writer for history file.");
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	public Project(long seed, String type, SaveLoader saveLoader) {
		logSeedHistory(seed);
		saveLoaded = !(saveLoader == null);
		save = saveLoader;
		//Enter seed data:
		Options.instance.seed = seed;
		
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		MinecraftUtil.createWorld(seed, type);
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
		timer.cancel();
		timer = null;
		curTarget = null;
		save = null;
		System.gc();
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
	
	
	public KeyListener getKeyListener() {
		return map;
	}
	public void moveMapTo(long x, long y) {
		map.centerAt(x, y);
	}
}
