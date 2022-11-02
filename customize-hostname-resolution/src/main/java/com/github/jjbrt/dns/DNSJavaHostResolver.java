package com.github.jjbrt.dns;

import static org.burningwave.core.assembler.StaticComponentContainer.Driver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.burningwave.tools.net.HostResolver;
import org.burningwave.tools.net.IPAddressUtil;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.lookup.LookupResult;
import org.xbill.DNS.lookup.LookupSession;

public class DNSJavaHostResolver implements HostResolver {

	private LookupSession lookupSession;

	public DNSJavaHostResolver(String dNSServerIP) {
		try {
			lookupSession = LookupSession.builder().resolver(
				new SimpleResolver(InetAddress.getByName(dNSServerIP))
			).build();
		} catch (UnknownHostException exc) {
			Driver.throwException(exc);
		}
	}

	@Override
	public Collection<InetAddress> getAllAddressesForHostName(Map<String, Object> argumentsMap) {
		return getAllAddressesForHostName((String)getMethodArguments(argumentsMap)[0]);
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
			Type.A, Type.AAAA
		);
		return hostInfos;
	}


	@Override
	public Collection<String> getAllHostNamesForHostAddress(Map<String, Object> argumentsMap) {
		return getAllHostNamesForHostAddress((byte[])getMethodArguments(argumentsMap)[0]);
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
