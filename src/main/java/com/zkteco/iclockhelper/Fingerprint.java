package com.zkteco.iclockhelper;

import java.util.HashMap;
import java.util.Map;

public class Fingerprint {
    private final String pin;
    private final String fid;
    private final String tmp;
    private final String raw;

    public Fingerprint(String pin, String fid, String tmp, String raw) {
        this.pin = pin;
        this.fid = fid;
        this.tmp = tmp;
        this.raw = raw;
    }

    public String getPin() {
        return pin;
    }

    public String getFid() {
        return fid;
    }

    public String getTmp() {
        return tmp;
    }

    public String getRaw() {
        return raw;
    }

    public static Fingerprint fromStr(String line) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        Map<String, String> flds = buildDict(line);
        Map _fingerprintFieldsMap = null;
		return fillDaFromMapping(Fingerprint.class, _fingerprintFieldsMap, flds);
    }

    private static Map<String, String> buildDict(String line) {
        // Implement logic to parse the line and build a dictionary
        // (You can use a library like Jackson or Gson for JSON parsing in a real-world scenario)
        return new HashMap<>();
    }

    private static <T> T fillDaFromMapping(Class<T> modelType, Map<String, String> mapping, Map<String, String> kwargs) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        Map<String, String> modelData = new HashMap<>();
        java.lang.reflect.Field[] fields = modelType.getDeclaredFields();

        for (Map.Entry<String, String> entry : kwargs.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            String normalKey = StringUtil.toSnakeCase(key);

            if (mapping.containsKey(key)) {
                normalKey = mapping.get(key);
            }

            for (java.lang.reflect.Field field : fields) {
                if (field.getName().equals(normalKey)) {
                    field.setAccessible(true);
                    field.set(modelType, val);
                }
            }
        }

        return modelType.newInstance();
    }
}