package amidst.filter.json;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonParseException;

import java.util.Collections;

public class WorldFilterParseException extends Exception {

	private static final long serialVersionUID = 631895046392283086L;
	
	private List<String> errors;

	public WorldFilterParseException(JsonParseException cause) {
		super(cause);
		errors = Collections.emptyList();
	}
	
	public WorldFilterParseException(List<String> errors) {
		super("Failed to parse json with " + errors.size() + " error(s):");
		this.errors = errors;
	}
	
	public List<String> getErrorList() {
		return errors;
	}
	
	@Override
	public String getMessage() {
		return super.getMessage() + "\n" + errors.stream().collect(Collectors.joining("\n"));
	}
}
