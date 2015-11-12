package MoF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import amidst.Options;
import amidst.map.MapObject;
import amidst.map.MapViewer;
import amidst.minecraft.MinecraftUtil;

// TODO: we should remove this and integrate it into Options
@Deprecated
public class Project extends JPanel {
	private SeedHistoryLogger seedHistoryLogger = new SeedHistoryLogger();
	public MapViewer map;
	public static int FRAGMENT_SIZE = 256;
	private Timer timer;
	public MapObject curTarget;

	private SaveLoader saveLoader;

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
		Options.instance.seed = seed;
		this.saveLoader = saveLoader;
		logSeed(seed);
		createWorld(seed, type);
		initMapViewer();
		initComponent();
		initTimer();
	}

	private void logSeed(long seed) {
		seedHistoryLogger.log(seed);
	}

	private void createWorld(long seed, String type) {
		if (isSaveLoaded()) {
			String options = this.saveLoader.getGeneratorOptions();
			MinecraftUtil.createWorld(seed, type, options);
		} else {
			MinecraftUtil.createWorld(seed, type);
		}
	}

	private void initMapViewer() {
		map = new MapViewer(this);
	}

	private void initComponent() {
		setLayout(new BorderLayout());
		add(map.getComponent(), BorderLayout.CENTER);
		setBackground(Color.BLUE);
	}

	private void initTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				tick();
			}
		}, 20, 20);
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

	public SaveLoader getSaveLoader() {
		return saveLoader;
	}

	public boolean isSaveLoaded() {
		return saveLoader != null;
	}

	public void dispose() {
		map.dispose();
		map = null;
		timer.cancel();
		timer = null;
		curTarget = null;
		saveLoader = null;

		// TODO: do we really need this?
		System.gc();
	}
}
