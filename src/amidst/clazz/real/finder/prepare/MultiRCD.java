package amidst.clazz.real.finder.prepare;

import java.util.List;

import amidst.clazz.real.RealClass;

public class MultiRCD extends RealClassPreparer {
	private List<RealClassPreparer> preparers;

	public MultiRCD(List<RealClassPreparer> preparers) {
		this.preparers = preparers;
	}

	@Override
	public void prepare(RealClass byteClass) {
		for (RealClassPreparer preparer : preparers) {
			preparer.prepare(byteClass);
		}
	}
}
