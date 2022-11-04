package com.github.jjbrt.dns;

import static org.burningwave.core.assembler.StaticComponentContainer.Driver;
import static org.burningwave.core.assembler.StaticComponentContainer.Strings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.burningwave.tools.net.DNSClientHostResolver;
import org.burningwave.tools.net.HostResolver;
import org.burningwave.tools.net.IPAddressUtil;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.lookup.LookupResult;
import org.xbill.DNS.lookup.LookupSession;

@SuppressWarnings("unchecked")
public class DNSJavaHostResolver implements HostResolver {
	private static final int[] DEFAULT_IP_TYPE_TO_SEARCH_FOR;

	static {
		DEFAULT_IP_TYPE_TO_SEARCH_FOR = new int[] {Type.A, Type.AAAA};
	}

	private LookupSession lookupSession;
	private int[] ipTypeToSearchFor;

	public DNSJavaHostResolver(String dNSServerIP) {
		this(dNSServerIP, DNSClientHostResolver.DEFAULT_PORT, DEFAULT_IP_TYPE_TO_SEARCH_FOR);
	}

	public DNSJavaHostResolver(String dNSServerIP, int port, int... ipTypes) {
		try {
			Resolver resolver = new SimpleResolver(InetAddress.getByName(dNSServerIP));
			resolver.setPort(port);
			lookupSession = LookupSession.builder().resolver(
				resolver
			).build();
			ipTypeToSearchFor = ipTypes != null && ipTypes.length > 0 ?
				ipTypes : new int[] {Type.A, Type.AAAA};
		} catch (UnknownHostException exc) {
			Driver.throwException(exc);
		}
	}


	public static Collection<DNSJavaHostResolver> newInstances(Supplier<Collection<Map<String, Object>>> configuration) {
		Collection<DNSJavaHostResolver> dNSClientHostResolvers = new ArrayList<>();
		configuration.get().stream().forEach(serverMap -> {
			dNSClientHostResolvers.add(
	            new DNSJavaHostResolver(
	                (String)serverMap.get("ip"),
	                (Integer)serverMap.getOrDefault("port", DNSClientHostResolver.DEFAULT_PORT),
	                ((List<String>)serverMap.get("ipTypeToSearchFor")).stream()
	    			.mapToInt(iptype -> {
	    				if (iptype.toUpperCase().equals("IPV4")) {
	    					return Type.A;
	    				} else if (iptype.toUpperCase().equals("IPV6")) {
	    					return Type.AAAA;
	    				}
	    				throw new IllegalArgumentException(Strings.compile("{} is not a valid ip type", iptype));
	    			}).toArray()
	            )
	        );
		});
		return dNSClientHostResolvers;
	}

	@Override
	public Collection<InetAddress> getAllAddressesForHostName(Map<String, Object> argumentMap) {
		return getAllAddressesForHostName((String)getMethodArguments(argumentMap)[0]);
	}

	public Collection<InetAddress> getAllAddressesForHostName(String hostName) {
		Collection<InetAddress> hostInfos = new ArrayList<>();
		findAndProcessHostInfos(
			() -> {
				try {
					return Name.fromString(hostName.endsWith(".") ? hostName : hostName + ".");
				} catch (TextParseException exc) {
					return Driver.throwException(exc);
				}
			},
			record -> {
				if (record instanceof ARecord) {
					hostInfos.add(((ARecord)record).getAddress());
				} else if (record instanceof AAAARecord) {
					hostInfos.add(((AAAARecord)record).getAddress());
				}
			},
			ipTypeToSearchFor
		);
		return hostInfos;
	}


	@Override
	public Collection<String> getAllHostNamesForHostAddress(Map<String, Object> argumentMap) {
		return getAllHostNamesForHostAddress((byte[])getMethodArguments(argumentMap)[0]);
	}

	public Collection<String> getAllHostNamesForHostAddress(String iPAddress) {
		return getAllHostNamesForHostAddress(IPAddressUtil.INSTANCE.textToNumericFormat(iPAddress));
	}

	public Collection<String> getAllHostNamesForHostAddress(byte[] address) {
		Collection<String> hostNames = new ArrayList<>();
		findAndProcessHostInfos(
			() ->
				ReverseMap.fromAddress(address),
			record ->
				hostNames.add(((PTRRecord)record).getTarget().toString(true)),
			Type.PTR
		);
		return hostNames;
	}

	private void findAndProcessHostInfos(
		Supplier<Name> nameSupplier,
		Consumer<Record> recordProcessor,
		int... types
	) {
    	Collection<CompletableFuture<LookupResult>> hostInfoRetrievers = new ArrayList<>();
    	for (int type : types) {
    		hostInfoRetrievers.add(
				lookupSession.lookupAsync(nameSupplier.get(), type).toCompletableFuture()
			);
    	}
    	hostInfoRetrievers.stream().forEach(hostNamesRetriever -> {
    		try {
    			List<Record> records = hostNamesRetriever.join().getRecords();
    			if (records != null) {
    				for (Record record : records) {
    					recordProcessor.accept(record);
    				}
    			}
    		} catch (Throwable exc) {

    		}
    	});
	}

}
