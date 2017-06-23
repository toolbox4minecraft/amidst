package amidst.filter.json;

import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonObject;
import amidst.documentation.JsonField;
import amidst.filter.Criterion;
import amidst.filter.criterion.MatchAnyCriterion;

@GsonObject
public class CriterionJsonOr extends CriterionJson {
	@JsonField()
	public List<CriterionJson> or;
	
	@JsonField(optional=true)
	public int minScore = Integer.MIN_VALUE;
	
	@JsonField(optional=true)
	public int min = 1;

	public CriterionJsonOr() {}
	
	@Override
	protected Optional<Criterion> doValidate(CriterionJsonContext ctx) {		
		if(minScore != Integer.MIN_VALUE)
			ctx.unsupportedAttribute("minScore");
		
		if(min <= 0)
			ctx.error("the min attribute must be strictly positive");
		
		if(min > or.size())
			ctx.error("the min attribute can't be greater than the number of children (" + or.size() + ")");
		
		if(min != 1)
			ctx.unsupportedAttribute("min");
		
		if(ctx.hasErrors())
			return Optional.empty();
		
		return validateList(or, ctx, "or")
				.map(l -> new MatchAnyCriterion(l));
	}
}