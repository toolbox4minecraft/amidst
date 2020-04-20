package amidst.gui.exporter;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.gui.main.viewer.FragmentGraphToScreenTranslator;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.export.WorldExporterConfiguration;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import java.util.concurrent.Executors;

import static java.awt.GridBagConstraints.*;

@NotThreadSafe
public class WorldExporterDialog {
	private static final int PREVIEW_SIZE = 100;
	private static final Insets DEFAULT_INSETS = new Insets(10, 10, 10, 10);
	
	private final ExecutorService previewUpdator = Executors.newSingleThreadExecutor(r -> new Thread(r));
	
	private final WorldOptions worldOptions;
	private final BiomeDataOracle biomeDataOracle;
	private final Actions actions;
	private final BiomeProfileSelection biomeProfileSelection;
	private final CompletableFuture<WorldExporterConfiguration> futureConfiguration;
	
	private final GridBagConstraints constraints;
	private final GridBagConstraints labelPaneConstraints;
	
	private final JSpinner leftSpinner, topSpinner, rightSpinner, bottomSpinner;
	
	private final JCheckBox fullResCheckBox;
	
	private final JTextField pathField;
	private final JButton browseButton;
	
	private final JButton exportButton;
	
	private final BufferedImage previewImage;
	private final ImageIcon previewIcon;
	private final JLabel previewLabel;
	
	private final JDialog dialog;
	
	public WorldExporterDialog(World world,
							 Actions actions,
							 FragmentGraphToScreenTranslator translator,
							 BiomeProfileSelection biomeProfileSelection) {
		// @formatter:off
		this.worldOptions          = world.getWorldOptions();
		this.biomeDataOracle       = world.getBiomeDataOracle();
		this.actions               = actions;
		this.biomeProfileSelection = biomeProfileSelection;
		this.futureConfiguration   = new CompletableFuture<WorldExporterConfiguration>();
		this.constraints           = new GridBagConstraints();
		this.labelPaneConstraints  = new GridBagConstraints();
		
		CoordinatesInWorld defaultTopLeft = translator.screenToWorld(new Point(0, 0));
		CoordinatesInWorld defaultBottomRight = translator.screenToWorld(new Point((int) translator.getWidth(), (int) translator.getHeight()));
		
		this.leftSpinner           = createCoordinateSpinner(defaultTopLeft.getX());
		this.topSpinner            = createCoordinateSpinner(defaultTopLeft.getY());
		this.rightSpinner          = createCoordinateSpinner(defaultBottomRight.getX());
		this.bottomSpinner         = createCoordinateSpinner(defaultBottomRight.getY());
		this.fullResCheckBox       = new JCheckBox("Full Resolution");
		this.pathField             = new JTextField(Paths.get("").toAbsolutePath().toString());
		this.browseButton          = createBrowseButton();
		this.exportButton          = createExportButton();
		this.previewImage          = new BufferedImage(PREVIEW_SIZE, PREVIEW_SIZE, BufferedImage.TYPE_INT_ARGB);
		this.previewIcon           = new ImageIcon(new BufferedImage((int) (PREVIEW_SIZE * 2), (int) (PREVIEW_SIZE * 2), BufferedImage.TYPE_INT_ARGB));
		this.previewLabel          = createPreviewLabel();
		this.dialog                = createDialog();
		// @formatter:on
	}
	
	private JSpinner createCoordinateSpinner(long defaultValue) {
		JSpinner newSpinner = new JSpinner(new SpinnerNumberModel(defaultValue, -30000000, 30000000, 25));
		newSpinner.addChangeListener(e -> {
			renderPreview();
		});
		return newSpinner;
	}
	
	private JLabel createPreviewLabel() {
		JLabel newLabel = new JLabel();
		
		newLabel.setIcon(previewIcon);
		newLabel.setBorder(new LineBorder(Color.BLACK, 2));
		return newLabel;
	}
	
	private JButton createExportButton() {
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener((e) -> {
			CoordinatesInWorld topLeft = getTopLeftCoordinates();
			CoordinatesInWorld bottomRight = getBottomRightCoordinates();
			if (verifyImageCoordinates(topLeft, bottomRight) && verifyPathString(pathField.getText())) {
				futureConfiguration.complete(
						new WorldExporterConfiguration(
								Paths.get(pathField.getText()),
								!fullResCheckBox.isSelected(),
								topLeft,
								bottomRight,
								biomeProfileSelection
							)
					);
				dialog.dispose();
			}
		});
		return exportButton;
	}
	
	private boolean verifyPathString(String path) {
		try {
			Path p = Paths.get(path);
			if (Files.isWritable(p)) {
				return true;
			} else {
				AmidstMessageBox.displayError(dialog, "Error", "Path is not able to be written to.");
				return false;
			}
		} catch (InvalidPathException e) {
			AmidstMessageBox.displayError(dialog, "Error", "Invalid path given.");
		}
		return false;
	}
	
	public boolean verifyImageCoordinates(CoordinatesInWorld topLeft, CoordinatesInWorld bottomRight) {
		if((topLeft != null && bottomRight != null) && 
		   (topLeft.getX() >= bottomRight.getX() || topLeft.getY() >= bottomRight.getY())) {
			String message = "Unable to create image: Invalid image coordinates detected.";
			AmidstLogger.warn(message);
			AmidstMessageBox.displayError(dialog, "Error", message);
			return false;
		} else {
			return true;
		}
	}

	
	private JButton createBrowseButton() {
		JButton newButton = new JButton("Browse...");
		newButton.addActionListener(e -> {
			Path exportPath = actions.getExportPath(worldOptions, dialog);
			if (exportPath != null) {
				pathField.setText(exportPath.toAbsolutePath().toString());
			}
		});
		return newButton;
	}
	
	private JPanel createLabeledPanel(String label, Component component, int fillConst) {
		JPanel newPanel = new JPanel(new GridBagLayout());
		
		JLabel newLabel = new JLabel(label);
		newLabel.setHorizontalAlignment(SwingConstants.CENTER);
		newLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		setLabelPaneConstraints(0, 0, 0, 0, HORIZONTAL, 0, 0, 1, 1, 1.0, 0.0, SOUTH);
		newPanel.add(newLabel, labelPaneConstraints);
		
		setLabelPaneConstraints(0, 0, 0, 0, fillConst, 0, 1, 1, 1, 1.0, 0.0, CENTER);
		newPanel.add(component, labelPaneConstraints);
		
		return newPanel;
	}
	
	private JDialog createDialog() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		setConstraints(40, 0, 0, 0, NONE, 1, 1, 1, 1, 0.0, 0.0, SOUTH);
		panel.add(createLabeledPanel("Top:", topSpinner, NONE), constraints);
		
		setConstraints(20, 20, 0, 0, NONE, 0, 2, 1, 1, 0.0, 0.0, SOUTH);
		panel.add(createLabeledPanel("Left:", leftSpinner, NONE), constraints);
		
		setConstraints(20, 0, 0, 0, NONE, 1, 3, 1, 1, 0.0, 0.0, SOUTH);
		panel.add(createLabeledPanel("Bottom:", bottomSpinner, NONE), constraints);
		
		setConstraints(20, 0, 0, 0, NONE, 2, 2, 1, 1, 0.0, 0.0, SOUTH);
		panel.add(createLabeledPanel("Right:", rightSpinner, NONE), constraints);
		
		setConstraints(10, 20, 0, 0, NONE, 0, 5, 2, 1, 0.0, 0.0, SOUTHWEST);
		panel.add(fullResCheckBox, constraints);
		
		setConstraints(0, 15, 0, 15, BOTH, 3, 0, 1, 6, 1.0, 0.0, CENTER);
		panel.add(Box.createGlue(), constraints);
		
		setConstraints(0, 0, 0, 0, BOTH, 0, 4, 4, 1, 0.0, 1.0, CENTER);
		panel.add(Box.createGlue(), constraints);
		
		JPanel pathPanel = new JPanel(new GridBagLayout());
		
		setConstraints(0, 0, 0, 0, HORIZONTAL, 0, 0, 1, 1, 0.0, 0.0, CENTER);
		pathPanel.add(pathField, constraints);
		
		setConstraints(0, 10, 0, 0, HORIZONTAL, 1, 0, 1, 1, 0.0, 0.0, CENTER);
		pathPanel.add(browseButton, constraints);
		
		setConstraints(10, 20, 20, 10, BOTH, 0, 6, 4, 2, 0.0, 0.0, SOUTHWEST);
		panel.add(createLabeledPanel("Path:", pathPanel, HORIZONTAL), constraints);
		
		setConstraints(15, 10, 10, 20, BOTH, 4, 0, 1, 7, 0.0, 0.0, EAST);
		panel.add(createLabeledPanel("Preview:", previewLabel, NONE), constraints);
		
		setConstraints(10, 10, 20, 20, NONE, 4, 7, 1, 1, 0.0, 0.0, SOUTHEAST);
		panel.add(exportButton, constraints);
		
		JDialog newDialog = new JDialog(actions.getFrame(), "Export Biome Image") {
			private static final long serialVersionUID = 827399282059202834L;

			public void dispose() {
				super.dispose();
				futureConfiguration.complete(null);
			}
		};
		newDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		newDialog.add(panel);
		newDialog.pack();
		newDialog.setResizable(false);
		return newDialog;
	}
	
	private Future<?> renderTask;
	
	private void renderPreview() {
		if(renderTask != null && !renderTask.isDone()) {
			renderTask.cancel(true);
		}
		
		renderTask = previewUpdator.submit(() -> {
			final int quarterResFactor = 4;
			try {
				clearImage(previewImage);
				
				// We use a direct int array because it's much faster than calling setRGB()
				int[] pixels = ((DataBufferInt) previewImage.getRaster().getDataBuffer()).getData();
				
				CoordinatesInWorld topLeft = getTopLeftCoordinates();
				CoordinatesInWorld bottomRight = getBottomRightCoordinates();
				
				int worldWidth = (int) (bottomRight.getX() - topLeft.getX());
				int worldHeight = (int) -(topLeft.getY() - bottomRight.getY());
				
				int worldLongestSide = Math.max(worldWidth, worldHeight);
				
				double imgToWorldFactor = worldLongestSide / (double) PREVIEW_SIZE;
				
				int imgXOffset = (int) (((worldLongestSide - worldWidth) / 2) / imgToWorldFactor);
				int imgYOffset = (int) (((worldLongestSide - worldHeight) / 2) / imgToWorldFactor);
				
				int imgHeightWithoutBorders = previewImage.getHeight() - imgYOffset * 2;
				int imgWidthWithoutBorders = previewImage.getWidth() - imgXOffset * 2;
				for(int y = 0; y < imgHeightWithoutBorders; y++) {
					for(int x = 0; x < imgWidthWithoutBorders; x++) {
						int worldX = (int) ((x * imgToWorldFactor + topLeft.getX()) / quarterResFactor);
						int worldY = (int) ((y * imgToWorldFactor + topLeft.getY()) / quarterResFactor);
						int imgX = x + imgXOffset;
						int imgY = y + imgYOffset;
						
						// we use imgY instead of (previewImage.getHeight() - imgY - 1) to mirror the y axis
						int imgidx = imgY * previewImage.getWidth() + imgX;
						pixels[imgidx] = biomeProfileSelection.getBiomeColorOrUnknown(biomeDataOracle.getBiomeAt(worldX, worldY, true)).getRGB();
					}
				}
				
				previewIcon.setImage(previewImage.getScaledInstance(previewIcon.getIconWidth(), previewIcon.getIconHeight(), Image.SCALE_FAST));
				
				SwingUtilities.invokeLater(() -> previewLabel.repaint());
			} catch(MinecraftInterfaceException | UnknownBiomeIdException e) {
				AmidstLogger.error(e);
			}
		});
	}
	
	private void clearImage(BufferedImage img) {
		Graphics2D graphics = (Graphics2D) img.getGraphics();
		Composite tempComposite = graphics.getComposite();
		graphics.setComposite(AlphaComposite.Clear);
		graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
		graphics.setComposite(tempComposite);
	}
	
	public void show() {
		dialog.setVisible(true);
		renderPreview();
		updateGUI();
	}
	
	private void updateGUI() {
		
	}
	
	private CoordinatesInWorld getTopLeftCoordinates() {
		return new CoordinatesInWorld(((Double) leftSpinner.getValue()).intValue(), ((Double) topSpinner.getValue()).intValue());
	}
	
	private CoordinatesInWorld getBottomRightCoordinates() {
		return new CoordinatesInWorld(((Double) rightSpinner.getValue()).intValue(), ((Double) bottomSpinner.getValue()).intValue());
	}
	
	public Future<WorldExporterConfiguration> getWorldExporterConfiguration() {
		return futureConfiguration;
	}
	
	private void setConstraints(int iTop, int iLeft, int iBottom, int iRight, int fillConst, int gridx,
			int gridy, int gridw, int gridh, double weightx, double weighty, int anchor) {
		constraints.insets = new Insets(iTop, iLeft, iBottom, iRight);
		constraints.fill = fillConst;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridw;
		constraints.gridheight = gridh;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		constraints.anchor = anchor;
	}
	
	@SuppressWarnings("unused")
	private void setConstraints(int fillConst, int gridx, int gridy, int gridw, int gridh,
			double weightx, double weighty, int anchor) {
		constraints.insets = DEFAULT_INSETS;
		constraints.fill = fillConst;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridw;
		constraints.gridheight = gridh;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		constraints.anchor = anchor;
	}
	
	@SuppressWarnings("unused")
	private void setLabelPaneConstraints(int fillConst, int gridx, int gridy, int gridw, int gridh,
			double weightx, double weighty, int anchor) {
		labelPaneConstraints.insets = DEFAULT_INSETS;
		labelPaneConstraints.fill = fillConst;
		labelPaneConstraints.gridx = gridx;
		labelPaneConstraints.gridy = gridy;
		labelPaneConstraints.gridwidth = gridw;
		labelPaneConstraints.gridheight = gridh;
		labelPaneConstraints.weightx = weightx;
		labelPaneConstraints.weighty = weighty;
		labelPaneConstraints.anchor = anchor;
	}
	
	private void setLabelPaneConstraints(int iTop, int iLeft, int iBottom, int iRight, int fillConst, int gridx,
			int gridy, int gridw, int gridh, double weightx, double weighty, int anchor) {
		labelPaneConstraints.insets = new Insets(iTop, iLeft, iBottom, iRight);
		labelPaneConstraints.fill = fillConst;
		labelPaneConstraints.gridx = gridx;
		labelPaneConstraints.gridy = gridy;
		labelPaneConstraints.gridwidth = gridw;
		labelPaneConstraints.gridheight = gridh;
		labelPaneConstraints.weightx = weightx;
		labelPaneConstraints.weighty = weighty;
		labelPaneConstraints.anchor = anchor;
	}
	
}
