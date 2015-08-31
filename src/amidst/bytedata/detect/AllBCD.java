package amidst.bytedata.detect;

import java.util.List;

import amidst.bytedata.ByteClass;

public class AllBCD extends ByteClassDetector {
	private List<ByteClassDetector> detectors;

	public AllBCD(List<ByteClassDetector> detectors) {
		this.detectors = detectors;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		for (ByteClassDetector detector : detectors) {
			if (!detector.detect(byteClass)) {
				return false;
			}
		}
		return true;
	}
}
