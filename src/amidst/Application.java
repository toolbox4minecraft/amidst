package amidst;

import MoF.MapWindow;
import MoF.Project;
import amidst.gui.version.VersionSelectWindow;

public class Application {
	private VersionSelectWindow versionSelectWindow = new VersionSelectWindow();
	private MapWindow mapWindow;
	private Project project;

	public Application(MapWindow mapWindow) {
		this.mapWindow = mapWindow;
	}

	public MapWindow getWindow() {
		return mapWindow;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		mapWindow.setProject(project);
	}

	public VersionSelectWindow getVersionSelectWindow() {
		return versionSelectWindow;
	}
}
