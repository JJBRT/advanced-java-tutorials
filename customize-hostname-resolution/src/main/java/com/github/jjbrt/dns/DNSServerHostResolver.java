/*
 * This file is part of Burningwave Tools.
 *
 * Author: Roberto Gentili
 *
 * Hosted at: https://github.com/burningwave/tools
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2022 Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jjbrt.dns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.burningwave.tools.dns.HostResolver;
import org.burningwave.tools.dns.IPAddressUtil;

public class DNSServerHostResolver implements HostResolver {
	private static Random requestIdGenerator;

	static {
		requestIdGenerator = new Random();
	}

	private InetAddress dNSServerIP;
	private int dNSServerPort;

	public DNSServerHostResolver(String dNSServerIP, int dNSServerPort) throws UnknownHostException {
		this.dNSServerIP = InetAddress.getByName(dNSServerIP);
		this.dNSServerPort = dNSServerPort;
	}

	@Override
	public Collection<InetAddress> getAllAddressesForHostName(Map<String, Object> argumentsMap) {
		return resolveHostForName((String)getMethodArguments(argumentsMap)[0]);
	}

	public Collection<InetAddress> resolveHostForName(String hostName) {
		try {
			Collection<InetAddress> addresses = new ArrayList<>();
			byte[] response = sendRequest(hostName);
	        Map<String, String> iPToDomainMap = parseResponse(response);
	        for (Map.Entry<String, String> iPToDomain : iPToDomainMap.entrySet()) {
	        	addresses.add(InetAddress.getByAddress(iPToDomain.getValue(), IPAddressUtil.INSTANCE.textToNumericFormat(iPToDomain.getKey())));
	        }
	        return addresses;
		} catch (Throwable exc) {
			return sneakyThrow(exc);
		}
	}

	private byte[] sendRequest(String hostName) throws IOException, SocketException {
		short ID = (short)requestIdGenerator.nextInt(32767);
		try (
			ByteArrayOutputStream requestContentStream = new ByteArrayOutputStream();
			DataOutputStream requestWrapper = new DataOutputStream(requestContentStream);
		) {
			short requestFlags = Short.parseShort("0000000100000000", 2);
			ByteBuffer byteBuffer = ByteBuffer.allocate(2).putShort(requestFlags);
			byte[] flagsByteArray = byteBuffer.array();

			short QDCOUNT = 1;
			short ANCOUNT = 0;
			short NSCOUNT = 0;
			short ARCOUNT = 0;

			requestWrapper.writeShort(ID);
			requestWrapper.write(flagsByteArray);
			requestWrapper.writeShort(QDCOUNT);
			requestWrapper.writeShort(ANCOUNT);
			requestWrapper.writeShort(NSCOUNT);
			requestWrapper.writeShort(ARCOUNT);

			String[] domainParts = hostName.split("\\.");

			for (int i = 0; i < domainParts.length; i++) {
			    byte[] domainBytes = domainParts[i].getBytes(StandardCharsets.UTF_8);
			    requestWrapper.writeByte(domainBytes.length);
			    requestWrapper.write(domainBytes);
			}
			requestWrapper.writeByte(0);
			requestWrapper.writeShort(1);
			requestWrapper.writeShort(1);
			byte[] dnsFrame = requestContentStream.toByteArray();
			DatagramPacket packet;
			byte[] response;
			try (DatagramSocket socket = new DatagramSocket()){
			    DatagramPacket dnsReqPacket = new DatagramPacket(dnsFrame, dnsFrame.length, dNSServerIP, dNSServerPort);
			    socket.send(dnsReqPacket);
			    response = new byte[1024];
			    packet = new DatagramPacket(response, response.length);
			    socket.receive(packet);
			}
			return response;
		}
	}

	private Map<String, String> parseResponse(byte[] responseContent) throws IOException {
		try (InputStream responseContentStream = new ByteArrayInputStream(responseContent);
			DataInputStream responseWrapper = new DataInputStream(responseContentStream)
		) {
			responseWrapper.skip(6);
			short ANCOUNT = responseWrapper.readShort();
			responseWrapper.skip(4);
			int recLen;
			while ((recLen = responseWrapper.readByte()) > 0) {
			    byte[] record = new byte[recLen];
			    for (int i = 0; i < recLen; i++) {
			        record[i] = responseWrapper.readByte();
			    }
			}
			responseWrapper.skip(4);
			byte firstBytes = responseWrapper.readByte();
			int firstTwoBits = (firstBytes & 0b11000000) >>> 6;
			Map<String, String> ipToDomainMap = new HashMap<>();
			try (ByteArrayOutputStream label = new ByteArrayOutputStream();) {
				for(int i = 0; i < ANCOUNT; i++) {
				    if(firstTwoBits == 3) {
				        byte currentByte = responseWrapper.readByte();
				        boolean stop = false;
				        byte[] newArray = Arrays.copyOfRange(responseContent, currentByte, responseContent.length);
				        try (InputStream responseSectionContentStream = new ByteArrayInputStream(newArray);
			        		DataInputStream responseSectionWrapper = new DataInputStream(responseSectionContentStream);
		        		) {
					        ArrayList<Integer> RDATA = new ArrayList<>();
					        ArrayList<String> DOMAINS = new ArrayList<>();
					        while(!stop) {
					            byte nextByte = responseSectionWrapper.readByte();
					            if(nextByte != 0) {
					                byte[] currentLabel = new byte[nextByte];
					                for(int j = 0; j < nextByte; j++) {
					                    currentLabel[j] = responseSectionWrapper.readByte();
					                }
					                label.write(currentLabel);
					            } else {
					                stop = true;
					                responseWrapper.skip(8);
					                int RDLENGTH = responseWrapper.readShort();
					                for(int s = 0; s < RDLENGTH; s++) {
					                    int nx = responseWrapper.readByte() & 255;
					                    RDATA.add(nx);
					                }
					            }
					            DOMAINS.add(new String( label.toByteArray(), StandardCharsets.UTF_8));
					            label.reset();
					        }

					        StringBuilder ip = new StringBuilder();
					        StringBuilder domainSb = new StringBuilder();
					        for(Integer ipPart:RDATA) {
					            ip.append(ipPart).append(".");
					        }

					        for(String domainPart:DOMAINS) {
					            if(!domainPart.equals("")) {
					                domainSb.append(domainPart).append(".");
					            }
					        }
					        String domainFinal = domainSb.toString();
					        String ipFinal = ip.toString();
					        ipToDomainMap.put(ipFinal.substring(0, ipFinal.length()-1), domainFinal.substring(0, domainFinal.length()-1));
				        }
				    }

				    firstBytes = responseWrapper.readByte();
				    firstTwoBits = (firstBytes & 0b11000000) >>> 6;
				}
			}
			return ipToDomainMap;
		}
	}

	@Override
	public Collection<String> getAllHostNamesForHostAddress(Map<String, Object> argumentsMap) {
		byte[] addressAsByteArray = (byte[])getMethodArguments(argumentsMap)[0];
		String iPAddress = IPAddressUtil.INSTANCE.numericToTextFormat(addressAsByteArray);
		//To be implemented
		return sneakyThrow(new UnknownHostException(iPAddress));
	}

    private <T> T sneakyThrow(Throwable exc) {
        throwException(exc);
        return null;
    }


    private <E extends Throwable> void throwException(Throwable exc) throws E {
        throw (E)exc;
    }

}
