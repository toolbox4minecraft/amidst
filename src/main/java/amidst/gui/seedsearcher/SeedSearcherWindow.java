package amidst.gui.seedsearcher;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.MainWindow;
import amidst.logging.Log;
import amidst.mojangapi.file.json.filter.WorldFilterJson;
import amidst.mojangapi.file.json.filter.WorldFilterParseException;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.filter.WorldFilter;

@NotThreadSafe
public class SeedSearcherWindow {
	
	private final AmidstMetaData metadata;
	private final AmidstSettings settings;
	private final MainWindow mainWindow;
	private final SeedSearcher seedSearcher;

	private final JTextArea searchQueryTextArea;
	private final JComboBox<WorldType> worldTypeComboBox;
	private final JCheckBox searchContinuouslyCheckBox;
	private final JButton searchButton;
	private final JFrame frame;

	@CalledOnlyBy(AmidstThread.EDT)
	public SeedSearcherWindow(AmidstMetaData metadata, AmidstSettings settings,
			MainWindow mainWindow, SeedSearcher seedSearcher) {
		this.metadata = metadata;
		this.settings = settings;
		this.mainWindow = mainWindow;
		this.seedSearcher = seedSearcher;
		this.searchQueryTextArea = createSearchQueryTextArea();
		this.worldTypeComboBox = createWorldTypeComboBox();
		this.searchContinuouslyCheckBox = createSearchContinuouslyCheckBox();
		this.searchButton = createSearchButton();
		this.frame = createFrame();
		
		tryReloadFromFile();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void show() {
		this.frame.setVisible(true);
		updateGUI();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JTextArea createSearchQueryTextArea() {
		JTextArea area = new JTextArea();
		
		area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		area.setTabSize(2);
		return area;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JComboBox<WorldType> createWorldTypeComboBox() {
		return new JComboBox<WorldType>(WorldType.getSelectableArray());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JCheckBox createSearchContinuouslyCheckBox() {
		return new JCheckBox("search continuously");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JButton createSearchButton() {
		JButton result = new JButton("Search");
		result.addActionListener(e -> searchButtonClicked());
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame result = new JFrame("Seed Searcher");
		result.setIconImages(metadata.getIcons());
		result.getContentPane().setLayout(new MigLayout());
		result.add(new JLabel("Search Query:"), "growx, pushx, wrap");
		result.add(createScrollPane(searchQueryTextArea), "grow, push, wrap");
		result.add(new JLabel("World Type:"), "growx, pushx, wrap");
		result.add(worldTypeComboBox, "growx, pushx, wrap");
		result.add(searchContinuouslyCheckBox, "growx, pushx, wrap");
		result.add(searchButton, "pushx, wrap");
		result.setSize(800, 600);
		result.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JScrollPane createScrollPane(JTextArea textArea) {
		JScrollPane result = new JScrollPane(textArea);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		result.setBorder(new LineBorder(Color.darkGray, 1));
		return result;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private void tryReloadFromFile() {
		Path file = Paths.get(settings.searchJsonFile.get());
		
		if(Files.notExists(file)) {
			Log.i("The search file " + file + "doesn't exist: abort loading");
			return;
		}
		
		try {
			List<String> lines = Files.readAllLines(file);
			String content = lines.stream().collect(Collectors.joining("\n"));
			searchQueryTextArea.setText(content);
			
		} catch (IOException e) {
			Log.w("Unable to read search file " + file + ": " + e.getMessage());
			mainWindow.displayError("Could not load file; see logs.");
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void searchButtonClicked() {
		if (seedSearcher.isSearching()) {
			seedSearcher.stop();
		} else {
			try {
				SeedSearcherConfiguration seedSearcherConfiguration = createSeedSearcherConfiguration();
				WorldType worldType = seedSearcherConfiguration.getWorldType();
				seedSearcher.search(seedSearcherConfiguration, worldSeed -> seedFound(worldSeed, worldType));
			} catch (WorldFilterParseException e) {
				mainWindow.displayError(e.getMessage());
			}
		}
		updateGUI();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private SeedSearcherConfiguration createSeedSearcherConfiguration() throws WorldFilterParseException {
		WorldFilterJson filter = WorldFilterJson.fromJSON(searchQueryTextArea.getText());
		return createSeedSearcherConfiguration(filter.validate());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private SeedSearcherConfiguration createSeedSearcherConfiguration(WorldFilter worldFilter) {
		return new SeedSearcherConfiguration(
				worldFilter,
				(WorldType) worldTypeComboBox.getSelectedItem(),
				searchContinuouslyCheckBox.isSelected());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void seedFound(WorldSeed worldSeed, WorldType worldType) {
		mainWindow.displayWorld(worldSeed, worldType);
		updateGUI();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateGUI() {
		if (seedSearcher.isSearching() && !seedSearcher.isStopRequested()) {
			searchButton.setText("Stop");
			searchQueryTextArea.setEditable(false);
			worldTypeComboBox.setEnabled(false);
			searchContinuouslyCheckBox.setEnabled(false);
		} else {
			searchButton.setText("Search");
			searchQueryTextArea.setEditable(true);
			worldTypeComboBox.setEnabled(true);
			searchContinuouslyCheckBox.setEnabled(true);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		seedSearcher.dispose();
	}
}
