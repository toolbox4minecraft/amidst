package amidst.gui.voronoi;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Draws a Voronoi diagram, currently using the brute-force method
 */
public class VoronoiGraph {
	
	private BufferedImage graph;
	private int[] bufferArray;
	private int width;
	private int height;
	
	/** Create the graph BufferedImage if it doesn't already exist at the correct size */
	private void createBufferedImage() {
		if (graph == null || graph.getWidth() != width || graph.getHeight() != height) {
			graph = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			bufferArray = new int[(200000 / width) * width];
		}
	}

	public BufferedImage render(List<GraphNode> nodes, int x_min, int x_max, int y_min, int y_max) {
		
		createBufferedImage();
		
		GraphNode closestNode;
		float closestDist;
    	float xScale = (x_max - x_min) / (float)width;
    	float yScale = (y_max - y_min) / (float)height;

    	int bufferArrayY = 0;
		int bufferArrayLines = bufferArray.length / width; // number of rasterlines we can write to bufferArray before it needs to be written to the BufferedImage
		int bufferArrayIndex = 0;

		// Convert to array for hopefully faster indexed access
		GraphNode[] nodeArray = nodes.toArray(new GraphNode[nodes.size()]);
		int nodeCount = nodeArray.length;
		
		for (int y = 0; y < height; y++) {
			float valueAtY = (y * yScale) + y_min;

			if (y - bufferArrayY >= bufferArrayLines) {
				// flush bufferArray into the BufferedImage.
				// (I'm hoping Java has this optimized, if not, there's no need for rgbArray)
				graph.setRGB(0, bufferArrayY, width, bufferArrayLines, bufferArray, 0, width);				
				bufferArrayY = y;
				bufferArrayIndex = 0;
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
				bufferArray[bufferArrayIndex++] = (closestNode == null) ? 0x00000000 : closestNode.argb;
			}
		}
		if (bufferArrayIndex > 0) {
			// flush the rest of bufferArray into the BufferedImage.
			graph.setRGB(0, bufferArrayY, width, bufferArrayIndex / width, bufferArray, 0, width);				
		}
		
		return graph;
	}
	
	public VoronoiGraph(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
