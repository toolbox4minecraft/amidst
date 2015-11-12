package MoF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.MapObject;
import amidst.map.MapViewer;
import amidst.minecraft.MinecraftUtil;

// TODO: we should remove this and integrate it into Options
@Deprecated
public class Project extends JPanel {
	public MapViewer map;
	public static int FRAGMENT_SIZE = 256;
	private Timer timer;
	public MapObject curTarget;

	public boolean saveLoaded;
	public SaveLoader save;

	public Project(SaveLoader file) {
		this(file.seed, SaveLoader.genType.getName(), file);

		Google.track("seed/file/" + Options.instance.seed);
	}

	public Project(String seed, String type) {
		this(getSeedFromString(seed), type);

		Google.track("seed/" + seed + "/" + Options.instance.seed);
	}

	public Project(long seed, String type) {
		this(seed, type, null);
	}

	private Project(long seed, String type, SaveLoader saveLoader) {
		logSeedHistory(seed);
		saveLoaded = saveLoader != null;
		save = saveLoader;
		Options.instance.seed = seed;

		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		if (saveLoaded) {
			MinecraftUtil.createWorld(seed, type, save.getGeneratorOptions());
		} else {
			MinecraftUtil.createWorld(seed, type);
		}
		map = new MapViewer(this);
		add(map.getComponent(), BorderLayout.CENTER);

		this.setBackground(Color.BLUE);

		timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				tick();
			}
		}, 20, 20);
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
				writer.append(new Timestamp(new Date().getTime()).toString()
						+ " " + seed + "\r\n");
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

	private static long getSeedFromString(String seed) {
		try {
			return Long.parseLong(seed);
		} catch (NumberFormatException err) {
			return seed.hashCode();
		}
	}

	@Deprecated
	public KeyListener getKeyListener() {
		return map.getKeyListener();
	}

	public void moveMapTo(long x, long y) {
		map.centerAt(x, y);
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
}
