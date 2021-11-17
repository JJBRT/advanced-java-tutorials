package com.github.jjbrt.iteration;


import static org.burningwave.core.assembler.StaticComponentContainer.IterableObjectHelper;
import static org.burningwave.core.assembler.StaticComponentContainer.ManagedLoggersRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.burningwave.core.iterable.IterableObjectHelper.IterationConfig;


public class ListsIterator {
	
	public static void main(String[] args) {
        try {
    		iterate(buildInput());
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }



	private static Collection<Integer> buildInput() {
		Collection<Integer> input = new ArrayList<>();
		for (int i = 0; i < 10000000; i++) {
			input.add(i + 1);
		}
		return input;
	}
	

	
	private static void iterate(Collection<Integer> input) {
		long initialTime = System.currentTimeMillis();
		List<Integer> output = IterableObjectHelper.createIterateAndGetTask(
			IterationConfig.of(input)
			.parallelIf(inputColl -> inputColl.size() > 2)
			.withOutput(new ArrayList<Integer>())
			.withAction((number, outputCollectionSupplier) -> {
                if (number > input.size() / 2) {
                    //Terminating the current thread iteration early.
                    IterableObjectHelper.terminateCurrentThreadIteration();
                    //If we need to terminate all threads iteration (useful for a find first iteration) we must use
                    //IterableObjectHelper.terminateIteration();
                }
				if ((number % 2) == 0) {						
					outputCollectionSupplier.accept(outputCollection -> 
						outputCollection.add(number)
					);
				}
			})
			
		).submit().join();
		ManagedLoggersRepository.logInfo(
			ListsIterator.class::getName,
			"\n\n\tInput collection size: {}\n\tOutput collection size: {}\n\tTotal elapsed time: {}s\n",
			input.size(),
			output.size(),
			getFormattedDifferenceOfMillis(System.currentTimeMillis(),initialTime)
		);
	}
	
	private static String getFormattedDifferenceOfMillis(long value1, long value2) {
		String valueFormatted = String.format("%04d", (value1 - value2));
		return valueFormatted.substring(0, valueFormatted.length() - 3) + "," + valueFormatted.substring(valueFormatted.length() -3);
	}
	
}
