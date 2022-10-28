package com.github.jjbrt.reflection;

import static org.burningwave.core.assembler.StaticComponentContainer.Driver;
import static org.burningwave.core.assembler.StaticComponentContainer.ManagedLoggerRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.tools.dns.DefaultHostResolver;
import org.burningwave.tools.dns.HostResolverService;
import org.burningwave.tools.dns.MappedHostResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class HostNameResolutionFromYAMLCustomizer {

	public static void main(String[] args) {
        try {
			execute(() -> loadConfiguration("hosts.yml"));
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }

    @SuppressWarnings("unchecked")
	public static List<Map<String, Object>> loadConfiguration(String fileName) {
    	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    	try (InputStream inputStream = ComponentContainer.getInstance().getPathHelper().getResourceAsStream("hosts.yml")) {
    		return (List<Map<String, Object>>)mapper.readValue(inputStream, Map.class).get("hostAliases");
    	} catch (IOException exc) {
			return Driver.throwException(exc);
		}
    }

    public static void execute(Supplier<List<Map<String, Object>>> hostAliasesSupplier) {

    	//Installing the host resolvers
		HostResolverService.INSTANCE.install(
			new MappedHostResolver(hostAliasesSupplier),
			//This is the system default resolving wrapper
			DefaultHostResolver.INSTANCE
		);

		printHostInfo("my.hostname.one");
		printHostInfo("my.hostname.two");
		printHostInfo("my.hostname.three");
		printHostInfo("my.hostname.four");
		printHostInfo("google.com");

		//Restoring hostname resolving to default and cleaning the cache
		HostResolverService.INSTANCE.reset();

		//Now we have reset the hostnames in the map will not be resolved...
		printHostInfo("my.hostname.one");
		printHostInfo("my.hostname.two");
		printHostInfo("my.hostname.three");
		printHostInfo("my.hostname.four");
		//... Except this
		printHostInfo("google.com");

		//Adding host aliases again
		HostResolverService.INSTANCE.install(
			new MappedHostResolver(hostAliasesSupplier),
			DefaultHostResolver.INSTANCE
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