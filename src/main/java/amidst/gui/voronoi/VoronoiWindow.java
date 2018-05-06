package amidst.gui.voronoi;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.minetest.world.mapgen.MinetestBiomeProfileImpl;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.settings.biomeprofile.BiomeProfileUpdateListener;

public class VoronoiWindow implements BiomeProfileUpdateListener, ChangeListener {

	private static final int ALTITUDESLIDER_DEFAULT_LOW    = -40;
	private static final int ALTITUDESLIDER_DEFAULT_HIGH   = 200;
	private static final int ALTITUDESLIDER_STARTING_VALUE = 10; // higher than beaches and oceans, so we start showing "normal" biomes.

	private static VoronoiWindow voronoiWindow = null;
	private BiomeProfileSelection biomeProfileSelection;

	private final JFrame windowFrame;
	private VoronoiPanel voronoiPanel;
	private JSlider altitudeSlider;
	private JLabel graphHeading;
	private JCheckBox option_showAxis;
	private JCheckBox option_showLabels;
	private JCheckBox option_showNodes;
	private JSpinner altitudeOffset;

	private MinetestBiomeProfileImpl selectedProfile = null;

	@CalledOnlyBy(AmidstThread.EDT)
	private VoronoiWindow() {
		this.windowFrame = createWindowFrame(800, 764);
		setOptionFlagsInDialog(VoronoiPanel.FLAG_SHOWLABELS | VoronoiPanel.FLAG_SHOWAXIS | VoronoiPanel.FLAG_SHOWNODES);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createWindowFrame(int width, int height) {
		JFrame result = new JFrame();

		result.getContentPane().setLayout(new MigLayout());
		result.setSize(width, height);
		result.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				result.setVisible(false);
			}
		});

		this.voronoiPanel = new VoronoiPanel();
		result.add(voronoiPanel, "grow, pushx, spany 2");// the next row (which we span) will be "push", so don't do it here - the rest of this row needs to be thin

		JLabel heightLabel = new JLabel("Altitude");
		result.add(heightLabel, "center, wrap");

		altitudeSlider = new JSlider(JSlider.VERTICAL, ALTITUDESLIDER_DEFAULT_LOW, ALTITUDESLIDER_DEFAULT_HIGH, ALTITUDESLIDER_STARTING_VALUE);
		altitudeSlider.addChangeListener(this);
		altitudeSlider.setMajorTickSpacing(10);
		altitudeSlider.setMinorTickSpacing(5);
		altitudeSlider.setPaintTicks(true);
		altitudeSlider.setPaintLabels(true);
		result.add(altitudeSlider, "grow, pushy, wrap");

		graphHeading = new JLabel();
		result.add(graphHeading, "center");

		JLabel offsetLabel = new JLabel("Altitude offset:");
		result.add(offsetLabel, "left, wrap");

		option_showAxis = new JCheckBox("Show axes");
		option_showNodes = new JCheckBox("Show nodes");
		option_showLabels = new JCheckBox("Show labels");
		option_showAxis.addChangeListener(this);
		option_showNodes.addChangeListener(this);
		option_showLabels.addChangeListener(this);
		result.add(option_showAxis, "center, split 3");
		result.add(option_showNodes, "center");
		result.add(option_showLabels, "center");

		altitudeOffset = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE - ALTITUDESLIDER_DEFAULT_LOW, Short.MAX_VALUE - ALTITUDESLIDER_DEFAULT_HIGH, 100));
		altitudeOffset.addChangeListener(this);
		result.add(altitudeOffset);

		return result;
	}

	@Override
	public void onBiomeProfileUpdate(BiomeProfile newBiomeProfile) {
		UpdateSelectedBiomeProfile(newBiomeProfile);
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == altitudeOffset) {
			updateHeightSlider((int)altitudeOffset.getValue());
		}
		updateVoronoiDiagram();
	}

	private void UpdateSelectedBiomeProfile(BiomeProfile newProfile) {

		MinetestBiomeProfileImpl minetestProfile = (newProfile instanceof MinetestBiomeProfileImpl) ? (MinetestBiomeProfileImpl)newProfile : null;

		boolean changed = (minetestProfile == null) ? (selectedProfile != null) : !minetestProfile.equals(selectedProfile);
		selectedProfile = minetestProfile;

		if (changed) updateVoronoiDiagram();
		this.windowFrame.setTitle(selectedProfile == null ? "Biome profile Voronoi graph" : "Voronoi graph for " + selectedProfile.getName());
	}

	private void updateVoronoiDiagram() {
		EventQueue.invokeLater(
			() -> {
				int altitude = getAltitudeFromDialog();
				voronoiPanel.Update(selectedProfile, altitude, getOptionFlagsFromDialog());
				graphHeading.setText("Biomes at altitude " + altitude);
			}
		);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateHeightSlider(int offset) {
		int oldHeightPosition = altitudeSlider.getValue() - altitudeSlider.getMinimum();
		altitudeSlider.setMinimum(ALTITUDESLIDER_DEFAULT_LOW + offset);
		altitudeSlider.setMaximum(ALTITUDESLIDER_DEFAULT_HIGH + offset);
		altitudeSlider.setValue(ALTITUDESLIDER_DEFAULT_LOW + offset + oldHeightPosition);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getAltitudeFromDialog() {
		return altitudeSlider.getValue();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setOptionFlagsInDialog(int optionFlags) {
		option_showAxis.setSelected((optionFlags & VoronoiPanel.FLAG_SHOWAXIS) > 0);
		option_showNodes.setSelected((optionFlags & VoronoiPanel.FLAG_SHOWNODES) > 0);
		option_showLabels.setSelected((optionFlags & VoronoiPanel.FLAG_SHOWLABELS) > 0);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getOptionFlagsFromDialog() {
		int result = 0;
		if (option_showAxis.isSelected()) result  |= VoronoiPanel.FLAG_SHOWAXIS;
		if (option_showNodes.isSelected()) result  |= VoronoiPanel.FLAG_SHOWNODES;
		if (option_showLabels.isSelected()) result  |= VoronoiPanel.FLAG_SHOWLABELS;
		return result;
	}

	public static void showDiagram(BiomeProfileSelection biomeProfileSelection) {

		if (voronoiWindow == null) {
			voronoiWindow = new VoronoiWindow();
		}
		SwingUtilities.invokeLater(() -> voronoiWindow.show(biomeProfileSelection));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void show(BiomeProfileSelection biomeProfileSelection) {

		if (this.biomeProfileSelection != null) {
			this.biomeProfileSelection.removeUpdateListener(this);
		}

		this.biomeProfileSelection = biomeProfileSelection;
		
		BiomeProfile newProfile = null;
		if (biomeProfileSelection != null) {
			this.biomeProfileSelection.addUpdateListener(this);
			newProfile = this.biomeProfileSelection.getCurrentBiomeProfile();
		}

		UpdateSelectedBiomeProfile(newProfile);
		this.windowFrame.setVisible(true);
	}
}
