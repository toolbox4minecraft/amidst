package amidst.mojangapi.file.json.filter;

import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.file.world.filter.Criterion;
import amidst.mojangapi.file.world.filter.CriterionAnd;

public class CriterionJsonAnd extends CriterionJson {
	@JsonField()
	public List<CriterionJson> and;
	
	@GsonConstructor
	public CriterionJsonAnd() {}
	
	@Override
	protected Optional<Criterion> doValidate(CriterionJsonContext ctx) {
		
		List<Criterion> criteria = validateList(and, ctx, "and");
		
		if(criteria.size() < and.size())
			return Optional.empty();
		
		return Optional.of(new CriterionAnd(ctx.getName(), criteria));
	}
}