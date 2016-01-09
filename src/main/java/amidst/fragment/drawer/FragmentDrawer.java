package amidst.fragment.drawer;

import java.awt.Graphics2D;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;

@NotThreadSafe
public abstract class FragmentDrawer {
	protected final LayerDeclaration declaration;

	public FragmentDrawer(LayerDeclaration declaration) {
		this.declaration = declaration;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isEnabled() {
		return declaration.isVisible();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isDrawUnloaded() {
		return declaration.isDrawUnloaded();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public abstract void draw(Fragment fragment, Graphics2D g2d, float time);
}
