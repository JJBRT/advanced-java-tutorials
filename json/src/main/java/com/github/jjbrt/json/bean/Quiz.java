package com.github.jjbrt.json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quiz {
	Maths maths;

	Sport sport;

	@JsonProperty("maths")
	public Maths getMaths() {
		return this.maths;
	}

	@JsonProperty("sport")
	public Sport getSport() {
		return this.sport;
	}

	public void setMaths(Maths maths) {
		this.maths = maths;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}
}
