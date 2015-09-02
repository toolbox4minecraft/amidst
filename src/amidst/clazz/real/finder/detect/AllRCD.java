package amidst.clazz.real.finder.detect;

import java.util.List;

import amidst.clazz.real.RealClass;

public class AllRCD extends RealClassDetector {
	private List<RealClassDetector> detectors;

	public AllRCD(List<RealClassDetector> detectors) {
		this.detectors = detectors;
	}

	@Override
	public boolean detect(RealClass byteClass) {
		for (RealClassDetector detector : detectors) {
			if (!detector.detect(byteClass)) {
				return false;
			}
		}
		return true;
	}
}
