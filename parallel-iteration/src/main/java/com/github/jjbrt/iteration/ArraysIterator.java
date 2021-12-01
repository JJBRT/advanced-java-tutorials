package com.github.jjbrt.iteration;

import static org.burningwave.core.assembler.StaticComponentContainer.IterableObjectHelper;
import static org.burningwave.core.assembler.StaticComponentContainer.ManagedLoggerRepository;

import java.util.ArrayList;
import java.util.List;

import org.burningwave.core.iterable.IterableObjectHelper.IterationConfig;

public class ArraysIterator {
	
	public static void main(String[] args) {
        try {
    		iterate(buildInputCollection());
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }



	private static Object[] buildInputCollection() {
		Object[] input = new Object[100000000];
		for (int i = 0; i < input.length; i++) {
			input[i] = Integer.valueOf(i + 1);
		}
		return input;
	}
	

	
	private static void iterate(Object[] inputCollection) {
		long initialTime = System.currentTimeMillis();
		List<Long> outputCollection = IterableObjectHelper.createIterateAndGetTask(
			IterationConfig.of(inputCollection)
			.parallelIf(inputColl -> inputColl.length > 2)
			.withOutput(new ArrayList<Long>())
			.withAction((number, outputCollectionSupplier) -> {
				int intNumber = (int)number;
                if ((int)number <= inputCollection.length - 10) {
                    //Skipping iterated item
                	return;
                }
				if (((int)number % 2) == 0) {						
					outputCollectionSupplier.accept(outputColl -> 
						outputColl.add((Long.valueOf(intNumber) * intNumber))
					);
				}
			})
			
		).submit().join();
        IterableObjectHelper.iterate(
            IterationConfig.of(outputCollection)
            //Disabling parallel iteration
            .parallelIf(inputColl -> false)
            .withAction((number) -> {
                ManagedLoggerRepository.logInfo(ArraysIterator.class::getName, "Iterated number: {}", number);
            })    
        );
        
        ManagedLoggerRepository.logInfo(
			ListsIterator.class::getName,
			"\n\n\tInput collection size: {}\n\tOutput collection size: {}\n\tTotal elapsed time: {}s\n",
			inputCollection.length,
			outputCollection.size(),
			getFormattedDifferenceOfMillis(System.currentTimeMillis(),initialTime)
		);
	}

	
	private static String getFormattedDifferenceOfMillis(long value1, long value2) {
		String valueFormatted = String.format("%04d", (value1 - value2));
		return valueFormatted.substring(0, valueFormatted.length() - 3) + "," + valueFormatted.substring(valueFormatted.length() -3);
	}
	
}
