package amidst.mojangapi.world.export;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import javax.media.jai.JAI;

import org.jaitools.tilecache.DiskMemTileCache;
import org.jaitools.tiledimage.DiskMemImage;

import com.sun.media.jai.codec.TIFFEncodeParam;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.ProgressReporter;

import static amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType.*;

@ThreadSafe
public class WorldExporter {
	private static final int TILE_SIZE = Fragment.SIZE;
	private static final int TILES_BETWEEN_FLUSHES = 30;
	private static final DiskMemTileCache tileCache = new DiskMemTileCache();
	private static final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool(new ThreadFactory() {
		private int threadNo;
		
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("WorldExporterThread-" + threadNo++);
			return t;
		}
	});
	private static final WorkerExecutor exporterExecutor = new WorkerExecutor(pool);
	
	private final World world;
	private final WorldExporterConfiguration configuration;
	private final Consumer<Entry<ProgressEntryType, Integer>> progressListener;

	public WorldExporter(
			World world,
			WorldExporterConfiguration configuration,
			Consumer<Entry<ProgressEntryType, Integer>> progressListener) {
		this.world = world;
		this.configuration = configuration;
		this.progressListener = progressListener;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void export() throws WorldExportException {
		exporterExecutor.<Entry<ProgressEntryType, Integer>> run(this::doExport, progressListener, this::onFinished);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doExport(ProgressReporter<Entry<ProgressEntryType, Integer>> progressReporter) {
		progressReporter.report(entry(MIN, 0));
		progressReporter.report(entry(PROGRESS, 0));
		int factor = configuration.isQuarterResolution() ? 4 : 1;
		
		int x = (int) configuration.getTopLeftCoord().getX();
		int y = (int) configuration.getTopLeftCoord().getY();
		int width = (int) configuration.getBottomRightCoord().getX() - x;
		int height = (int) configuration.getBottomRightCoord().getY() - y;
		
		int[] bitmasks = {
			0xFF0000,
			0x00FF00,
			0x0000FF
		};
		
		DiskMemImage tiledImage = new DiskMemImage(0, 0, width / factor, height / factor, 0, 0,
					new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, TILE_SIZE, TILE_SIZE, bitmasks),
					new DirectColorModel(32, bitmasks[0], bitmasks[1], bitmasks[2])
				);
		tiledImage.setUseCommonCache(true);
		
		int tileMaxProgress = tiledImage.getNumXTiles() * tiledImage.getNumYTiles();
		int imageMaxProgress = (int) Math.ceil(tiledImage.getNumXTiles() * tiledImage.getNumYTiles() * .05);
		progressReporter.report(entry(MAX, tileMaxProgress + imageMaxProgress));
		
		BiomeProfileSelection biomeColors = configuration.getBiomeProfileSelection();
		
		short[][] dataArray;
		int tilesProcessed = 0;
		for (int tx = 0; tx < tiledImage.getNumXTiles(); tx++) {
			for (int ty = 0; ty < tiledImage.getNumYTiles(); ty++) {
				if (tilesProcessed % TILES_BETWEEN_FLUSHES == 0) {
					tileCache.memoryControl();
				}
				
				Rectangle r = tiledImage.getTileRect(tx, ty);
				r.setLocation(tiledImage.tileXToX(tx), tiledImage.tileYToY(ty));
				dataArray = new short[r.width][r.height];
				world.getBiomeDataOracle().populateArray(new CoordinatesInWorld((long) x + r.x * factor, (long) y + r.y * factor), dataArray, configuration.isQuarterResolution());
			
				try {
					for (int i = 0; i < r.width; i++) {
						for (int j = 0; j < r.height; j++) {
							tiledImage.setSample(r.x + i, r.y + j, 0, biomeColors.getBiomeColor(dataArray[i][j]).getR());
							tiledImage.setSample(r.x + i, r.y + j, 1, biomeColors.getBiomeColor(dataArray[i][j]).getG());
							tiledImage.setSample(r.x + i, r.y + j, 2, biomeColors.getBiomeColor(dataArray[i][j]).getB());
						}
					}
				} catch (UnknownBiomeIndexException e) {
					e.printStackTrace();
				}
				
				progressReporter.report(entry(PROGRESS, ++tilesProcessed));
			}
		}
		dataArray = null;
		
		TIFFEncodeParam tep = new TIFFEncodeParam();
		tep.setTileSize(TILE_SIZE, TILE_SIZE);
		tep.setWriteTiled(true);
		tep.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE);
		tep.setDeflateLevel(5);
		
		JAI.create("filestore", tiledImage, configuration.getImagePath().toAbsolutePath().toString(), "TIFF", tep);
		tileCache.flush();
		tiledImage.dispose();
		// We nullify these objects and call the garbage collector so that JAI releases its lock on the newly created file.
		// This is sadly the best way to do this, and is even recommended by JAI when we want to unlock a file.
		tep = null;
		tiledImage = null;
		System.gc();
		progressReporter.report(entry(PROGRESS, tileMaxProgress + imageMaxProgress));
	}
	
	private static <K, V> Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(key, value);
	}

	private void onFinished(Exception e) {
		AmidstLogger.warn(e);
	}
	
	public static boolean isExporterRunning() {
		if(pool.getActiveCount() > 0) {
				return true;
			}
		return false;
	}
	
	static {
		DiskMemImage.setCommonTileCache(tileCache);
		JAI.getDefaultInstance().setTileCache(tileCache);
	}
	
}
