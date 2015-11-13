package amidst;

import MoF.FinderWindow;
import MoF.Project;
import amidst.gui.version.VersionSelectWindow;

public class Application {
	private VersionSelectWindow versionSelectWindow = new VersionSelectWindow();
	private FinderWindow mapWindow;
	private Project project;

	public Application(FinderWindow mapWindow) {
		this.mapWindow = mapWindow;
	}

	public FinderWindow getWindow() {
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
