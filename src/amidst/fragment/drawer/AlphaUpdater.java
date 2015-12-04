package amidst.fragment.drawer;

import java.awt.Graphics2D;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;

public class AlphaUpdater extends FragmentDrawer {
	public AlphaUpdater(LayerDeclaration declaration) {
		super(declaration);
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d, float time) {
		fragment.setAlpha(Math.min(1.0f, time * 3.0f + fragment.getAlpha()));
	}
}
