package com.zkteco.iclockhelper;

import java.util.HashMap;
import java.util.Map;

public class UserDataFields {
    private static final Map<String, String> USER_FIELDS_MAP = new HashMap<>();

    static {
        USER_FIELDS_MAP.put("PIN", "pin");
        USER_FIELDS_MAP.put("Passwd", "password");
        USER_FIELDS_MAP.put("Card", "card");
        USER_FIELDS_MAP.put("Grp", "group");
        USER_FIELDS_MAP.put("TZ", "tz");
        USER_FIELDS_MAP.put("Pri", "privileges");
        USER_FIELDS_MAP.put("Verify", "verify");
        USER_FIELDS_MAP.put("ViceCard", "vice_card");
    }

    public static String getMappedField(String key) {
        return USER_FIELDS_MAP.getOrDefault(key, "");
    }
}
