package amidst.gui.export;

import static amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType.MAX;
import static amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType.MIN;
import static amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType.PROGRESS;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.ProgressReporter;

@ThreadSafe
public class BiomeExporter {
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
						"Open Save Game ...",
						"Switch Profile ...",
						"Select Look & Feel"
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
							"Open Save Game ...",
							"Switch Profile ...",
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

			int resolutionFactor = Resolution.from(configuration.isQuarterResolution()).getStep();

			int x = (int) configuration.getTopLeftCoord().getX();
			int y = (int) configuration.getTopLeftCoord().getY();
			int width = ((int) configuration.getBottomRightCoord().getX() - x) / resolutionFactor;
			int height = ((int) configuration.getBottomRightCoord().getY() - y) / resolutionFactor;

			progressReporter.report(entry(MAX, height + 1));

			RenderedImage img = new CustomRenderedImage(x, y, width, height,
					configuration.getBiomeProfileSelection(), biomeDataOracle, configuration.isQuarterResolution(),
					progressReporter);

			try {
				ImageIO.write(img, "png", configuration.getImagePath().toAbsolutePath().toFile());
			} catch (Exception e) {
	    		AmidstLogger.error(e, "An error occured while trying to export the image");
	    		AmidstMessageBox.displayError("Export Biome Images", e, "An error occured while trying to export the image");
			} finally {
				System.gc();
				progressReporter.report(entry(PROGRESS, height + 1));
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

	private static class CustomRenderedImage implements RenderedImage {

		private final static int[] BITMASKS = {
			0xFF0000,
			0x00FF00,
			0x0000FF
		};
		private final static int DEFAULT_BATCH_HEIGHT = 16;

		private final int worldX, worldY, width, height;
		private final ColorModel colorModel;
		private final SampleModel sampleModel;
		private final BiomeProfileSelection biomeProfileSelection;
		private final BiomeDataOracle biomeDataOracle;
		private final boolean useQuarterResolution;
		private final int resolutionFactor;
		private final ProgressReporter<Entry<ProgressEntryType, Integer>> progressReporter;

		// The PNG renderer asks for data scanline by scanline, but the BiomeDataOracle has better
		// performance when computing biome data for "thicker" regions.
		// So we calculate several lines at once and cache the pixels in this array.
		private int[] cachedPixels;
		private int cachedY;
		private int cachedHeight;

		public CustomRenderedImage(
				int worldX,
				int worldY,
				int width,
				int height,
				BiomeProfileSelection biomeProfileSelection,
				BiomeDataOracle biomeDataOracle,
				boolean useQuarterResolution,
				ProgressReporter<Entry<ProgressEntryType, Integer>> progressReporter) {
			this.useQuarterResolution = useQuarterResolution;
			this.resolutionFactor = Resolution.from(useQuarterResolution).getStep();
			this.worldX = worldX;
			this.worldY = worldY;
			this.width = width;
			this.height = height;
			this.colorModel = new DirectColorModel(24, BITMASKS[0], BITMASKS[1], BITMASKS[2]);
			this.sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, width, height, BITMASKS);
			this.biomeProfileSelection = biomeProfileSelection;
			this.biomeDataOracle = biomeDataOracle;
			this.progressReporter = progressReporter;
			this.cachedHeight = DEFAULT_BATCH_HEIGHT;
		}

		@Override
		public ColorModel getColorModel() {
			return colorModel;
		}

		@Override
		public SampleModel getSampleModel() {
			return sampleModel;
		}

		@Override
		public int getMinX() {
			return 0;
		}

		@Override
		public int getMinY() {
			return 0;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public Raster getData(Rectangle rect) {
			if (rect.x != getMinX() || rect.width != getWidth() || rect.height != 1) {
				throw new IllegalArgumentException("this RenderedImage only supports producing Rasters for horizontal slices");
			}

			if (cachedPixels == null) {
				cachedPixels = new int[Math.multiplyExact(getWidth(), cachedHeight)];
				cachedY = Integer.MIN_VALUE;
			}

			// New batch, calculate the new pixels
			if (rect.y < cachedY || rect.y >= cachedY + cachedHeight) {
				CoordinatesInWorld corner = CoordinatesInWorld.from((long) worldX + rect.x * resolutionFactor, (long) worldY + rect.y * resolutionFactor);
				biomeDataOracle.getBiomeData(corner, getWidth(), cachedHeight, useQuarterResolution, data -> {
					for (int i = 0; i < cachedPixels.length; i++) {
						cachedPixels[i] = biomeProfileSelection.getBiomeColorOrUnknown(data[i]).getRGB();
					}
				});
				cachedY = rect.y;
			}

			int bufSize = Math.multiplyExact(getWidth(), rect.height);
			int bufOffset = Math.multiplyExact(getWidth(), rect.y - cachedY);
			DataBufferInt buffer = new DataBufferInt(cachedPixels, bufSize, bufOffset);
			Raster r = Raster.createPackedRaster(buffer, getWidth(), rect.height, getWidth(), BITMASKS, new Point(rect.x, rect.y));
			progressReporter.report(entry(PROGRESS, rect.y));

			return r;
		}

		@Override
		public Raster getData() {
			throw new UnsupportedOperationException();
		}

		@Override
		public WritableRaster copyData(WritableRaster arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getMinTileX() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getMinTileY() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getNumXTiles() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getNumYTiles() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object getProperty(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String[] getPropertyNames() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Vector<RenderedImage> getSources() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Raster getTile(int x, int y) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getTileGridXOffset() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getTileGridYOffset() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getTileHeight() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getTileWidth() {
			throw new UnsupportedOperationException();
		}
	}

}
