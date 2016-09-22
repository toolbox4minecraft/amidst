package amidst.mojangapi.file.json.filter;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.world.filter.Criterion;

public class CriterionJsonGroup extends CriterionJson {
	@JsonField()
	public String group;
	
	@GsonConstructor
	public CriterionJsonGroup() {}

	@Override
	protected Criterion doValidate(CriterionJsonContext ctx) {
		return ctx.convertCriterion(group);
	}
}