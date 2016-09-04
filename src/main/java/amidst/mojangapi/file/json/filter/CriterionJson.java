package amidst.mojangapi.file.json.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.file.world.filter.Criterion;
import amidst.mojangapi.file.world.filter.CriterionNegate;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

public abstract class CriterionJson {
	
	@JsonField(optional=true)
	public CoordinatesInWorld center = CoordinatesInWorld.origin();
	
	@JsonField(optional=true)
	public boolean negate = false;
	
	@JsonField(optional=true)
	public int score = 0;
	
	@GsonConstructor
	public CriterionJson() {}
	
	public Optional<Criterion> validate(CriterionJsonContext ctx) {

		if(center != null) {
			ctx.withCenter(ctx.getCenter().add(center));
		}
		
		Optional<Criterion> criterion;
		if(negate) {
			criterion = doValidate(ctx.withName("!"))
					.map(c -> new CriterionNegate(ctx.getName(), c));
		} else {
			criterion = doValidate(ctx);
		}
		
			
		if(score != 0) {
			ctx.error("the score attribute isn't supported yet");
		}
		
		return criterion;
	}
	
	protected abstract Optional<Criterion> doValidate(CriterionJsonContext ctx);
	
	
	protected static List<Criterion> validateList(List<CriterionJson> list, CriterionJsonContext ctx, String listName) {	
		List<Criterion> criteria = new ArrayList<>();
		for(int i = 0; i < list.size(); i++) {
			Optional<Criterion> c = list.get(i).validate(ctx.withName(listName + "[" + i + "]"));
			c.ifPresent(criteria::add);
		}
		return criteria;
	}
}
