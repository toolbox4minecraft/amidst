package amidst.mojangapi.file.json.filter;

import java.util.List;

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
	protected Criterion doValidate(CriterionJsonContext ctx) {

		return new CriterionAnd(ctx.getName(), validateList(and, ctx, "and"));
	}
}