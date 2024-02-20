package com.zkteco.iclockhelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.zkteco.Enum.AlarmEnum;
import com.zkteco.Enum.OperationEnum;

public class Operation extends ServerDatetimeMixin {
    private final String object;
    private final String param1;
    private final String param2;
    private final String param3;
    private final String raw;
    private final OperationEnum operation;
    private final String admin;
    private final AlarmEnum alarm;

   public Operation(String object, String param1, String param2, String param3, String raw, OperationEnum operation, String admin, AlarmEnum alarm) {
       super(null);  
	   this.object = object;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.raw = raw;
        this.operation = operation;
        this.admin = admin;
        this.alarm = alarm;
    }

    public String getObject() {
        return object;
    }

    public String getParam1() {
        return param1;
    }

    public String getParam2() {
        return param2;
    }

    public String getParam3() {
        return param3;
    }

    public String getRaw() {
        return raw;
    }

    public OperationEnum getOperation() {
        return operation;
    }

    public String getAdmin() {
        return admin;
    }

    public AlarmEnum getAlarm() {
        return alarm;
    }

    public static Operation fromStr(String line) {
        String[] flds = line.split("\t");
        LocalDateTime logTime = parseDateTime(flds[2]);
        String admin = flds[1];
        OperationEnum operation = OperationEnum.valueOf(flds[0]);
        String object = flds[3];
        String param1 = flds[4];
        String param2 = flds[5];
        String param3 = flds[6];
        String raw = line;
        AlarmEnum alarm = (operation == OperationEnum.alarm) ? AlarmEnum.valueOf(null, object) : AlarmEnum.UNKNOWN;
        
        return new Operation(object, param1, param2, param3, raw, operation, admin, alarm);
    }

    private static LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception ignored) {
            return null;
        }
    }
}
