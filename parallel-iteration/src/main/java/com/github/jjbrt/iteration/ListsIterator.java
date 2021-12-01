package com.github.jjbrt.iteration;


import static org.burningwave.core.assembler.StaticComponentContainer.IterableObjectHelper;
import static org.burningwave.core.assembler.StaticComponentContainer.ManagedLoggerRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.burningwave.core.iterable.IterableObjectHelper.IterationConfig;


public class ListsIterator {
	
	public static void main(String[] args) {
        try {
    		iterate(buildInputCollection());
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }



	private static Collection<Integer> buildInputCollection() {
		return IntStream.rangeClosed(1, 1000000).boxed().collect(Collectors.toList());
	}
	

	
	private static void iterate(Collection<Integer> inputCollection) {
		long initialTime = System.currentTimeMillis();
		List<Integer> outputCollection = IterableObjectHelper.createIterateAndGetTask(
			IterationConfig.of(inputCollection)
			.parallelIf(inputColl -> inputColl.size() > 2)
			.withOutput(new ArrayList<Integer>())
			.withAction((number, outputCollectionSupplier) -> {
                if (number > inputCollection.size() / 2) {
                    //Terminating the current thread iteration early.
                    IterableObjectHelper.terminateCurrentThreadIteration();
                    //If we need to terminate all threads iteration (useful for a find first iteration) we must use
                    //IterableObjectHelper.terminateIteration();
                }
				if ((number % 2) == 0) {						
					outputCollectionSupplier.accept(outputColl -> 
						outputColl.add(number)
					);
				}
			})
			
		).submit().join();
		ManagedLoggerRepository.logInfo(
			ListsIterator.class::getName,
			"\n\n\tInput collection size: {}\n\tOutput collection size: {}\n\tTotal elapsed time: {}s\n",
			inputCollection.size(),
			outputCollection.size(),
			getFormattedDifferenceOfMillis(System.currentTimeMillis(),initialTime)
		);
	}
	
	private static String getFormattedDifferenceOfMillis(long value1, long value2) {
		String valueFormatted = String.format("%04d", (value1 - value2));
		return valueFormatted.substring(0, valueFormatted.length() - 3) + "," + valueFormatted.substring(valueFormatted.length() -3);
	}
	
}
