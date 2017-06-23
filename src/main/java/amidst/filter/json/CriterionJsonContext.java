package amidst.filter.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import amidst.filter.Criterion;
import amidst.mojangapi.world.coordinates.Coordinates;

public class CriterionJsonContext {
	private Globals globals;
	
	private boolean hasErrors = false;
	
	private String name = "";
	private String shape;
	private Coordinates center = null;
	
	private static class Globals {
		public Function<String, CriterionJson> supplier;
		
		//List of criteria already converted;
		//if null, the criterion is being converted and we have a circular reference
		public Map<String, Optional<Criterion<?>>> mappings;
		public List<String> errors;
		
		public Globals(Function<String, CriterionJson> criterionSupplier) {
			errors = new ArrayList<>();
			mappings = new HashMap<>();
			supplier = criterionSupplier;
		}
	}
	
	public CriterionJsonContext(WorldFilterJson.Defaults defaults, Function<String, CriterionJson> criterionSupplier) {
		shape = defaults.shape;
		center = Coordinates.origin();
		globals = new Globals(criterionSupplier);
	}

	
	private CriterionJsonContext(CriterionJsonContext ctx) {
		globals = ctx.globals;
		name = ctx.name;
		hasErrors = false;
		shape = ctx.shape;
		center = ctx.center;
	}
	
	public CriterionJsonContext copy() {
		return new CriterionJsonContext(this);
	}
	
	public String getName() {
		return name.isEmpty() ? "<root>" : name;
	}
	
	public CriterionJsonContext withName(String name) {
		CriterionJsonContext ctx = copy();
		if(!ctx.name.isEmpty())
			ctx.name += ".";
		ctx.name += name;
		return ctx;
	}
	
	public String getShape() {
		return shape;
	}
	
	public CriterionJsonContext withShape(String shape) {
		CriterionJsonContext ctx = copy();
		ctx.shape = shape;
		return ctx;
	}
	
	public Coordinates getCenter() {
		return center;
	}
	
	public CriterionJsonContext withCenter(Coordinates center) {
		CriterionJsonContext ctx = copy();
		ctx.center = center;
		return ctx;
	}
	
	public Optional<Criterion<?>> convertCriterion(String name) {
		CriterionJson json = globals.supplier.apply(name);
		if(json == null) {
			error("the group " + name + " doesn't exist");
			return Optional.empty();
		}
		
		if(globals.mappings.containsKey(name)) {
			Optional<Criterion<?>> c = globals.mappings.get(name);
			if(c == null) {
				error("circular reference to group " + name);
				return Optional.empty();
			}
			return c;
		}
		
		globals.mappings.put(name, null);
		CriterionJsonContext ctx = copy();
		ctx.name = name;
		Optional<Criterion<?>> c = json.validate(ctx);
		globals.mappings.put(name, c);
		return c;
	}
	
	public void error(String msg) {
		msg = "In " + getName() + ": " + msg;
		globals.errors.add(msg);
		hasErrors = true;
	}
	
	public void unsupportedAttribute(String attName) {
		error("the " + attName + " attribute isn't supported yet");
	}
	
	public boolean hasErrors() {
		return hasErrors;
	}
	
	public List<String> getErrors() {
		return globals.errors;
	}
	
}
