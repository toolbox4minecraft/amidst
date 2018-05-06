package amidst.gui.voronoi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.Fragment;
import amidst.gameengineabstraction.world.biome.IBiome;
import amidst.logging.AmidstLogger;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.MinetestBiomeProfileImpl;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

public class VoronoiPanel extends JPanel {

	public static final int FLAG_SHOWLABELS       = 0x01;
	public static final int FLAG_SHOWAXIS         = 0x02;
	public static final int FLAG_SHOWNODES        = 0x04;
	public static final int FLAG_SHOWDISTRIBUTION = 0x08;

	public static final boolean GRAPHICS_DEBUG = false;

	private static final float AXIS_WIDTH           = 0.5f;
	private static final int   TICKMARK_WIDTH_SMALL = 3;
	private static final int   TICKMARK_WIDTH_LARGE = 6;
	private static final int   TICKMARK_LABEL_SPACE = 1;
	private static final int   NODE_RADIUS          = 0;
	private static final int   NODE_LABEL_SPACE     = 0;
	private static final int   NODE_LABEL_FONTSIZE  = 3;

	private static final long serialVersionUID = 1L;

	private static Stroke          stroke_capButt   = new BasicStroke(AXIS_WIDTH, BasicStroke.CAP_BUTT,   BasicStroke.JOIN_ROUND);
	private static Stroke          stroke_capSquare = new BasicStroke(AXIS_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	private static AffineTransform noTransform      = new AffineTransform();

	public int axis_min         = -40;
	public int axis_max         = 140;
	public int graph_resolution = 1000;

	private MinetestBiome[] nodes;
	private int renderFlags;
	private BufferedImage graph;
	private int[] rgbArray;

	@Override
	@CalledOnlyBy(AmidstThread.EDT)
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        createBufferedImage(graph_resolution);

        this.setBorder(null);
        Rectangle panelBounds = getBounds();
        if (panelBounds.width > 0 && panelBounds.height > 0) {

        	Graphics2D g2d = (Graphics2D) g.create();
        	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        	g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        	if (GRAPHICS_DEBUG) {
	        	g2d.setColor(Color.RED);
	        	g2d.fillRect(0, 0, panelBounds.width, panelBounds.height);
        	}

        	int graphSize;
        	int desiredAxisLength = axis_max - axis_min;
            AffineTransform graphTransform = new AffineTransform(g2d.getTransform());
            // set up a square render area of (graphSize by graphSize) that's centered
            if (panelBounds.width > panelBounds.height) {
            	graphSize = panelBounds.height;
            	graphTransform.translate((panelBounds.width - graphSize) / 2.0, 0);
            } else {
            	graphSize = panelBounds.width;
            	graphTransform.translate(0, (panelBounds.height - graphSize) / 2.0);
            }
            // Flip the axis to y points up, and scale desiredAxisLength to full the centered render area
            graphTransform.scale(graphSize / (double)desiredAxisLength, -graphSize / (double)desiredAxisLength);
            graphTransform.translate(-axis_min, -axis_max);
            g2d.setTransform(graphTransform);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, Math.round(NODE_LABEL_FONTSIZE * graphSize / (float)desiredAxisLength)));

            // set graph background to white
        	g2d.setColor(Color.WHITE);
        	g2d.fillRect(axis_min, axis_min, axis_max - axis_min, axis_max - axis_min);

        	//drawVoronoi(g2d);
        	drawVoronoiGraph(rgbArray, graph_resolution, graph_resolution);
        	graph.setRGB(0, 0, graph_resolution, graph_resolution, rgbArray, 0, graph_resolution);
        	g2d.drawImage(graph, axis_min, axis_min, desiredAxisLength, desiredAxisLength, Color.WHITE, null);

        	// draw nodes
        	drawNodesOrNodeLabels(g2d);

        	// draw axis
        	if ((renderFlags & FLAG_SHOWAXIS) > 0) drawAxes(g2d);
        }
    }

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawNodesOrNodeLabels(Graphics2D g2d) {

		AffineTransform currentTransform = g2d.getTransform();
		FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
    	boolean showNodes = (renderFlags & FLAG_SHOWNODES) > 0;
    	boolean showLabels = (renderFlags & FLAG_SHOWLABELS) > 0;

    	for (short i = 0; i < nodes.length; i++) {
			MinetestBiome biome = nodes[i];
			int x = Math.round(biome.heat_point);
			int y = Math.round(biome.humidity_point);

        	if (perceivedBrightness(biome.getDefaultColor().getColor()) < 120) {
        		g2d.setColor(Color.WHITE);
        	} else {
        		g2d.setColor(Color.BLACK);
        	}

        	if (showNodes) {
        		g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, 1 + NODE_RADIUS * 2, 1 + NODE_RADIUS * 2);
        	}

        	if (showLabels) {
		    	Point2D fontPos            = new Point(x, y - (showNodes ? NODE_LABEL_SPACE : 0));
		    	Point2D fontPosTransformed = currentTransform.transform(fontPos, null);

		        g2d.setTransform(noTransform);
		    	g2d.drawString(
					biome.getName(),
					(float)fontPosTransformed.getX() - (metrics.stringWidth(biome.getName()) / 2),
					(float)fontPosTransformed.getY() + (metrics.getAscent() / (showNodes ? 1 : -4))
				);
		        g2d.setTransform(currentTransform);
        	}
    	}
    }

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawAxes(Graphics2D g2d) {

		AffineTransform currentTransform = g2d.getTransform();
		FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());

    	g2d.setStroke(stroke_capSquare);
    	g2d.setColor(Color.BLACK);
    	g2d.drawLine(axis_min, 0, axis_max, 0);
    	g2d.drawLine(0, axis_min, 0, axis_max);

    	g2d.setStroke(stroke_capButt);
    	for (int i = axis_min; i <= axis_max; i++) {
    		if (i % 10 == 0 && i != 0) {
    			if (i % 100 == 0) {
    				g2d.drawLine(0, i, -TICKMARK_WIDTH_LARGE, i);
    				g2d.drawLine(i, 0, i, -TICKMARK_WIDTH_LARGE);

    	        	Point2D fontPosTemp   = new Point(-TICKMARK_WIDTH_LARGE - TICKMARK_LABEL_SPACE, i);
    	        	Point2D fontPosHumidy = new Point(i, -TICKMARK_WIDTH_LARGE - TICKMARK_LABEL_SPACE);

    	            g2d.setTransform(noTransform);
    	        	Point2D fontPosTransformed = currentTransform.transform(fontPosTemp, null);
    	        	g2d.drawString(
    	        		String.valueOf(i),
            			(float)fontPosTransformed.getX() - metrics.stringWidth(String.valueOf(i)),
            			(float)fontPosTransformed.getY() + metrics.getAscent() / 3
            		);
    	        	fontPosTransformed = currentTransform.transform(fontPosHumidy, null);
    	        	g2d.drawString(
    	        			String.valueOf(i),
                			(float)fontPosTransformed.getX() - metrics.stringWidth(String.valueOf(i)) / 2,
                			(float)fontPosTransformed.getY() + metrics.getAscent()
                		);
    	            g2d.setTransform(currentTransform);
    			} else {
    				g2d.drawLine(0, i, -TICKMARK_WIDTH_SMALL, i);
    				g2d.drawLine(i, 0, i, -TICKMARK_WIDTH_SMALL);
    			}
    		}
    	}

    	if ((renderFlags & FLAG_SHOWLABELS) > 0) {
			// label the axes
	        g2d.setFont(new Font("Serif", Font.PLAIN, (int)Math.round(NODE_LABEL_FONTSIZE * 1.7f * currentTransform.getScaleX())));
			metrics = g2d.getFontMetrics(g2d.getFont());

        	Point2D fontPosHumidy = new Point(-TICKMARK_WIDTH_LARGE * 2, axis_max / 2);
        	Point2D fontPosTemp   = new Point(axis_max / 2, -TICKMARK_WIDTH_LARGE * 2);

            g2d.setTransform(noTransform);
        	Point2D fontPosTransformed = currentTransform.transform(fontPosTemp, null);
        	String value = "Temperature";
        	g2d.drawString(
        		value,
    			(float)fontPosTransformed.getX() - metrics.stringWidth(value),
    			(float)fontPosTransformed.getY() + metrics.getAscent() / 4
    		);

        	fontPosTransformed = currentTransform.transform(fontPosHumidy, null);
        	value = "Humidity";
        	drawRotatedText(
       			g2d,
    			(float)fontPosTransformed.getX() + metrics.getAscent() / 4,
    			(float)fontPosTransformed.getY() + metrics.stringWidth(value),
    			270,
    			value
    		);

        	g2d.setTransform(currentTransform);
    	}
	}

	public static void drawRotatedText(Graphics2D g2d, double x, double y, int angle, String text)
	{
	    g2d.translate((float)x,(float)y);
	    g2d.rotate(Math.toRadians(angle));
	    g2d.drawString(text,0,0);
	    g2d.rotate(-Math.toRadians(angle));
	    g2d.translate(-(float)x,-(float)y);
	}


	/** returns the perceived brightness of col, between 0 (dark) and 255 (bight) */
	@CalledByAny
	public double perceivedBrightness(Color col)
	{
		return Math.sqrt(
			.299 * Math.pow(col.getRed(),   2) +
			.587 * Math.pow(col.getGreen(), 2) +
			.114 * Math.pow(col.getBlue(),  2)
		);
	}

	/** Create the graph BufferedImage if it doesn't already exist at the correct size */
	private void createBufferedImage(int size) {
		if (graph == null || graph.getWidth() != size) {
			graph = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			rgbArray = new int[size * size];
		}
	}

	@CalledByAny
	private void drawVoronoiGraph(int[] rgbArray, int width, int height) {

		if (nodes == null) return;

		MinetestBiome biome_closest;
		float dist_min;
		int nodeCount = nodes.length;
    	float xScale = (axis_max - axis_min) / (float)width;
    	float yScale = (axis_max - axis_min) / (float)height;
		int index = 0;

		for (int y = 0; y < height; y++) {
			float humidity = (y * yScale) + axis_min;

			// TODO: break up rgbArray?

			for (int x = 0; x < width; x++) {
				float heat = (x * xScale) + axis_min;

				dist_min = Float.MAX_VALUE;
				biome_closest = null;

				for (short i = 0; i < nodeCount; i++) {
					MinetestBiome b = nodes[i];
					float d_heat = heat - b.heat_point;
					float d_humidity = humidity - b.humidity_point;
					float dist = (d_heat * d_heat) + (d_humidity * d_humidity);

					if (dist < dist_min) {
						dist_min = dist;
						biome_closest = b;
					}
				}
				rgbArray[index++] = (biome_closest == null) ? 0x00000000 : biome_closest.getDefaultColor().getRGB();
			}
		}
	}


	private void drawVoronoi(Graphics2D g2d) {

		if (nodes == null) return;

		MinetestBiome biome_closest;
		float dist_min;
		int nodeCount = nodes.length;

		for (int y = axis_min; y <= axis_max; y++) {
			for (int x = axis_min; x <= axis_max; x++) {

				float heat = x;
				float humidity = y;
				dist_min = Float.MAX_VALUE;
				biome_closest = null;

				for (short i = 0; i < nodeCount; i++) {
					MinetestBiome b = nodes[i];
					float d_heat = heat - b.heat_point;
					float d_humidity = humidity - b.humidity_point;
					float dist = (d_heat * d_heat) + (d_humidity * d_humidity);

					if (dist < dist_min) {
						dist_min = dist;
						biome_closest = b;
					}
				}
				if (biome_closest != null) {
					g2d.setColor(biome_closest.getDefaultColor().getColor());
					g2d.drawRect(x, y, 1, 1);
				}
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void Update(MinetestBiomeProfileImpl biomeProfile, int height, int flags) {

		ArrayList<MinetestBiome> biomes = new ArrayList<MinetestBiome>();
		if (biomeProfile != null) for (IBiome biome : biomeProfile.allBiomes()) {
			MinetestBiome mtBiome = (MinetestBiome)biome;
			if (height <= (mtBiome.y_max + mtBiome.vertical_blend) && height >= mtBiome.y_min) {
				biomes.add(mtBiome);
			}
		}

		ArrayList<MinetestBiome> currentNodes = nodes == null ? new ArrayList<MinetestBiome>() : new ArrayList<MinetestBiome>(Arrays.asList(nodes));
		if (flags != this.renderFlags || !currentNodes.equals(biomes)) {
			this.renderFlags = flags;
			this.nodes = biomes.toArray(new MinetestBiome[biomes.size()]);
			invalidate();
			repaint();
		}
	}

	public int getRenderFlags() { return renderFlags; }
}
