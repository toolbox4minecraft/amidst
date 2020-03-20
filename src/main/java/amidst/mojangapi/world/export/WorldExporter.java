package amidst.mojangapi.world.export;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import javax.media.jai.JAI;
import javax.media.jai.TiledImage;

import com.sun.media.jai.codec.TIFFEncodeParam;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.ProgressReporter;

@NotThreadSafe
public class WorldExporter {
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
	private final Consumer<String> progressListener;

	public WorldExporter(
			World world,
			WorldExporterConfiguration configuration,
			Consumer<String> progressListener) {
		this.world = world;
		this.configuration = configuration;
		this.progressListener = progressListener;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void export() throws WorldExportException {
		exporterExecutor.<String> run(this::doExport, progressListener, this::onFinished);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doExport(ProgressReporter<String> progressReporter) {
		int factor = configuration.isQuarterResolution() ? 4 : 1;
		
		TIFFEncodeParam tep = new TIFFEncodeParam();
		tep.setTileSize(Fragment.SIZE, Fragment.SIZE);
		tep.setWriteTiled(true);
		tep.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE);
		tep.setDeflateLevel(5);
		
		int x = (int) configuration.getTopLeftCoord().getX();
		int y = (int) configuration.getTopLeftCoord().getY();
		int width = (int) configuration.getBottomRightCoord().getX() - x;
		int height = (int) configuration.getBottomRightCoord().getY() - y;
		
		int[] bitmasks = {
			0xFF0000,
			0x00FF00,
			0x0000FF
		};
		
		BiomeProfileSelection biomeColors = configuration.getBiomeProfileSelection();
		TiledImage tiledImage = new TiledImage(0, 0, width / factor, height / factor, 0, 0,
					new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, 256, 256, bitmasks),
					new DirectColorModel(32, bitmasks[0], bitmasks[1], bitmasks[2])
				);
		
		int tileMaxProgress = tiledImage.getNumXTiles() * tiledImage.getNumYTiles();
		int imageMaxProgress = (int) Math.ceil(tiledImage.getNumXTiles() * tiledImage.getNumYTiles() * .05);
		progressReporter.report("min,0");
		progressReporter.report("max," + (tileMaxProgress + imageMaxProgress));
		progressReporter.report("progress,0");
		
		short[][] dataArray;
		int tilesProcessed = 0;
		for(int tx = 0; tx < tiledImage.getNumXTiles(); tx++) {
			for(int ty = 0; ty < tiledImage.getNumYTiles(); ty++) {
				Rectangle r = tiledImage.getTileRect(tx, ty);
				r.setLocation(tiledImage.tileXToX(tx), tiledImage.tileYToY(ty));
				dataArray = new short[r.width][r.height];
				world.getBiomeDataOracle().populateArray(new CoordinatesInWorld((long) x + r.x * factor, (long) y + r.y * factor), dataArray, configuration.isQuarterResolution());
			
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
				progressReporter.report("progress," + ++tilesProcessed);
			}
			JAI.getDefaultInstance().getTileCache().memoryControl();
		}
		dataArray = null;
		
		try {
			JAI.create("filestore", tiledImage, configuration.getImageFile().getCanonicalPath(), "TIFF", tep);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		tiledImage.dispose();
		// We nullify these objects and call the garbage collector so that JAI releases its lock on the newly created file.
		// This is sadly the best way to do this, and is even reccomended by JAI when we want to unlock a file.
		tep = null;
		tiledImage = null;
		System.gc();
		progressReporter.report("progress," + (tileMaxProgress + imageMaxProgress));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void onFinished(Exception e) {
		AmidstLogger.warn(e);
	}
	
	@CalledByAny
	public static boolean isExporterRunning() {
		if(pool.getActiveCount() > 0) {
				return true;
			}
		return false;
	}
	
}
