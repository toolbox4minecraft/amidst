package amidst.filter.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonObject;
import amidst.documentation.JsonField;
import amidst.filter.Criterion;
import amidst.filter.criterion.NegateCriterion;
import amidst.mojangapi.world.coordinates.Coordinates;

/**
 * This class represent a criterion, as represented in the JSON structure.
 * The criterion is NOT validated, and may not be valid for semantic reasons.
 * 
 * It can be converted to an usable form with the method validate().
 */
@GsonObject
public abstract class CriterionJson {
	
	@JsonField(optional=true)
	public Coordinates center = Coordinates.origin();
	
	@JsonField(optional=true)
	public boolean negate = false;
	
	@JsonField(optional=true)
	public int score = 0;
	
	public CriterionJson() {}
	
	/**
	 * This method validate the criterion and convert it to a
	 * instance of the amidst.mojangapi.world.filter.Criterion.
	 * 
	 * The CriterionJsonContext is used to collect errors and to
	 * provide default values.
	 * 
	 * If any error occurs while validating a criterion, an empty
	 * Optional is returned.
	 */
	public Optional<Criterion<?>> validate(CriterionJsonContext ctx) {

		if(center != null) {
			ctx.withCenter(ctx.getCenter().add(center));
		}
		
		Optional<Criterion<?>> criterion;
		if(negate) {
			criterion = doValidate(ctx.withName("!"))
						.map(c -> new NegateCriterion(ctx.getName(), c));
		} else {
			criterion = doValidate(ctx);
		}
		
			
		if(score != 0) {
			ctx.unsupportedAttribute("score");
			return Optional.empty();
		}
		
		return criterion;
	}
	

	// This method takes care of subclass-specific validation.
	protected abstract Optional<Criterion<?>> doValidate(CriterionJsonContext ctx);

	
	protected static Optional<List<Criterion<?>>> validateList(List<CriterionJson> list, CriterionJsonContext ctx, String listName) {	
		List<Criterion<?>> criteria = new ArrayList<>();
		boolean isOk = true;
		for(int i = 0; i < list.size(); i++) {
			Optional<Criterion<?>> res = list.get(i).validate(ctx.withName(listName + "[" + i + "]"));
			
			if(isOk && res.isPresent())
				criteria.add(res.get());
			else isOk = false;
		}
		return isOk ? Optional.of(criteria) : Optional.empty();
	}
}
