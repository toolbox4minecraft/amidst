package amidst.clazz.real.detector;

import java.util.List;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class AllRCD extends RealClassDetector {
	private final List<RealClassDetector> detectors;

	public AllRCD(List<RealClassDetector> detectors) {
		this.detectors = detectors;
	}

	@Override
	public boolean detect(RealClass realClass) {
		for (RealClassDetector detector : detectors) {
			if (!detector.detect(realClass)) {
				return false;
			}
		}
		return true;
	}
}
