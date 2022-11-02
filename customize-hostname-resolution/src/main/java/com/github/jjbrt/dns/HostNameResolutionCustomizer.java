package com.github.jjbrt.dns;

import static org.burningwave.core.assembler.StaticComponentContainer.ManagedLoggerRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.burningwave.tools.net.DefaultHostResolver;
import org.burningwave.tools.net.HostResolutionRequestInterceptor;
import org.burningwave.tools.net.MappedHostResolver;

public class HostNameResolutionCustomizer {

	public static void main(String[] args) {
        try {
			execute(loadConfiguration());
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
    }

	public static Map<String, String> loadConfiguration() {
		Map<String, String> hostAliases = new LinkedHashMap<>();
		hostAliases.put("my.hostname.one", "123.123.123.123");
		hostAliases.put("my.hostname.two", "123.123.123.123");
		hostAliases.put("my.hostname.three", "12.21.34.43");
		hostAliases.put("my.hostname.four", "12.21.34.43");
		return hostAliases;
	}

    public static void execute(Map<String, String> hostAliases) {

    	//Installing the host resolvers
    	HostResolutionRequestInterceptor.INSTANCE.install(
			new MappedHostResolver(hostAliases),
			//This is the system default resolving wrapper
			DefaultHostResolver.INSTANCE
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
			new MappedHostResolver(hostAliases),
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