package amidst.dependency.injection;

public interface Factory2<P1, P2, R> {
	R create(P1 p1, P2 p2);
}
