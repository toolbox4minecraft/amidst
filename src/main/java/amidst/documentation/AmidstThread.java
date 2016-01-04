package amidst.documentation;

import amidst.logging.FileLogger;

/**
 * Each entry of this enum describes one type of thread that will ever exist in
 * the project. It is only used for documentation purposes.
 */
public enum AmidstThread {
	/**
	 * The thread that calls the main method at application startup. This will
	 * do some initialization. Afterwards it calls the EDT to create and start
	 * the application. So this thread dies pretty quickly.
	 */
	STARTUP,

	/**
	 * This thread constantly causes the EDT to repaint the map, however it does
	 * not execute any other code by itself.
	 */
	REPAINTER,

	/**
	 * This thread is the Event Dispatch Thread used by Swing and AWT. All GUI
	 * events are executed in this thread. Also, the drawing of the GUI is
	 * executed by this thread. This thread should not be used to execute long
	 * running tasks.
	 */
	EDT,

	/**
	 * This thread constantly loads, reloads and recycles fragments, because it
	 * takes to long to do this in the EDT. Since this thread and the EDT
	 * constantly read from and write to the fragments and fragment graph, extra
	 * care must be used in this part of the application.
	 */
	FRAGMENT_LOADER,

	/**
	 * This is actually a pool of thread that are created and removed as needed.
	 * They are used to execute tasks that take to long for the EDT and belong
	 * to no other thread, e.g. the loading of skins. After a worker has
	 * finished the background task, it should pass its result to the EDT to
	 * display the result to the user.
	 */
	WORKER,

	/**
	 * The file logger also uses a single threaded scheduled executor service to
	 * actually write the logging messages to the file. However, this thread
	 * should never leave the class {@link FileLogger}.
	 */
	FILE_LOGGER;
}
