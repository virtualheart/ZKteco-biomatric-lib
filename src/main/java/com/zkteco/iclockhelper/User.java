package com.zkteco.iclockhelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class User {
    private final String pin;
    private final String name;
    private final String password;
    private final String card;
    private final String group;
    private final String tz;
    private final String privileges;
    private final String raw;
    private final String verify;
    private final String viceCard;

    public User(String pin, String name, String password, String card, String group, String tz, String privileges, String raw, String verify, String viceCard) {
        this.pin = pin;
        this.name = name;
        this.password = password;
        this.card = card;
        this.group = group;
        this.tz = tz;
        this.privileges = privileges;
        this.raw = raw;
        this.verify = verify;
        this.viceCard = viceCard;
    }

    public String getPin() {
        return pin;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getCard() {
        return card;
    }

    public String getGroup() {
        return group;
    }

    public String getTz() {
        return tz;
    }

    public String getPrivileges() {
        return privileges;
    }

    public String getRaw() {
        return raw;
    }

    public String getVerify() {
        return verify;
    }

    public String getViceCard() {
        return viceCard;
    }


	public static User fromStr1(String line) throws IllegalAccessException, InstantiationException  {
        Map<String, String> flds = buildDict(line);
        return fillDaFromMapping(User.class, UserDataFields.getMappedField(line), flds);
    }

    private static Map<String, String> buildDict(String line) {
        // Implement logic to parse the line and build a dictionary
        // (You can use a library like Jackson or Gson for JSON parsing in a real-world scenario)
        return new HashMap<>();
    }

    private static <T> User fillDaFromMapping(Class<User> class1, String string, Map<String, String> kwargs) throws IllegalAccessException, InstantiationException {
        Map<String, String> modelData = new HashMap<>();
        Field[] fields = class1.getDeclaredFields();

        for (Map.Entry<String, String> entry : kwargs.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            String normalKey = StringUtil.toSnakeCase(key);

            if (string.contains(key)) {
//                normalKey = string.get(key);
            }

            for (Field field : fields) {
                if (field.getName().equals(normalKey)) {
                    field.setAccessible(true);
                    field.set(class1, val);
                }
            }
        }

        return class1.newInstance();
    }
}
