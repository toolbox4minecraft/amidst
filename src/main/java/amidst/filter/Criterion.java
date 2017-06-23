package amidst.filter;

import java.util.List;

import amidst.documentation.Immutable;

@Immutable
public interface Criterion {	
	public List<Criterion> getChildren();
}
