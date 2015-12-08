package amidst.fragment.drawer;

import java.awt.Graphics2D;

import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;

public abstract class FragmentDrawer {
	protected final LayerDeclaration declaration;

	public FragmentDrawer(LayerDeclaration declaration) {
		this.declaration = declaration;
	}

	public boolean isEnabled() {
		return declaration.isVisible();
	}

	public abstract void draw(Fragment fragment, Graphics2D g2d, float time);
}
