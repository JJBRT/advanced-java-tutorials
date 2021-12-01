package org.springbootappadapter;

import static org.burningwave.core.assembler.StaticComponentContainer.Driver;
import static org.burningwave.core.assembler.StaticComponentContainer.JVMInfo;
import static org.burningwave.core.assembler.StaticComponentContainer.ManagedLoggerRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.io.FileSystemItem;
import org.burningwave.core.io.PathHelper;
import org.burningwave.tools.dependencies.Capturer.Result;
import org.burningwave.tools.dependencies.TwoPassCapturer;

public class DependenciesAdapter {
	
	
	public static void main(String[] args) throws Exception {
		long initialTime = System.currentTimeMillis();
		ComponentContainer componentContainer = ComponentContainer.getInstance();
		PathHelper pathHelper = componentContainer.getPathHelper();
		Collection<String> paths = pathHelper.getAllMainClassPaths();
		if (JVMInfo.getVersion() > 8) {
			String jdkHome = componentContainer.getConfigProperty("paths.jdk-home");
			if (jdkHome == null || !FileSystemItem.ofPath(jdkHome).exists()) {
				String errorMessage = "\"{}\" is not a valid jdk home path: please provide a correct jdk home in the property 'paths.jdk-home' inside the file \"{}\"";
				Object[] errorMessageParams = new Object[] {
					componentContainer.getConfigProperty("paths.jdk-home"),
					pathHelper.getAbsolutePathOfResource("../../../spring-boot-application-adapter/src/test/resources/burningwave.properties")
				};
				ManagedLoggerRepository.logError(
					() -> DependenciesAdapter.class.getName(),
					errorMessage, errorMessageParams
					
				);
				Driver.throwException(errorMessage, errorMessageParams);
			}
			paths.addAll(pathHelper.getPaths("dependencies-capturer.additional-resources-path"));
		}
		List<String> _paths = new ArrayList<>(paths);
		Collections.sort(_paths);
		Result result = TwoPassCapturer.getInstance().captureAndStore(
				"com.springbootappadapter.SpringBootWebApplication",
				args,
				paths,
				System.getProperty("user.home") + "/Desktop/dependencies",
				true,
				0L
		);
		result.waitForTaskEnding();
		ManagedLoggerRepository.logInfo(() -> DependenciesAdapter.class.getName(), "Elapsed time: " + getFormattedDifferenceOfMillis(System.currentTimeMillis(), initialTime));
	}
	
	private static String getFormattedDifferenceOfMillis(long value1, long value2) {
		String valueFormatted = String.format("%04d", (value1 - value2));
		return valueFormatted.substring(0, valueFormatted.length() - 3) + "," + valueFormatted.substring(valueFormatted.length() -3);
	}

}