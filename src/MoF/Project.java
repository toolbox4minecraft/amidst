package MoF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import amidst.Options;
import amidst.map.MapViewer;
import amidst.minecraft.MinecraftUtil;

// TODO: we should remove this and integrate it into Options
@Deprecated
public class Project {
	private SeedHistoryLogger seedHistoryLogger = new SeedHistoryLogger();
	private Timer timer = new Timer();
	private JPanel panel = new JPanel();
	private MapViewer mapViewer;
	private SaveLoader saveLoader;

	public Project(SaveLoader file) {
		this(file.seed, SaveLoader.genType.getName(), file);
		Google.track("seed/file/" + Options.instance.seed);
	}

	public Project(String seed, String type) {
		this(getSeedFromString(seed), type, null);
		Google.track("seed/" + seed + "/" + Options.instance.seed);
	}

	public Project(long seed, String type) {
		this(seed, type, null);
		// no Google.track(), because this is only called with a random seed?
	}

	private Project(long seed, String type, SaveLoader saveLoader) {
		Options.instance.seed = seed;
		this.saveLoader = saveLoader;
		logSeed(seed);
		createWorld(seed, type);
		initMapViewer();
		initPanel();
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
		mapViewer = new MapViewer(this);
	}

	private void initPanel() {
		panel.setLayout(new BorderLayout());
		panel.add(mapViewer.getComponent(), BorderLayout.CENTER);
		panel.setBackground(Color.BLUE);
	}

	private void initTimer() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
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
		return mapViewer.getKeyListener();
	}

	public MapViewer getMapViewer() {
		return mapViewer;
	}

	public void moveMapTo(long x, long y) {
		mapViewer.centerAt(x, y);
	}

	public void tick() {
		mapViewer.repaint();
	}

	public SaveLoader getSaveLoader() {
		return saveLoader;
	}

	public boolean isSaveLoaded() {
		return saveLoader != null;
	}

	public void dispose() {
		mapViewer.dispose();
		timer.cancel();
		seedHistoryLogger = null;
		mapViewer = null;
		timer = null;
		saveLoader = null;
	}
}
