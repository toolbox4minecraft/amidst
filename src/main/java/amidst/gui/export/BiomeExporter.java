package amidst.gui.export;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.media.jai.JAI;
import javax.swing.SwingUtilities;

import org.jaitools.tilecache.DiskMemTileCache;
import org.jaitools.tiledimage.DiskMemImage;

import com.sun.media.jai.codec.TIFFEncodeParam;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.ProgressReporter;

import static amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType.*;

@ThreadSafe
public class BiomeExporter {
	private static final int TILE_SIZE = Fragment.SIZE;
	private static final int TILES_BETWEEN_FLUSHES = 30;
	private static final DiskMemTileCache TILE_CACHE = new DiskMemTileCache();
	private static final AtomicLong ACTIVE_THREADS = new AtomicLong(0);
	
	private final WorkerExecutor workerExecutor;

	public BiomeExporter(WorkerExecutor workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void export(BiomeDataOracle biomeDataOracle,
			BiomeExporterConfiguration configuration,
			Consumer<Entry<ProgressEntryType, Integer>> progressListener,
			AmidstMenu menuBar) {
		
		workerExecutor.<Entry<ProgressEntryType, Integer>> run((p) -> {
				SwingUtilities.invokeLater(() -> {
					menuBar.setMenuItemsEnabled(new String[] {
						"New From Seed ...",     
						"New From Random Seed",
						"Search for Random Seed",
						"Open Save Game ...",
						"Switch Profile ...",
						"Exit",
						"Select Look & Feel",
						"Biome Profile"
					}, false);
				});
				ACTIVE_THREADS.incrementAndGet();
				try {
					this.doExport(biomeDataOracle, configuration, p);
				} finally {
					ACTIVE_THREADS.decrementAndGet();
					SwingUtilities.invokeLater(() -> {
						menuBar.setMenuItemsEnabled(new String[] {
							"Export Biomes to Image ...",
							"New From Seed ...",     
							"New From Random Seed",
							"Search for Random Seed",
							"Open Save Game ...",
							"Switch Profile ...",
							"Exit",
							"Select Look & Feel",
							"Biome Profile"
						}, true);
					});
				}
			}, progressListener, this::onException);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doExport(BiomeDataOracle biomeDataOracle,
			BiomeExporterConfiguration configuration,
			ProgressReporter<Entry<ProgressEntryType, Integer>> progressReporter) {
		
    	try {
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
						TILE_CACHE.memoryControl();
					}
					
					Rectangle r = tiledImage.getTileRect(tx, ty);
					r.setLocation(tiledImage.tileXToX(tx), tiledImage.tileYToY(ty));
					dataArray = new short[r.width][r.height];
					biomeDataOracle.populateArray(new CoordinatesInWorld((long) x + r.x * factor, (long) y + r.y * factor), dataArray, configuration.isQuarterResolution());
				
					try {
						for (int i = 0; i < r.width; i++) {
							for (int j = 0; j < r.height; j++) {
								tiledImage.setSample(r.x + i, r.y + j, 0, biomeColors.getBiomeColor(dataArray[i][j]).getR());
								tiledImage.setSample(r.x + i, r.y + j, 1, biomeColors.getBiomeColor(dataArray[i][j]).getG());
								tiledImage.setSample(r.x + i, r.y + j, 2, biomeColors.getBiomeColor(dataArray[i][j]).getB());
							}
						}
					} catch (UnknownBiomeIdException e) {
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
			
			try {
				JAI.create("filestore", tiledImage, configuration.getImagePath().toAbsolutePath().toString(), "TIFF", tep);
	    	} catch (Exception e) {
	    		AmidstLogger.error(e, "An error occured while trying to export the image");
	    		AmidstMessageBox.displayError("Export Biome Images", e, "An error occured while trying to export the image");
	    	} finally {
	    		TILE_CACHE.flush();
				tiledImage.dispose();
				// We nullify these objects and call the garbage collector so that JAI releases its lock on the newly created file.
				// This is sadly the best way to do this, and it is even recommended by JAI for when we want to unlock the file.
	    		tep = null;
				tiledImage = null;
				System.gc();
				progressReporter.report(entry(PROGRESS, tileMaxProgress + imageMaxProgress));
			}
    	} finally {
    		// delete file if we made it earlier and didn't write to it or closed out early
    		try {
				if (Files.size(configuration.getImagePath()) == 0) {
					Files.delete(configuration.getImagePath());
				}
			} catch (IOException e) {
				AmidstLogger.error("Error accessing blank file: " + configuration.getImagePath().toAbsolutePath().toString());
			}
    	}
	}
	
	private static <K, V> Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(key, value);
	}

	private void onException(Exception e) {
		AmidstLogger.error(e);
	}
	
	public static boolean isExporterRunning() {
		if(ACTIVE_THREADS.get() > 0) {
				return true;
			}
		return false;
	}
	
	static {
		DiskMemImage.setCommonTileCache(TILE_CACHE);
		JAI.getDefaultInstance().setTileCache(TILE_CACHE);
	}
	
}
