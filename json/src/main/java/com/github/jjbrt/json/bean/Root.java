package com.github.jjbrt.json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Root{
    Quiz quiz;
    @JsonProperty("quiz")
    public Quiz getQuiz() {
		 return this.quiz; }

		public void setQuiz(Quiz quiz) {
			this.quiz = quiz;
		}
}
