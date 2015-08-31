package amidst.bytedata;

public abstract class NameLessAcceptingClassChecker extends ClassChecker {
	public NameLessAcceptingClassChecker() {
		super(null);
	}

	@Override
	public boolean isMatching(ByteClass byteClass) {
		return true;
	}
}
