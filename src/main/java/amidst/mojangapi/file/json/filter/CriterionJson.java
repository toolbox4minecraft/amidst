package amidst.mojangapi.file.json.filter;

import java.util.ArrayList;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.file.world.filter.Criterion;
import amidst.mojangapi.file.world.filter.CriterionInvalid;
import amidst.mojangapi.file.world.filter.CriterionNegate;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

/*
 * This class represent a criterion, as represented in the JSON structure.
 * The criterion is NOT validated, and may not be valid for semantic reasons.
 * 
 * It can be converted to an usable form with the method validate().
 */
public abstract class CriterionJson {
	
	@JsonField(optional=true)
	public CoordinatesInWorld center = CoordinatesInWorld.origin();
	
	@JsonField(optional=true)
	public boolean negate = false;
	
	@JsonField(optional=true)
	public int score = 0;
	
	@GsonConstructor
	public CriterionJson() {}
	
	/*
	 * This method validate the criterion and convert it to a
	 * instance of the amidst.mojangapi.world.filter.Criterion.
	 * 
	 * The CriterionJsonContext is used to collect errors and to
	 * provide default values.
	 * 
	 * If any error occurs while validating a criterion, it will be
	 * converted to an instance of CriterionInvalid.
	 */
	public Criterion validate(CriterionJsonContext ctx) {

		if(center != null) {
			ctx.withCenter(ctx.getCenter().add(center));
		}
		
		Criterion criterion;
		if(negate) {
			criterion = new CriterionNegate(ctx.getName(), doValidate(ctx.withName("!")));
		} else {
			criterion = doValidate(ctx);
		}
		
			
		if(score != 0) {
			ctx.error("the score attribute isn't supported yet");
			return new CriterionInvalid(ctx.getName());
		}
		
		return criterion;
	}
	

	// This method takes care of subclass-specific validation.
	protected abstract Criterion doValidate(CriterionJsonContext ctx);

	
	protected static List<Criterion> validateList(List<CriterionJson> list, CriterionJsonContext ctx, String listName) {	
		List<Criterion> criteria = new ArrayList<>();
		for(int i = 0; i < list.size(); i++) {
			criteria.add(list.get(i).validate(ctx.withName(listName + "[" + i + "]")));
		}
		return criteria;
	}
}
