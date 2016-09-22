package amidst.mojangapi.file.json.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.filter.Criterion;
import amidst.mojangapi.world.filter.CriterionInvalid;

public class CriterionJsonContext {
	private Globals globals;
	
	private CriterionJsonContext parent = null;
	private boolean hasErrors = false;
	
	private String name = "";
	private String shape;
	private CoordinatesInWorld center = null;
	
	private static class Globals {
		public Function<String, CriterionJson> supplier;
		
		//List of criteria already converted;
		//if null, the criterion is being converted and we have a circular reference
		public Map<String, Criterion> mappings;
		public List<String> errors;
		
		public Globals(Function<String, CriterionJson> criterionSupplier) {
			errors = new ArrayList<>();
			mappings = new HashMap<>();
			supplier = criterionSupplier;
		}
	}
	
	public CriterionJsonContext(WorldFilterJson.Defaults defaults, Function<String, CriterionJson> criterionSupplier) {
		shape = defaults.shape;
		center = CoordinatesInWorld.origin();
		globals = new Globals(criterionSupplier);
	}

	
	private CriterionJsonContext(CriterionJsonContext ctx) {
		globals = ctx.globals;
		parent = ctx;
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
	
	public CoordinatesInWorld getCenter() {
		return center;
	}
	
	public CriterionJsonContext withCenter(CoordinatesInWorld center) {
		CriterionJsonContext ctx = copy();
		ctx.center = center;
		return ctx;
	}
	
	public Criterion convertCriterion(String name) {
		CriterionJson json = globals.supplier.apply(name);
		if(json == null) {
			error("the group " + name + " doesn't exist");
			return new CriterionInvalid(name);
		}
		
		if(globals.mappings.containsKey(name)) {
			Criterion c = globals.mappings.get(name);
			if(c == null) {
				error("circular reference to group " + name);
				return new CriterionInvalid(name);
			}
			return c;
		}
		
		globals.mappings.put(name, null);
		CriterionJsonContext ctx = copy();
		ctx.name = name;
		Criterion c = json.validate(ctx);
		globals.mappings.put(name, c);
		return c;
	}
	
	public void error(String msg) {
		msg = "In " + getName() + ": " + msg;
		globals.errors.add(msg);
		
		CriterionJsonContext cur = this;
		while(cur != null && !cur.hasErrors) {
			cur.hasErrors = true;
			cur = cur.parent;
		}
	}
	
	public boolean hasErrors() {
		return hasErrors;
	}
	
	public List<String> getErrors() {
		return globals.errors;
	}
	
}
