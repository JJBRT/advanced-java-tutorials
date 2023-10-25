package com.github.jjbrt.json;

import java.util.Collection;

import org.burningwave.json.Check;
import org.burningwave.json.Facade;
import org.burningwave.json.ObjectHandler;
import org.burningwave.json.Validation;

import com.github.jjbrt.json.bean.Root;

public class ValidatorExample {

	static final Facade facade = Facade.create();

	public static void main(String[] args) {
        try {
        	validate();
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }

	static void validate() {
		facade.validator().registerCheck(
			//Checking whether a value in any field marked as required (e.g.: @JsonProperty(value = "answer", required = true)) is null
			Check.forAll().checkMandatory(),
			//Checking whether a string value in any field is empty
			Check.forAllStringValues().execute(pathValidationContext -> {
				if (pathValidationContext.getValue() != null && pathValidationContext.getValue().trim().equals("")) {
					pathValidationContext.rejectValue("IS_EMPTY", "is empty");
				}
			})
		);

		//Loading the JSON object
		ObjectHandler objectHandler = facade.newObjectHandler(
			ValidatorExample.class.getClassLoader().getResourceAsStream("quiz.json"),
			Root.class
		);
		Collection<Throwable> exceptions = facade.validator().validate(
			Validation.Config.forJsonObject(objectHandler.getValue())
			//By calling this method the validation will be performed on the entire document,
			//otherwise the validation will stop at the first exception thrown
			.withCompleteValidation()
		);
		for (Throwable exc : exceptions) {
			System.err.println(exc.getMessage());
		};

	}

}
