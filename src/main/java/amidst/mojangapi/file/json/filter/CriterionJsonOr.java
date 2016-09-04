package amidst.mojangapi.file.json.filter;

import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.file.world.filter.Criterion;
import amidst.mojangapi.file.world.filter.CriterionAnd;

public class CriterionJsonOr extends CriterionJson {
	@JsonField()
	public List<CriterionJson> or;
	
	@JsonField(optional=true)
	int minScore = Integer.MIN_VALUE;
	
	@JsonField(optional=true)
	int min = 1;
	
	@GsonConstructor
	public CriterionJsonOr() {}
	
	@Override
	protected Optional<Criterion> doValidate(CriterionJsonContext ctx) {
		
		List<Criterion> criteria = validateList(or, ctx, "or");
		
		if(criteria.size() < or.size())
			return Optional.empty();
		
		if(minScore != Integer.MIN_VALUE) {
			ctx.error("the minScore attribute isn't supported yet");
			return Optional.empty();
		}
		
		return Optional.of(new CriterionAnd(ctx.getName(), criteria));
	}
}