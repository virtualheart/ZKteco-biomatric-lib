package com.zkteco.iclockhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestUtils {

//    	query = ?SN=0000000000000&options=all&language=69&pushver=2.4.1;
        public static String[] extractSnVersion(ParsedRequest parsedRequest) {
            String query = parsedRequest.getParseresult().getQuery();
            String[] parts = query.split("[&=]");

            // Extracting SN and version
            String sn = null;
            String pushver = null;

            for (int i = 0; i < parts.length; i += 2) {
                if ("SN".equals(parts[i])) {
                    sn = parts[i + 1];
                } else if ("pushver".equals(parts[i])) {
                    pushver = parts[i + 1];
                }
            }

            System.out.println("SN: " + sn);
            System.out.println("Version: " + pushver);
            return new String[]{sn, pushver};
        }

    
    static Map<String, Object> buffertoMaps(BufferedReader br) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            Map<String, Object> d = parseLine(line);
            result.putAll(d);
        }
        return result.isEmpty() ? null : result;
    }

    static Map<String, Object> StringtoMaps(String uri) throws IOException {
    	String[] parts = uri.split("[&=]");
    	Map<String, Object> d = new HashMap<>();

    	for (int i = 0; i < parts.length; i += 2) {
    	    String key = parts[i];
    	    String value = (i + 1 < parts.length) ? parts[i + 1] : null;
    	    d.put(key, value);
    	}
    	
    	return d;
    }


    
    private static Map<String, Object> parseLine(String line) {
        Map<String, Object> map = new HashMap<>();
        return map;
    }


	}

