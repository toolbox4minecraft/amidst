package amidst.gui.voronoi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.vecmath.Point2d;

import amidst.minetest.world.mapgen.IHistogram2D;

public class FrequencyGraph {

	private int width;
	private int height;
	private BufferedImage graph;

	private IHistogram2D last_histogram2d;
	int last_x_min, last_x_max, last_y_min, last_y_max;

	/**
	 * Returns an image of the frequency distribution graph given by histogram2d
	 * @param histogram2d
	 * @param x_min - start of the x axis for the graph
	 * @param x_max - end of the x axis for the graph
	 * @param y_min - start of the y axis for the graph
	 * @param y_max - end of the y axis for the graph
	 * @param step - the increment per pixel, i.e. a step of 0.25 means 4 pixels to increment along an axis by 1
	 * @return an image of the frequency data
	 */
	public BufferedImage render(IHistogram2D histogram2d, int x_min, int x_max, int y_min, int y_max) {

		int xRange = 1 + x_max - x_min; // inclusive range
		int yRange = 1 + y_max - y_min; // inclusive range
    	float xScale = xRange / (float)width;
    	float yScale = yRange / (float)height;

		if (graph == null || graph.getWidth() != width || graph.getHeight() != height) {
			graph = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		} else {
			if (histogram2d.equals(last_histogram2d) &&
				x_min == last_x_min && x_max == last_x_max &&
				y_min == last_y_min && y_max == last_y_max) {
				// It's the same as last time
				return graph;
			}
		}
		last_histogram2d = histogram2d;
		last_x_min = x_min;
		last_x_max = x_max;
		last_y_min = y_min;
		last_y_max = y_max;


		// No math here, these minDist values for band thicknesses were set by trial/eyeballing
		double minDistRed  = 0.0000017;
		double minDistBlue = 0.000001;
		double minDistBase = 0.00000001;

		// the 0.01 percentile is the lowest the ClimateHistogram lookup tables store values for.
		double bottomFrequency = histogram2d.frequencyAtPercentile(0.01);

		Point2d mean = histogram2d.getSampleMean();
    	int xMean = (int)Math.round((mean.x - x_min) / xScale);
    	int yMean = (int)Math.round((mean.y - y_min) / yScale);


		for (int y = 0; y < height; y++) {
			float histogramY = (y * yScale) + y_min;

			for (int x = 0; x < width; x++) {
				float histogramX = (x * xScale) + x_min;

				// set transparent
				graph.setRGB(x, y, 0x00000000);

				double distance;
				double frequency = histogram2d.frequencyOfOccurance(histogramX, histogramY);

				if (frequency == 0) {
					// No values were ever seen in this location, color it gray to indicate
					// the climate doesn't reach these values.
					graph.setRGB(x, y, 0xFF707070);

				} else if (frequency < (bottomFrequency + minDistBase)) {
					// It is exceeding rare for the climate to ever reach these values.
					// Draw a red quartile boundary line here.
					if (frequency > (bottomFrequency - minDistBase)) {

						// trying to make a dotted circle while avoiding sin() or cos() ;)
						int absX = (int)Math.abs(x - xMean);
						int absY = (int)Math.abs(y - yMean);
						int travel = Math.abs((absX < absY) ? absX - (absY / 2) : absY - (absX / 2));

						if(((travel / 8) & 1) > 0) {
							distance = Math.abs(frequency - bottomFrequency);
							graph.setRGB(x, y, 0x00FF3030 | (255 - (int)Math.round((255 * distance) / minDistBase)) << 24);
						}
					}
				} else {

					for(int percentile = 10; percentile <= 90; percentile += 5) {

						if ((percentile % 25) == 0) {
							distance = Math.abs(frequency - histogram2d.frequencyAtPercentile(percentile));
							if (distance <= minDistRed) {
								// Draw a quartile boundary line here.
								graph.setRGB(x, y, 0x00FF3030 | (255 - (int)Math.round((255 * distance) / minDistRed)) << 24);
							}
						} else if ((percentile % 10) == 0) {
							distance = Math.abs(frequency - histogram2d.frequencyAtPercentile(percentile));
							if (distance <= minDistBlue) {
								// Draw a 10-percentile boundary line here.
								graph.setRGB(x, y, 0x003050FF | (255 - (int)Math.round((255 * distance) / minDistBlue)) << 24);
							}
						}
					}
				}
			}
		}

		// Draw the mean, as the frequency data will be almost flat there, and random noise
		// may prevent that looking point-like.
		Graphics2D g2d = graph.createGraphics();
		g2d.setColor(Color.RED);
		g2d.fillOval(xMean - 4, yMean - 4, 9, 9);

		return graph;
	}

	public FrequencyGraph(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
