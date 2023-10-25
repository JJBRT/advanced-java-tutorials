package com.github.jjbrt.json;

import java.util.Map;

import org.burningwave.json.Facade;
import org.burningwave.json.ObjectHandler;
import org.burningwave.json.Path;

import com.github.jjbrt.json.bean.Question;
import com.github.jjbrt.json.bean.Root;
import com.github.jjbrt.json.bean.Sport;


class ObjectHandlerExample {
	static final Facade facade = Facade.create();

	public static void main(String[] args) {
        try {
        	findFirstWithFinder();
        	findFirstWithValueFinder();
        	findFirstWithValueAndConvert();
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }

	static void findFirstWithFinder() {
		//Loading the JSON object
		ObjectHandler objectHandler = facade.newObjectHandler(
			ObjectHandlerExample.class.getClassLoader().getResourceAsStream("quiz.json"),
			Root.class
		);

		ObjectHandler.Finder finder = objectHandler.newFinder();
		ObjectHandler sportOH = finder.findFirstForPathEndsWith("sport");
		//Retrieving the path of the sport object ("quiz.sport")
		String sportPath = sportOH.getPath();
		//Retrieving the value of the sport object
		Sport sport = sportOH.getValue();
		ObjectHandler option2OfSportQuestionOH = finder.findFirstForPathEndsWith(Path.of("sport", "q1", "options[1]"));
		String option2OfSportQuestionOHPath = option2OfSportQuestionOH.getPath();
		String option2OfSportQuestion = option2OfSportQuestionOH.getValue();
		ObjectHandler questionOneOH = finder.findForPathEquals(Path.of("quiz", "sport", "q1"));
		String questionOnePath = questionOneOH.getPath();
		Question questionOne = questionOneOH.getValue();
	}

	static void findFirstWithValueFinder() {
		//Loading the JSON object
		ObjectHandler objectHandler = facade.newObjectHandler(
			ObjectHandlerExample.class.getClassLoader().getResourceAsStream("quiz.json"),
			Root.class
		);

		ObjectHandler.ValueFinder finder = objectHandler.newValueFinder();
		Sport sport = finder.findFirstForPathEndsWith("sport");
		String option2OfSportQuestion = finder.findFirstForPathEndsWith(Path.of("sport", "q1", "options[1]"));
		Question questionOne = finder.findForPathEquals(Path.of("quiz", "sport", "q1"));
	}

	static void findFirstWithValueAndConvert() {
		//Loading the JSON object
		ObjectHandler objectHandler = facade.newObjectHandler(
			ObjectHandlerExample.class.getClassLoader().getResourceAsStream("quiz.json"),
			Root.class
		);

		ObjectHandler.ValueFinderAndConverter finderAndConverter = objectHandler.newValueFinderAndConverter(Map.class);
		Map<String, Object> sportAsMap = finderAndConverter.findFirstForPathEndsWith("sport");

	}

}
