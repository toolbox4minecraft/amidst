package amidst.mojangapi.file.json.filter;

import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.file.world.filter.Criterion;

public class CriterionJsonGroup extends CriterionJson {
	@JsonField()
	public String group;
	
	@GsonConstructor
	public CriterionJsonGroup() {}

	@Override
	protected Optional<Criterion> doValidate(CriterionJsonContext ctx) {
		return ctx.convertCriterion(group);
	}
}