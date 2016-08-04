package amidst.mojangapi.file.json.filter;

import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.WorldFilter;

@Immutable
public class WorldFilterJson {
	
	@GsonConstructor
	public WorldFilterJson() {
	}
	
	//TODO
	
	public Optional<WorldFilter> fromJSON(String jsonString) {
		return Optional.empty();
	}
}