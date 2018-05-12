package amidst.gui.voronoi;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.gameengineabstraction.world.biome.IBiome;
import amidst.minetest.world.mapgen.ClimateHistogram;
import amidst.minetest.world.mapgen.IHistogram2D;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.MinetestBiomeProfileImpl;

/**
 * Panel which displays a Voronoi graph of Minetest biomes
 */
public class VoronoiPanel extends JPanel {

	public static final int FLAG_SHOWLABELS         = 0x01;
	public static final int FLAG_SHOWAXIS           = 0x02;
	public static final int FLAG_SHOWNODES          = 0x04;
	public static final int FLAG_SHOWCOVERAGE       = 0x08;

	public static final boolean GRAPHICS_DEBUG      = false;

	private static final float AXIS_WIDTH           = 0.5f;
	private static final int   TICKMARK_WIDTH_SMALL = 2;
	private static final int   TICKMARK_WIDTH_LARGE = 4;
	private static final int   TICKMARK_LABEL_SPACE = 1;
	private static final int   NODE_RADIUS          = 0;
	private static final int   NODE_LABEL_SPACE     = 2;
	private static final int   NODE_LABEL_FONTSIZE  = 3;

	private static final long  serialVersionUID     = 1L;

	private static Stroke          stroke_capButt   = new BasicStroke(AXIS_WIDTH, BasicStroke.CAP_BUTT,   BasicStroke.JOIN_ROUND);
	private static Stroke          stroke_capSquare = new BasicStroke(AXIS_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	private static AffineTransform noTransform      = new AffineTransform();

	public int axis_min         = -40;
	public int axis_max         = 140;
	public int graph_resolution = 1000;

	private IHistogram2D climateHistogram = null;
	ArrayList<MinetestBiome> biomes = new ArrayList<MinetestBiome>();
	private List<GraphNode> graphNodes = null;
	private VoronoiGraph   voronoiGraph   = null;
	private FrequencyGraph frequencyGraph = null;
	private float frequencyGraphOpacity = 0;
	private int renderFlags;

	@Override
	@CalledOnlyBy(AmidstThread.EDT)
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        boolean frequencyGraphWasNull = frequencyGraph == null;
        if (climateHistogram == null) climateHistogram = new ClimateHistogram();
        if (voronoiGraph     == null) voronoiGraph     = new VoronoiGraph(graph_resolution, graph_resolution, climateHistogram);
        if (frequencyGraph   == null) frequencyGraph   = new FrequencyGraph(graph_resolution, graph_resolution);

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

        	// Draw the filled voronoi graph
        	if (graphNodes != null) {
        		g2d.drawImage(
        			voronoiGraph.render(graphNodes, axis_min, axis_max, axis_min, axis_max),
        			axis_min, axis_min, desiredAxisLength, desiredAxisLength, Color.WHITE, null
        		);
        	}

        	// draw nodes
        	drawNodesOrNodeLabels(g2d);

        	// overlay the frequency graph
        	if (frequencyGraphOpacity > 0) {
	        	int rule = AlphaComposite.SRC_OVER;
	            Composite alphaComposite = AlphaComposite.getInstance(rule , frequencyGraphOpacity);
	        	Composite originalComposite = g2d.getComposite();
	            g2d.setComposite(alphaComposite);
	            try {
	            	if (graphNodes != null) {
	            		g2d.drawImage(
	            			frequencyGraph.render(climateHistogram, axis_min, axis_max, axis_min, axis_max),
	            			axis_min, axis_min, desiredAxisLength, desiredAxisLength, null, null
	            		);
	            	}
	            } finally {
	                g2d.setComposite(originalComposite);
	            }
        	}

        	// draw axis
        	if ((renderFlags & FLAG_SHOWAXIS) > 0) drawAxes(g2d);
        }


        if (frequencyGraphWasNull && frequencyGraphOpacity <= 0) {
        	// In order to prevent a delay when the user enables the frequencyGraph,
        	// Make it pre-render after the UI has finished updating.
        	SwingUtilities.invokeLater(() -> {
        		frequencyGraph.render(climateHistogram, axis_min, axis_max, axis_min, axis_max);
    		});
        }
    }

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawNodesOrNodeLabels(Graphics2D g2d) {

		AffineTransform currentTransform = g2d.getTransform();
		Font font_original = g2d.getFont();

		FontMetrics fontMetrics = g2d.getFontMetrics(font_original);
    	boolean showNodes = (renderFlags & FLAG_SHOWNODES) > 0;
    	boolean showLabels = (renderFlags & FLAG_SHOWLABELS) > 0;
    	boolean showCoverage = (renderFlags & FLAG_SHOWCOVERAGE) > 0;
    	int biomeIndex = 0;

    	for (MinetestBiome biome: biomes) {
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

	    	Point2D fontPos            = new Point(x, y - (showNodes ? NODE_LABEL_SPACE : 0));
	    	Point2D fontPosTransformed = currentTransform.transform(fontPos, null);
	    	float fontY = (float)fontPosTransformed.getY() + (fontMetrics.getAscent() / 4f);

	        g2d.setTransform(noTransform);
	        try {
		    	if (showLabels) {
			    	g2d.drawString(
						biome.getName(),
						(float)fontPosTransformed.getX() - (fontMetrics.stringWidth(biome.getName()) / 2),
						fontY
					);
		    		fontY += fontMetrics.getAscent();
		    	}

		    	if (showCoverage) {
			        String value = String.format(" %.1f", graphNodes.get(biomeIndex).occurrenceFrequency * 100);
			        if (value.charAt(value.length() - 1) == '0') value = value.substring(0, value.length() - 2);
			        value += "%";

			    	g2d.drawString(
			    		value,
						(float)fontPosTransformed.getX() - (fontMetrics.stringWidth(value) / 2),
						fontY
					);
		    	}
	        } finally {
		        g2d.setTransform(currentTransform);
	        }
    		biomeIndex++;
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
    				int tickmarkWidth = (i % 50 == 0) ? (int)Math.ceil((TICKMARK_WIDTH_SMALL + TICKMARK_WIDTH_LARGE) / 2f) : TICKMARK_WIDTH_SMALL;
    				g2d.drawLine(0, i, -tickmarkWidth, i);
    				g2d.drawLine(i, 0, i, -tickmarkWidth);
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
	public static double perceivedBrightness(Color col)
	{
		return Math.sqrt(
			.299 * Math.pow(col.getRed(),   2) +
			.587 * Math.pow(col.getGreen(), 2) +
			.114 * Math.pow(col.getBlue(),  2)
		);
	}

	public int getRenderFlags() { return renderFlags; }

	public String getDistributionData() {
		StringBuilder result = new StringBuilder();

    	int biomeIndex = 0;
    	for (MinetestBiome biome: biomes) {
    		result.append(biome.getName());
    		result.append(", ");
    		result.append(String.format(" %.1f%%\r\n", graphNodes.get(biomeIndex).occurrenceFrequency * 100));
    		biomeIndex++;
    	}
		return result.toString();
	}

	/**
	 * If the world doesn't use Minetest's default Heat&Humidity algorithm, then pass
	 * a histogram of it here.
	 */
	public void setClimateHistogram(IHistogram2D climate_histogram) { this.climateHistogram = climate_histogram; }

	@CalledOnlyBy(AmidstThread.EDT)
	public void Update(MinetestBiomeProfileImpl biomeProfile, int altitude, float frequency_graph_opacity, int flags) {

		ArrayList<MinetestBiome> newBiomes = new ArrayList<MinetestBiome>();
		if (biomeProfile != null) for (IBiome biome : biomeProfile.allBiomes()) {
			MinetestBiome mtBiome = (MinetestBiome)biome;
			if (altitude <= (mtBiome.y_max + mtBiome.vertical_blend) && altitude >= mtBiome.y_min) {
				newBiomes.add(mtBiome);
			}
		}

		if (flags != this.renderFlags || !newBiomes.equals(biomes) || frequencyGraphOpacity != frequency_graph_opacity) {
			this.renderFlags = flags;
			biomes = newBiomes;
			frequencyGraphOpacity = frequency_graph_opacity;
			graphNodes = new ArrayList<GraphNode>();
			for(MinetestBiome biome: newBiomes) {
				graphNodes.add(new GraphNode(biome.heat_point, biome.humidity_point, biome.getDefaultColor().getRGB()));
			}

			repaint();
		}
	}
}
