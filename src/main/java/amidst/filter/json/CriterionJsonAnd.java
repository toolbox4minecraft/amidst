package amidst.filter.json;

import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonObject;
import amidst.documentation.JsonField;
import amidst.filter.Criterion;
import amidst.filter.criterion.MatchAllCriterion;

@GsonObject
public class CriterionJsonAnd extends CriterionJson {
	@JsonField()
	public List<CriterionJson> and;

	public CriterionJsonAnd() {}
	
	@Override
	protected Optional<Criterion> doValidate(CriterionJsonContext ctx) {
		return validateList(and, ctx, "and")
				.map(MatchAllCriterion::new);
	}
}