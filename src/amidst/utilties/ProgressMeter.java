package amidst.utilties;

import java.util.Deque;

public class ProgressMeter {
	public float minimum = 0.0f;
	public float maximum = 1.0f;
	public float progress = 0.0f;
	public boolean isComplete = false;
	
	public Deque<ProgressListener> listeners;
	
	
	public ProgressMeter() {
		
	}
	
	public void update(float value) {
		progress = value;
		for (ProgressListener listener : listeners)
			listener.onComplete(this);
		
		if (!isComplete) {
			if (progress >= maximum) { 
				isComplete = true;
				for (ProgressListener listener : listeners)
					listener.onComplete(this);
			}
		}
	}
	
	public void reset() {
		progress = minimum;
		isComplete = false;
	}
	
	public float getPrecentage() {
		return (progress - minimum) / (maximum - minimum);
	}
}
