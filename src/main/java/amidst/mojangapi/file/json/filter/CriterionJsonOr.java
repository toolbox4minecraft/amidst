package amidst.mojangapi.file.json.filter;

import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.world.filter.Criterion;
import amidst.mojangapi.world.filter.CriterionAnd;
import amidst.mojangapi.world.filter.CriterionInvalid;

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
	protected Criterion doValidate(CriterionJsonContext ctx) {		
		if(minScore != Integer.MIN_VALUE) {
			ctx.error("the minScore attribute isn't supported yet");
			return new CriterionInvalid(ctx.getName());
		}
		
		return new CriterionAnd(ctx.getName(), validateList(or, ctx, "or"));
	}
}