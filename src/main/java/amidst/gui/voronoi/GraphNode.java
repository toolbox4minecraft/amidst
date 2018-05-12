package amidst.gui.voronoi;

/** Node information for a Voronoi diagram */
public class GraphNode {
	
	public int argb;
	float x;
	float y;
	
	double occurrenceFrequency;
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (!GraphNode.class.isAssignableFrom(obj.getClass())) return false;
	    final GraphNode other = (GraphNode)obj;
	    return  x == other.x && y == other.y && argb == other.argb;
	}

	@Override
	public int hashCode() {
	    int hash = 8;
	    hash = 37 * hash + (int)argb;
	    hash = 37 * hash + (int)Float.floatToIntBits(x);
	    hash = 37 * hash + (int)Float.floatToIntBits(y);
	    return hash;
	}		
	
	public GraphNode(float x, float y, int argb) {
		this.argb = argb;
		this.x = x;
		this.y = y;
	}
}
