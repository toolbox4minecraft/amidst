package MoF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import amidst.Application;
import amidst.map.MapViewer;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.World;

// TODO: we should remove this and integrate it into Options
@Deprecated
public class Project {
	private Timer timer = new Timer();
	private JPanel panel = new JPanel();
	private MapViewer mapViewer;
	private Application application;

	public Project(Application application, World world) {
		long seed = world.getSeed();
		this.application = application;
		createWorld(seed, world.getWorldType().getName());
		initMapViewer();
		initPanel();
		initTimer();
	}

	private void createWorld(long seed, String type) {
		if (application.isFileWorld()) {
			String options = application.getWorldAsFileWorld()
					.getGeneratorOptions();
			MinecraftUtil.createWorld(seed, type, options);
		} else {
			MinecraftUtil.createWorld(seed, type);
		}
	}

	private void initMapViewer() {
		mapViewer = new MapViewer(this, application);
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

	@Deprecated
	public KeyListener getKeyListener() {
		return mapViewer.getKeyListener();
	}

	public JPanel getPanel() {
		return panel;
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

	public void dispose() {
		mapViewer.dispose();
		timer.cancel();
		mapViewer = null;
		timer = null;
	}
}
