package com.github.jjbrt.json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Maths {
	Question q1;
	Question q2;

	@JsonProperty("q1")
	public Question getQ1() {
		return this.q1;
	}

	@JsonProperty("q2")
	public Question getQ2() {
		return this.q2;
	}

	public void setQ1(Question q1) {
		this.q1 = q1;
	}

	public void setQ2(Question q2) {
		this.q2 = q2;
	}
}
