package amidst.utilties;

public abstract class ProgressListener {
	public abstract void onUpdate(ProgressMeter meter, double value);
	public abstract void onComplete(ProgressMeter meter);
}
