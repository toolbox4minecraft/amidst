package amidst.fragment.drawer;

import java.awt.Graphics2D;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.FragmentGraphItem;

public abstract class FragmentDrawer {
	protected final LayerDeclaration declaration;

	public FragmentDrawer(LayerDeclaration declaration) {
		this.declaration = declaration;
	}

	public LayerDeclaration getLayerDeclaration() {
		return declaration;
	}

	public abstract void draw(FragmentGraphItem fragment, Graphics2D g2d);
}
