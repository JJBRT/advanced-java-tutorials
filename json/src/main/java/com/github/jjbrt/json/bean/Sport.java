package com.github.jjbrt.json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sport {
	Question q1;

	@JsonProperty("q1")
	public Question getQ1() {
		return this.q1;
	}

	public void setQ1(Question q1) {
		this.q1 = q1;
	}
}
