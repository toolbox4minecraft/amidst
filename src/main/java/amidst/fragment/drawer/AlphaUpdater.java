package amidst.fragment.drawer;

import java.awt.Graphics2D;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;

@NotThreadSafe
public class AlphaUpdater extends FragmentDrawer {
	public AlphaUpdater(LayerDeclaration declaration) {
		super(declaration);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void draw(Fragment fragment, Graphics2D g2d, float time) {
		fragment.setAlpha(Math.min(1.0f, time * 3.0f + fragment.getAlpha()));
	}
}
