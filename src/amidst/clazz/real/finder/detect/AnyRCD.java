package amidst.clazz.real.finder.detect;

import java.util.List;

import amidst.clazz.real.RealClass;

public class AnyRCD extends RealClassDetector {
	private List<RealClassDetector> detectors;

	public AnyRCD(List<RealClassDetector> detectors) {
		this.detectors = detectors;
	}

	@Override
	public boolean detect(RealClass realClass) {
		for (RealClassDetector detector : detectors) {
			if (detector.detect(realClass)) {
				return true;
			}
		}
		return false;
	}
}
