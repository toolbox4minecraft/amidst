package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class ObfuscatedNameRCD extends RealClassDetector {
	private final String obfuscatedName;

	public ObfuscatedNameRCD(String obfuscatedName) {
		this.obfuscatedName = obfuscatedName;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.getRealClassName().equals(obfuscatedName);
	}
}
