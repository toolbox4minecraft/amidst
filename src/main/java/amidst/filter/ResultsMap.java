package amidst.filter;

import java.util.HashMap;
import java.util.Map;

public class ResultsMap {
	private Map<Criterion<?>, CriterionResult> results;
	
	public ResultsMap() {
		results = new HashMap<>();
	}
	
	public boolean create(Criterion<?> criterion) {
		if(results.containsKey(criterion))
			return false;
		results.put(criterion, criterion.createResult());
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends CriterionResult> T get(Criterion<T> criterion) {
		return (T) results.get(criterion);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends CriterionResult> T remove(Criterion<T> criterion) {
		return (T) results.remove(criterion);
	}
	
	public ResultsMap copy() {
		ResultsMap copy = new ResultsMap();
		copy.results.putAll(results);
		copy.results.replaceAll((cr, res) -> res.copy());
		return copy;
	}
}