package amidst.util;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Method;
import java.util.EventListener;

import javax.swing.MenuElement;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

public enum SwingUtils {
	;

	/**
	 * This helps with allowing swing components to get garbage collected.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public static void destroyComponentTree(Component c) {
		// calls the component's dispose method if it has it.
		try {
			Method m = c.getClass().getMethod("dispose");
			m.invoke(c);
		} catch (Exception e) {
		}
		
		SwingUtils.removeListeners(c);
		
		if (c instanceof Container) {
			for (Component child : ((Container) c).getComponents()) {
				destroyComponentTree(child);
			}
			
			// for some reason menus don't return their tree correctly when using getComponents, so we have to do this instead
			if (c instanceof MenuElement) {
				for (MenuElement child : ((MenuElement) c).getSubElements()) {
					destroyComponentTree(child.getComponent());
				}
			}
		}
	}

	/**
	 * <a href="https://bugs.openjdk.java.net/browse/JDK-4380536?focusedCommentId=12103089&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-12103089">source</a>
	 */
	@SuppressWarnings("unchecked")
	@CalledOnlyBy(AmidstThread.EDT)
	public static void removeListeners(Component comp) {
		Method[] methods = comp.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String name = method.getName();
			if (name.startsWith("remove") && name.endsWith("Listener")) {
				
				Class<?>[] params = method.getParameterTypes();
				if (params.length == 1) {
					EventListener[] listeners = null;
					try {
						listeners = comp.getListeners((Class<? extends EventListener>) params[0]);
					} catch (Exception e) {
						// It is possible that someone could create a listener
						// that doesn't extend from EventListener. If so, ignore it
						System.out.println("Listener " + params[0] + " does not extend EventListener");
						continue;
					}
					for (int j = 0; j < listeners.length; j++) {
						try {
							method.invoke(comp, new Object[] { listeners[j] });
							//System.out.println("removed Listener " + name + " for comp " + comp + "\n");
						} catch (Exception e) {
							System.out.println("Cannot invoke removeListener method " + e);
							// Continue on. The reason for removing all listeners is to
							// make sure that we don't have a listener holding on to something
							// which will keep it from being garbage collected. We want to
							// continue freeing listeners to make sure we can free as much
							// memory has possible
						}
					}
				} else {
					// The only Listener method that I know of that has more than
					// one argument is removePropertyChangeListener. If it is
					// something other than that, flag it and move on.
					if (!name.equals("removePropertyChangeListener"))
						System.out.println(" Wrong number of Args " + name);
				}
			}
		}
	}
	
}
