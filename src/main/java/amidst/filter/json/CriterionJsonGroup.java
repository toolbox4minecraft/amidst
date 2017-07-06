package amidst.filter.json;

import java.util.Optional;

import amidst.documentation.GsonObject;
import amidst.documentation.JsonField;
import amidst.filter.Criterion;

@GsonObject
public class CriterionJsonGroup extends CriterionJson {
	@JsonField()
	public String group;

	public CriterionJsonGroup() {}

	@Override
	protected Optional<Criterion<?>> doValidate(CriterionJsonContext ctx) {
		return ctx.convertCriterion(group);
	}
}