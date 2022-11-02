package com.github.jjbrt.dns;

import static org.burningwave.core.assembler.StaticComponentContainer.Driver;
import static org.burningwave.core.assembler.StaticComponentContainer.ManagedLoggerRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.tools.net.DefaultHostResolver;
import org.burningwave.tools.net.HostResolutionRequestInterceptor;
import org.burningwave.tools.net.HostResolver;
import org.burningwave.tools.net.MappedHostResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;


@SuppressWarnings("unchecked")
public class HostNameResolutionWithCustomHostResolverWithYAMLConfiguration {

	public static void main(String[] args) {
        try {
			execute(loadConfiguration("config.yml"));
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }

	public static Map<String, Object> loadConfiguration(String fileNameRelativePathFromClasspath) {
    	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    	try (InputStream inputStream = ComponentContainer.getInstance().getPathHelper().getResourceAsStream(fileNameRelativePathFromClasspath)) {
    		return mapper.readValue(inputStream, Map.class);
    	} catch (IOException exc) {
			return Driver.throwException(exc);
		}
    }

    public static void execute(Map<String, Object> configuration) throws UnknownHostException, IOException {
		List<HostResolver> resolvers = new ArrayList<>();
		resolvers.add(
			new MappedHostResolver(() -> (List<Map<String, Object>>)configuration.get("hostAliases"))
		);
		resolvers.addAll(
			DNSJavaHostResolver.newInstances(() -> (List<Map<String, Object>>)((Map<String, Object>)configuration.get("dns")).get("servers"))
		);

		//This is the system default resolving wrapper
		resolvers.add(DefaultHostResolver.INSTANCE);

		//Installing the host resolvers
    	HostResolutionRequestInterceptor.INSTANCE.install(
			resolvers.toArray(new HostResolver[resolvers.size()])
		);

		printHostInfo("my.hostname.one");
		printHostInfo("my.hostname.two");
		printHostInfo("my.hostname.three");
		printHostInfo("my.hostname.four");
		printHostInfo("google.com");

		//Restoring hostname resolving to default and cleaning the cache
		HostResolutionRequestInterceptor.INSTANCE.uninstall();

		//Now we have reset the hostnames in the map will not be resolved...
		printHostInfo("my.hostname.one");
		printHostInfo("my.hostname.two");
		printHostInfo("my.hostname.three");
		printHostInfo("my.hostname.four");
		//... Except this
		printHostInfo("google.com");

		//Adding host aliases again
		HostResolutionRequestInterceptor.INSTANCE.install(
			resolvers.toArray(new HostResolver[resolvers.size()])
		);

		//Retesting
		printHostInfo("my.hostname.one");
		printHostInfo("my.hostname.two");
		printHostInfo("my.hostname.three");
		printHostInfo("my.hostname.four");
		printHostInfo("google.com");
    }

	public static void printHostInfo(String hostname) {
		try {
			InetAddress inetAddress = InetAddress.getByName(hostname);
			ManagedLoggerRepository.logInfo(HostNameResolutionCustomizer.class::getName, "\n\n\tThe ip of hostname {} is {}\n\n", inetAddress.getHostName(), inetAddress.getHostAddress());
		} catch (UnknownHostException exc) {
			ManagedLoggerRepository.logError(HostNameResolutionCustomizer.class::getName,"\n\n\tUnable to resolve hostname {}\n\n", hostname);
		}
	}

}