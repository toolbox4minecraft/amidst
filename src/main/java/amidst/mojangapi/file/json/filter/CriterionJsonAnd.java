package amidst.mojangapi.file.json.filter;

import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.world.filter.Criterion;
import amidst.mojangapi.world.filter.CriterionAnd;

public class CriterionJsonAnd extends CriterionJson {
	@JsonField()
	public List<CriterionJson> and;
	
	@GsonConstructor
	public CriterionJsonAnd() {}
	
	@Override
	protected Optional<Criterion> doValidate(CriterionJsonContext ctx) {

		return validateList(and, ctx, "and")
				.map(l -> new CriterionAnd(ctx.getName(), l));
	}
}