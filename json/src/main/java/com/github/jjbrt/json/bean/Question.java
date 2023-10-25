package com.github.jjbrt.json.bean;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Question {
	String answer;
	List<String> options;
	String text;

	@JsonProperty(value = "answer", required = true)
	public String getAnswer() {
		return this.answer;
	}

	@JsonProperty(value = "options", required = true)
	public List<String> getOptions() {
		return this.options;
	}

	@JsonProperty(value = "text", required = true)
	public String getQuestion() {
		return this.text;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	public void setQuestion(String question) {
		this.text = question;
	}
}
