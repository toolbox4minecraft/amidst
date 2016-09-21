package amidst.mojangapi.file.world.filter;

import java.util.Set;

import amidst.mojangapi.world.coordinates.Region;

//This class is used to represent a criterion which couldn't be parsed
public class CriterionInvalid implements Criterion {

	private String name;
	
	private static String ERROR_MSG = "This class doesn't support any operations";
	
	public CriterionInvalid(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<Region> getBiomeRegionsNeeded() {
		throw new UnsupportedOperationException(ERROR_MSG);
	}

}
