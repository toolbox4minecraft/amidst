package amidst.gui.main.viewer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.File;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.TiledImage;

import com.sun.media.jai.codec.TIFFEncodeParam;

import amidst.dependency.injection.Factory1;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerManager;
import amidst.fragment.layer.LayerReloader;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.export.WorldExporter;
import amidst.mojangapi.world.export.WorldExporterConfiguration;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.threading.WorkerExecutor;

/**
 * This class works as wrapper around a world instance. It holds everything that
 * is needed to display the world on the screen. This allows us to easily
 * exchange the currently displayed world.
 */
@NotThreadSafe
public class ViewerFacade {
	private final World world;
	private final FragmentManager fragmentManager;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Viewer viewer;
	private final LayerReloader layerReloader;
	private final WorldIconSelection worldIconSelection;
	private final LayerManager layerManager;
	private final WorkerExecutor workerExecutor;
	private final Factory1<WorldExporterConfiguration, WorldExporter> worldExporterFactory;
	private final Runnable onRepainterTick;
	private final Runnable onFragmentLoaderTick;
	private final Runnable onPlayerFinishedLoading;

	@CalledOnlyBy(AmidstThread.EDT)
	public ViewerFacade(
			World world,
			FragmentManager fragmentManager,
			FragmentGraph graph,
			FragmentGraphToScreenTranslator translator,
			Zoom zoom,
			Viewer viewer,
			LayerReloader layerReloader,
			WorldIconSelection worldIconSelection,
			LayerManager layerManager,
			WorkerExecutor workerExecutor,
			Factory1<WorldExporterConfiguration, WorldExporter> worldExporterFactory,
			Runnable onRepainterTick,
			Runnable onFragmentLoaderTick,
			Runnable onPlayerFinishedLoading) {
		this.world = world;
		this.fragmentManager = fragmentManager;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.viewer = viewer;
		this.layerReloader = layerReloader;
		this.worldIconSelection = worldIconSelection;
		this.layerManager = layerManager;
		this.workerExecutor = workerExecutor;
		this.worldExporterFactory = worldExporterFactory;
		this.onRepainterTick = onRepainterTick;
		this.onFragmentLoaderTick = onFragmentLoaderTick;
		this.onPlayerFinishedLoading = onPlayerFinishedLoading;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Component getComponent() {
		return viewer.getComponent();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reloadBackgroundLayer() {
		layerReloader.reloadBackgroundLayer();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reloadPlayerLayer() {
		layerReloader.reloadPlayerLayer();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		graph.dispose();
		zoom.skipFading();
		zoom.reset();
		world.dispose();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Runnable getOnRepainterTick() {
		return onRepainterTick;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Runnable getOnFragmentLoaderTick() {
		return onFragmentLoaderTick;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void centerOn(CoordinatesInWorld coordinates) {
		translator.centerOn(coordinates);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void centerOn(WorldIcon worldIcon) {
		translator.centerOn(worldIcon.getCoordinates());
		worldIconSelection.select(worldIcon);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public BufferedImage createScreenshot() {
		return viewer.createScreenshot();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(int notches) {
		zoom.adjustZoom(viewer.getMousePositionOrCenter(), notches);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(Point mousePosition, int notches) {
		zoom.adjustZoom(mousePosition, notches);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectWorldIcon(WorldIcon worldIcon) {
		worldIconSelection.select(worldIcon);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldOptions getWorldOptions() {
		return world.getWorldOptions();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void saveBiomesImage(File file, boolean useQuarterResolution) {
		TIFFEncodeParam tep = new TIFFEncodeParam();
		tep.setTileSize(Fragment.SIZE, Fragment.SIZE);
		tep.setWriteTiled(true);
		tep.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE);
		tep.setDeflateLevel(5);
		
		CoordinatesInWorld topLeft = translator.screenToWorld(new Point(0, 0));
		CoordinatesInWorld bottomRight = translator.screenToWorld(new Point((int) translator.getWidth(), (int) translator.getHeight()));
		int x = (int) topLeft.getX();
		int y = (int) topLeft.getY();
		int width = (int) bottomRight.getX() - x;
		int height = (int) bottomRight.getY() - y;
		
		int[] bitmasks = {
			0xFF0000,
			0x00FF00,
			0x0000FF
		};
		
		BiomeProfileSelection biomeColors = new BiomeProfileSelection(BiomeProfile.getDefaultProfile());
		TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0,
					new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, Fragment.SIZE, Fragment.SIZE, bitmasks),
					new DirectColorModel(32, bitmasks[0], bitmasks[1], bitmasks[2])
				);
		
		short[][] dataArray;
		for(int tx = 0; tx < tiledImage.getNumXTiles(); tx++) {
			for(int ty = 0; ty < tiledImage.getNumYTiles(); ty++) {
				Rectangle r = tiledImage.getTileRect(tx, ty);
				r.setLocation(tiledImage.tileXToX(tx), tiledImage.tileYToY(ty));
				dataArray = new short[r.width][r.height];
				world.getBiomeDataOracle().populateArray(new CoordinatesInWorld((long) x + r.x, (long) y + r.y), dataArray, useQuarterResolution);
			
				try {
					for(int i = 0; i < r.width; i++) {
						for(int j = 0; j < r.height; j++) {
							tiledImage.setSample(r.x + i, r.y + j, 0, biomeColors.getBiomeColor(dataArray[i][j]).getR());
							tiledImage.setSample(r.x + i, r.y + j, 1, biomeColors.getBiomeColor(dataArray[i][j]).getG());
							tiledImage.setSample(r.x + i, r.y + j, 2, biomeColors.getBiomeColor(dataArray[i][j]).getB());
						}
					}
				} catch (UnknownBiomeIndexException e) {
					e.printStackTrace();
				}
			}
		}
		dataArray = null;
		
		JAI.create("filestore", tiledImage, file.getAbsolutePath(), "TIFF", tep);
		
		tiledImage.dispose();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldIcon getSpawnWorldIcon() {
		return world.getSpawnWorldIcon();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public List<WorldIcon> getStrongholdWorldIcons() {
		return world.getStrongholdWorldIcons();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public List<WorldIcon> getPlayerWorldIcons() {
		return world.getPlayerWorldIcons();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public MovablePlayerList getMovablePlayerList() {
		return world.getMovablePlayerList();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean canLoadPlayerLocations() {
		return world.getMovablePlayerList().canLoad();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void loadPlayers() {
		worldIconSelection.clear();
		world.getMovablePlayerList().load(workerExecutor, onPlayerFinishedLoading);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean canSavePlayerLocations() {
		return world.getMovablePlayerList().canSave();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void savePlayerLocations() {
		world.getMovablePlayerList().save();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean calculateIsLayerEnabled(int layerId, Dimension dimension) {
		return layerManager.calculateIsEnabled(layerId, dimension);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean hasLayer(int layerId) {
		return world.getEnabledLayers().contains(layerId);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void export(WorldExporterConfiguration configuration) {
		worldExporterFactory.create(configuration).export();
	}

	public boolean isFullyLoaded() {
		return fragmentManager.getLoadingQueueSize() == 0;
	}

}
