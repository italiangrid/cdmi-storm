package it.grid.storm.cdmi.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class VOConfiguration {

	private String name;
	private List<String> roots;

	@JsonCreator
	public VOConfiguration(@JsonProperty("name") String name,
			@JsonProperty("roots") List<String> roots) {

		this.name = name;
		this.roots = roots;
	}

	public String getName() {
		return name;
	}

	public List<String> getRoots() {
		return roots;
	}

	@Override
	public String toString() {
		return "VOConfiguration [name=" + name + ", roots=" + roots + "]";
	}
}
