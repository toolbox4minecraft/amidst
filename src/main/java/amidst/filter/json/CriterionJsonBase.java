package amidst.filter.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import amidst.documentation.GsonObject;
import amidst.documentation.JsonField;
import amidst.filter.BiomeConstraint;
import amidst.filter.Criterion;
import amidst.filter.StructureConstraint;
import amidst.filter.criterion.MatchAnyCriterion;
import amidst.filter.criterion.SimpleCriterion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

@GsonObject
public class CriterionJsonBase extends CriterionJson {
	@JsonField()
	public int radius;
	
	@JsonField(optional=true)
	public String shape = null;
	
	@JsonField(optional=true)
	public List<String> biomes = Collections.emptyList();
	
	@JsonField(optional=true)
	public boolean variants = false;

	@JsonField(optional=true)
	public List<String> structures = null;
	
	@JsonField(optional=true, require={"structures"})
	public ClusterInfo cluster;

	@GsonObject
	public static class ClusterInfo {
		@JsonField(optional=true)
		public int radius = 0;
		
		@JsonField()
		public int size;
	}
	
	
	@Override
	protected Optional<Criterion<?>> doValidate(CriterionJsonContext ctx) {

		if(shape == null)
			shape = ctx.getShape();
		
		if(cluster != null)
			ctx.unsupportedAttribute("cluster");
		
		if(radius <= 0)
			ctx.error("the radius must be strictly positive (is " + radius + ")");
		
		Collection<Biome> biomeSet = getBiomeSet(ctx);
		Collection<DefaultWorldIconTypes> structSet = getStructureSet(ctx);
		if(biomeSet.isEmpty() && structSet.isEmpty())
			ctx.error("the biome list can't be empty if no structure is specified");
		
		
		boolean isChecked = false;
		boolean isSquare = false;
		switch(shape) {
		case "square":
			isChecked = true;
			isSquare = true;
			break;
			
		case "circle":
			isChecked = true;
			isSquare = false;
			break;
			
		case "square_nocheck":
			isChecked = false;
			isSquare = true;
			break;
			
		case "circle_nocheck":
			isChecked = false;
			isSquare = false;
			break;
			
		default:
			ctx.error("unknown shape " + shape);
		}			
					
		if(ctx.hasErrors())
			return Optional.empty();
		
		Region region = isSquare ? Region.box(center, radius) : Region.circle(center, radius);
		
		
		List<Criterion<?>> list = new ArrayList<>();
		
		if(structSet.isEmpty()) {
			for(Biome b: biomeSet) {
				list.add(new SimpleCriterion(
					new BiomeConstraint(region, b, isChecked)
				));
			}
		} else if(biomeSet.isEmpty()) {
			for(DefaultWorldIconTypes struct: structSet) {
				list.add(new SimpleCriterion(
					new StructureConstraint(region, struct, null, isChecked)
				));
			}
		} else {
			for(Biome b: biomeSet) {
				for(DefaultWorldIconTypes struct: structSet) {
					list.add(new SimpleCriterion(
						new StructureConstraint(region, struct, b, isChecked)
					));
				}
			}
		}
		
		
		if(list.size() == 1)
			return Optional.of(list.get(0));

		return Optional.of(new MatchAnyCriterion(list));
	}
	

	private Collection<Biome> getBiomeSet(CriterionJsonContext ctx) {
		Set<Biome> biomeSet = new HashSet<>();
		for(String biomeName: biomes) {
			if(Biome.exists(biomeName)) {
				Biome b = Biome.getByName(biomeName); 
				if(!biomeSet.add(b))
					ctx.error("duplicate biome " + b.getName());
				
				if(variants) {
					Biome spec = b.getSpecialVariant();
					if(b != spec && !biomeSet.add(spec))
						ctx.error("duplicate biome " + spec.getName());
				}
							
			} else ctx.error("the biome " + biomeName + " doesn't exist");
		}
		return biomeSet;
	}

	private Collection<DefaultWorldIconTypes> getStructureSet(CriterionJsonContext ctx) {	
		Set<DefaultWorldIconTypes> structSet = new HashSet<>();
		if(structures == null)
			return structSet;
		
		for(String structName: this.structures) {
			DefaultWorldIconTypes struct = DefaultWorldIconTypes.getByName(structName.toLowerCase());
			if(struct == null)
				ctx.error("the structure " + structName + " doesn't exist");
			else if(StructureConstraint.UNSUPPORTED_STRUCTURES.contains(struct))
				ctx.error("the structure " + structName + " isn't supported");
			else structSet.add(struct);
		}
		return structSet;
	}
	
	public static class ClusterInfoDeserializer implements JsonDeserializer<ClusterInfo> {
		@Override
		public ClusterInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if(json.isJsonPrimitive()) {
				JsonObject obj = new JsonObject();
				obj.add("size", json);
				json = obj;
			}
			
			return context.deserialize(json, ClusterInfo.class);
		}
		
	}
}