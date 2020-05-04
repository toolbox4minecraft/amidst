package amidst.dependency.injection;

public interface Factory3<P1, P2, P3, R> {
	R create(P1 p1, P2 p2, P3 p3);
}
