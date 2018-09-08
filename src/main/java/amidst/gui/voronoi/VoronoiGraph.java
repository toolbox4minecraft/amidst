package amidst.gui.voronoi;

import java.awt.image.BufferedImage;
import java.util.List;

import amidst.minetest.world.mapgen.IHistogram2D;

/**
 * Draws a Voronoi diagram, currently using the brute-force method.
 *
 */
public class VoronoiGraph {

	private BufferedImage graph;
	private int[] bufferArray;
	private int width;
	private int height;
	private IHistogram2D climateHistogram;

	/** Create the graph BufferedImage if it doesn't already exist at the correct size */
	private void createBufferedImage() {
		if (graph == null || graph.getWidth() != width || graph.getHeight() != height) {
			graph = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			bufferArray = new int[(200000 / width) * width];
		}
	}

	public void setHistogram(IHistogram2D histogram) {
		climateHistogram = histogram;
	}

	/**
	 * Returns an image of the Vonoroi graph, and if a climateHistogram has been provided then
	 * it calculates and sets the occurrenceFrequency field in each of the GraphNodes.
	 * @param nodes
	 * @param x_min - start of the x axis for the graph
	 * @param x_max - end of the x axis for the graph
	 * @param y_min - start of the y axis for the graph
	 * @param y_max - end of the y axis for the graph
	 * @return an image of the Vonoroi graph
	 */
	public BufferedImage render(List<GraphNode> nodes, int x_min, int x_max, int y_min, int y_max) {

		createBufferedImage();

		GraphNode closestNode;
		float closestDist;
    	float xScale = (x_max - x_min) / (float)width;
    	float yScale = (y_max - y_min) / (float)height;

    	int bufferArrayY = 0;
		int bufferArrayLines = bufferArray.length / width; // number of rasterlines we can write to bufferArray before it needs to be written to the BufferedImage
		int bufferArrayIndex = 0;

		int occurrenceHistogramY = Integer.MIN_VALUE;
		double[] occurrenceHistogram = new double[1 + x_max - x_min];

		// Convert the List to an array for hopefully faster indexed access
		GraphNode[] nodeArray = nodes.toArray(new GraphNode[nodes.size()]);
		int nodeCount = nodeArray.length;

		// Init/zero the occurrenceFrequency data field in each node
		for (short i = 0; i < nodeCount; i++) nodeArray[i].occurrenceFrequency = 0;

		for (int y = 0; y < height; y++) {
			float valueAtY = (y * yScale) + y_min;

			if (y - bufferArrayY >= bufferArrayLines) {
				// flush bufferArray into the BufferedImage.
				// (I'm hoping Java has this optimized, if not, there's no need for rgbArray)
				graph.setRGB(0, bufferArrayY, width, bufferArrayLines, bufferArray, 0, width);
				bufferArrayY = y;
				bufferArrayIndex = 0;
			}

			// Populate occuranceHistogram for this value of y
			if (Math.round(y * yScale) != occurrenceHistogramY && climateHistogram != null) {
				int yIndex = Math.round(y * yScale);
				for (int i = x_max - x_min; i >= 0; i--) occurrenceHistogram[i] = climateHistogram.frequencyOfOccurance(i + x_min, yIndex + y_min);
				occurrenceHistogramY = yIndex;
			}

			for (int x = 0; x < width; x++) {
				//float heat = (x * xScale) + axis_min;
				float valueAtX = (x * xScale) + x_min;

				closestDist = Float.MAX_VALUE;
				closestNode = null;

				for (short i = 0; i < nodeCount; i++) {
					GraphNode node = nodeArray[i];
					float xDist = valueAtX - node.x;
					float yDist = valueAtY - node.y;
					float dist = (xDist * xDist) + (yDist * yDist);

					if (dist < closestDist) {
						closestDist = dist;
						closestNode = node;
					}
				}
				if (closestNode != null) {
					bufferArray[bufferArrayIndex++] = closestNode.argb;
					closestNode.occurrenceFrequency += occurrenceHistogram[Math.round(x * xScale)];
				} else {
					bufferArray[bufferArrayIndex++] = 0x00000000;
				}
			}
		}
		if (bufferArrayIndex > 0) {
			// flush the rest of bufferArray into the BufferedImage.
			graph.setRGB(0, bufferArrayY, width, bufferArrayIndex / width, bufferArray, 0, width);
		}

		// Correct occurrenceFrequency for the oversampling caused by difference between
		// size of BufferedImage vs histogram resolution
		for (short i = 0; i < nodeCount; i++) {
			nodeArray[i].occurrenceFrequency = nodeArray[i].occurrenceFrequency * xScale * yScale;
		}

		return graph;
	}

	public VoronoiGraph(int width, int height, IHistogram2D climate_histogram) {
		this.width = width;
		this.height = height;
		this.climateHistogram = climate_histogram;
	}
}
