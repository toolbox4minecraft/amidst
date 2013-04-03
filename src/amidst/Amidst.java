package amidst;

import MoF.FinderWindow;
import MoF.Google;

import java.io.IOException;

public class Amidst {
	public static FinderWindow window;
	
	public static void main(String args[]) throws IOException {
		Google.startTracking();
		Google.track("Run");
		//TODO: load options
		new FinderWindow();
	}
}
