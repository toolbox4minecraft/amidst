package amidst.mojangapi.file.json.filter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import amidst.documentation.JsonField;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.filter.Criterion;
import amidst.mojangapi.world.filter.CriterionBiome;
import amidst.mojangapi.world.filter.CriterionInvalid;

public class CriterionJsonBase extends CriterionJson {
	@JsonField()
	public long radius;
	
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
	
	public static class ClusterInfo {
		@JsonField(optional=true)
		public long radius = 0;
		
		@JsonField()
		public int size;
	}
	
	
	@Override
	protected Criterion doValidate(CriterionJsonContext ctx) {

		if(shape == null)
			shape = ctx.getShape();
		
		if(structures != null)
			ctx.error("the structures attribute isn't supported yet");

		if(radius <= 0)
			ctx.error("the radius must be strictly positive (is " + radius + ")");
		
		Set<Biome> biomeSet = getBiomeSet(ctx);
		
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
			return new CriterionInvalid(ctx.getName());
		
		Region region = isSquare ? Region.box(center, radius) : Region.circle(center, radius);

		return new CriterionBiome(ctx.getName(), region, biomeSet, isChecked);
	}
	
	private Set<Biome> getBiomeSet(CriterionJsonContext ctx) {
		Set<Biome> biomeSet = new HashSet<>();
		if(biomes.isEmpty() && structures == null) {
			ctx.error("the biome list can't be empty");
			
		} else {
			for(String biomeName: biomes) {
				if(Biome.exists(biomeName)) {
					Biome b = Biome.getByName(biomeName); 
					if(!biomeSet.add(b))
						ctx.error("duplicate biome " + b.getName());
					
					if(variants && b.isSpecialBiome()) {
						Biome spec = b.getSpecialVariant();
						if(!biomeSet.add(spec))
							ctx.error("duplicate biome " + spec.getName());
					}
								
				} else ctx.error("the biome " + biomeName + " doesn't exist");
			}
		}
		return biomeSet;
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