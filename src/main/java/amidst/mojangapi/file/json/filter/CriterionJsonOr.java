package amidst.mojangapi.file.json.filter;

import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.world.filter.Criterion;
import amidst.mojangapi.world.filter.CriterionAnd;

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
		if(minScore != Integer.MIN_VALUE) {
			ctx.unsupportedAttribute("minScore");
			return Optional.empty();
		}
		
		return validateList(or, ctx, "or")
				.map(l -> new CriterionAnd(ctx.getName(), l));
	}
}