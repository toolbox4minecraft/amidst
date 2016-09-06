package amidst.mojangapi.file.json.filter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import amidst.documentation.JsonField;
import amidst.mojangapi.file.world.filter.Criterion;
import amidst.mojangapi.file.world.filter.CriterionBiome;
import amidst.mojangapi.file.world.filter.CriterionInvalid;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Region;

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
	public List<String> structures = Collections.emptyList();
	
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
		
		if(variants)
			ctx.error("the variants attribute isn't supported yet");
		
		if(radius <= 0)
			ctx.error("the radius must be strictly positive (is " + radius + ")");
		
		List<Biome> list = new ArrayList<>();
		if(biomes.isEmpty()) {
			ctx.error("the biome list can't be empty");
			
		} else {
			for(String biomeName: biomes) {
				if(Biome.exists(biomeName))
					list.add(Biome.getByName(biomeName));	
					
				else ctx.error("the biome " + biomeName + " doesn't exist");
			}
		}
		
		boolean isChecked, isSquare;
		
		switch(shape) {
		case "square":
			isChecked = true; isSquare = true; break;
			
		case "circle":
			isChecked = true; isSquare = false; break;
			
		case "square_nocheck":
			isChecked = false; isSquare = true; break;
			
		case "circle_nocheck":
			isChecked = false; isSquare = false; break;
			
		default:
			ctx.error("unknown shape " + shape);
			return new CriterionInvalid(ctx.getName());
		}
		
		if(ctx.hasErrors())
			return new CriterionInvalid(ctx.getName());
		
		Region region = isSquare ? Region.box(center, radius) : Region.circle(center, radius);

		return new CriterionBiome(ctx.getName(), region, list, isChecked);
	}
	
	public static class ClusterInfoDeserializer implements JsonDeserializer<ClusterInfo> {
		@Override
		public ClusterInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if(json.isJsonPrimitive()) {
				JsonObject obj = new JsonObject();
				obj.add("size", json);
			}
			
			return context.deserialize(json, ClusterInfo.class);
		}
		
	}
}