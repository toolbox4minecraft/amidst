package amidst.gui.seedsearcher;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;

import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.filter.WorldFilter;
import amidst.filter.WorldFilterResult;
import amidst.filter.WorldFilterResult.ResultItem;
import amidst.filter.json.WorldFilterJson;
import amidst.filter.json.WorldFilterParseException;
import amidst.gui.main.MainWindowDialogs;
import amidst.gui.main.WorldSwitcher;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.Coordinates;
import net.miginfocom.swing.MigLayout;

@NotThreadSafe
public class SeedSearcherWindow {
	
	private final AmidstMetaData metadata;
	private final AmidstSettings settings;
	
	private final MainWindowDialogs dialogs;
	private final WorldSwitcher worldSwitcher;

	private final SeedSearcher seedSearcher;

	private final JTextArea searchQueryTextArea;
	private final JComboBox<WorldType> worldTypeComboBox;
	private final JCheckBox searchContinuouslyCheckBox;
	private final JSpinner searchMaxHitsSpinner;
	private final JButton searchButton;
	private final JLabel worldsFoundLabel;
	private final JLabel goalsMetLabel;
	private final JList<WorldFilterResult> worldsFoundList;
	private final JList<Map.Entry<Coordinates, ResultItem>> worldItemsList;
	private final JFrame frame;
	
	private final DefaultListModel<WorldFilterResult> worldsFound;
	private final DefaultListModel<Map.Entry<Coordinates, ResultItem>> worldItems;

	@CalledOnlyBy(AmidstThread.EDT)
	public SeedSearcherWindow(
			AmidstMetaData metadata,
			AmidstSettings settings,
			MainWindowDialogs dialogs,
			WorldSwitcher worldSwitcher,
			SeedSearcher seedSearcher) {
		this.metadata = metadata;
		this.settings = settings;
		this.dialogs = dialogs;
		this.worldSwitcher = worldSwitcher;
		this.seedSearcher = seedSearcher;
		this.worldsFound = new DefaultListModel<>();
		this.worldItems = new DefaultListModel<>();
		this.searchQueryTextArea = createSearchQueryTextArea();
		this.worldTypeComboBox = createWorldTypeComboBox();
		this.searchContinuouslyCheckBox = createSearchContinuouslyCheckBox();
		this.searchMaxHitsSpinner = createSearchMaxHitsSpinner();
		this.searchButton = createSearchButton();
		this.worldsFoundLabel = new JLabel();
		this.goalsMetLabel = new JLabel();
		this.worldsFoundList = createWorldsFoundList();
		this.worldItemsList = createWorldItemsList();
		this.frame = createFrame();
		
		tryReloadFromFile();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void show() {
		this.frame.setVisible(true);
		updateGUI();
		onWorldSelected(null, false);
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
		return new JComboBox<>(WorldType.getSelectableArray());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JCheckBox createSearchContinuouslyCheckBox() {
		JCheckBox result = new JCheckBox("search continuously until");
		result.addActionListener(e -> {
			searchMaxHitsSpinner.setEnabled(result.isSelected());
		});
		return result;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private JSpinner createSearchMaxHitsSpinner() {
		return new JSpinner(new SpinnerNumberModel(100, 0, null, 1));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JButton createSearchButton() {
		JButton result = new JButton("Search");
		result.addActionListener(e -> searchButtonClicked());
		return result;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private JList<WorldFilterResult> createWorldsFoundList() {
		JList<WorldFilterResult> result = new JList<>(worldsFound);
		result.setCellRenderer(new ResultCellRenderer<>(filterResult -> {
			WorldSeed seed = filterResult.getWorldOptions().getWorldSeed();
			
			String txt = seed.getLong() + ": ";
			
			int nbGoals = filterResult.getOptionalGoals().size();
			txt +=  nbGoals + (nbGoals > 1 ? " goals" : " goal");
			
			return txt;
		}));
		result.addListSelectionListener(e -> {
			onWorldSelected(result.getSelectedValue(), e.getValueIsAdjusting());
		});
		return result;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private JList<Map.Entry<Coordinates, ResultItem>> createWorldItemsList() {
		JList<Map.Entry<Coordinates, ResultItem>> result = new JList<>(worldItems);
		result.setCellRenderer(new ResultCellRenderer<>(entry -> {
			String txt = entry.getKey() + ": ";
			
			ResultItem item = entry.getValue(); 
			if(item.biome != null) {
				txt += item.biome.getName();
				if(!item.icons.isEmpty())
					txt += " with ";
			}
			
			txt += item.icons.stream().map(s -> s.getLabel()).collect(Collectors.joining(", "));
					
			return txt;
		}));
		//TODO display some sort of marker on the world?
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame result = new JFrame("Seed Searcher");
		result.setIconImages(metadata.getIcons());
		result.getContentPane().setLayout(new GridLayout(1, 2));
		
		result.add(createSearchPanel());
		result.add(createResultsPanel());
		
		result.setSize(800, 600);
		result.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		result.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				seedSearcher.stop();
			}
		});
		return result;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private JPanel createSearchPanel() {
		JPanel result = new JPanel(new MigLayout());
		
		result.add(new JLabel("Search Query:"), "growx, pushx, wrap");
		
		result.add(createScrollPane(searchQueryTextArea, true), "grow, push, wrap");
		result.add(new JLabel("World Type:"), "growx, pushx, wrap");
		result.add(worldTypeComboBox, "growx, pushx, wrap");
		result.add(searchContinuouslyCheckBox, "split 3");
		result.add(searchMaxHitsSpinner, "w 60!");
		result.add(new JLabel("worlds"), "growx, pushx, wrap");
		result.add(searchButton, "pushx, wrap");
		
		return result;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private JPanel createResultsPanel() {
		JPanel result = new JPanel(new MigLayout());

		result.add(worldsFoundLabel, "growx, pushx, wrap");
		result.add(createScrollPane(worldsFoundList, false), "growx, pushx, growy, pushy, wrap");
		result.add(goalsMetLabel, "growx, pushx, wrap");
		result.add(new JLabel("Items found:"), "growx, pushx, gaptop 15, wrap");
		result.add(createScrollPane(worldItemsList, false), "growx, pushx, wrap");
		
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JScrollPane createScrollPane(JComponent component, boolean horizontalScrolling) {
		JScrollPane result = new JScrollPane(component);
		int scrollPolicy = horizontalScrolling
				? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
				: ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
			result.setHorizontalScrollBarPolicy(scrollPolicy);
		result.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
		return result;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private void tryReloadFromFile() {
		Path file = Paths.get(settings.searchJsonFile.get());
		
		if(Files.notExists(file)) {
			AmidstLogger.info("The search file " + file + "doesn't exist: abort loading");
			return;
		}
		
		try {
			List<String> lines = Files.readAllLines(file);
			String content = lines.stream().collect(Collectors.joining("\n"));
			searchQueryTextArea.setText(content);
			
		} catch (IOException e) {
			AmidstLogger.warn("Unable to read search file " + file + ": " + e.getMessage());
			dialogs.displayError("Could not load file; see logs.");
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void searchButtonClicked() {
		if (seedSearcher.isSearching()) {
			seedSearcher.stop();
		} else {
			worldsFound.clear();
			try {
				SeedSearcherConfiguration seedSearcherConfiguration = createSeedSearcherConfiguration();
				seedSearcher.search(seedSearcherConfiguration, world -> worldFound(world));
				
			} catch (WorldFilterParseException e) {
				AmidstLogger.warn("invalid configuration: {}", e.getMessage());
				AmidstMessageBox.displayError("Invalid configuration", e.getMessage());
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
	private void worldFound(WorldFilterResult filterResult) {
		worldsFound.addElement(filterResult);
		if(worldsFound.size() == 1)
			worldsFoundList.setSelectedIndex(0);
		
		int maxHits = (int) searchMaxHitsSpinner.getValue();
		if(maxHits > 0 && worldsFound.size() >= maxHits)
			seedSearcher.stop();
		
		updateGUI();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateGUI() {
		int nbWorlds = worldsFound.size();
		if(nbWorlds == 0)
			worldsFoundLabel.setText("No worlds found");
		else if(nbWorlds == 1)
			worldsFoundLabel.setText("1 world found:");
		else worldsFoundLabel.setText(nbWorlds + " worlds found:");
		
		if (seedSearcher.isSearching() && !seedSearcher.isStopRequested()) {
			searchButton.setText("Stop");
			searchQueryTextArea.setEditable(false);
			worldTypeComboBox.setEnabled(false);
			searchContinuouslyCheckBox.setEnabled(false);
			searchMaxHitsSpinner.setEnabled(false);
		} else {
			searchButton.setText("Search");
			searchQueryTextArea.setEditable(true);
			worldTypeComboBox.setEnabled(true);
			searchContinuouslyCheckBox.setEnabled(true);
			searchMaxHitsSpinner.setEnabled(searchContinuouslyCheckBox.isSelected());
		}
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private void onWorldSelected(WorldFilterResult result, boolean isAdjusting) {
		worldItems.clear();
		
		if(result == null) {
			goalsMetLabel.setText("Goals met: -");
			return;
		}
				
		String goals = result.getOptionalGoals().stream().collect(Collectors.joining(", ", "Goals met: ", ""));
		goalsMetLabel.setText(goals);
		
		result.getItems().entrySet().stream()
			.sorted(Comparator.comparingDouble(e -> e.getKey().getDistanceSq(Coordinates.origin())))
			.forEachOrdered(worldItems::addElement);
		
		if(!isAdjusting)
			worldSwitcher.displayWorld(result.getWorldOptions());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		seedSearcher.dispose();
	}
}
