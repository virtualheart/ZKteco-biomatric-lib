package com.zkteco.iclockhelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Transaction extends ServerDatetimeMixin {
    private final String pin;
    private final String raw;
    private final String checkType;
    private final String verifyCode;
    private final String workCode;
    private final String reserved;

    public Transaction(String pin, LocalDateTime serverDatetime, String checkType, String verifyCode, String workCode, String reserved, String raw) {
        super(serverDatetime);
        this.pin = pin;
        this.checkType = checkType;
        this.verifyCode = verifyCode;
        this.workCode = workCode;
        this.reserved = reserved;
        this.raw = raw;
    }

    public String getPin() {
        return pin;
    }

    public String getRaw() {
        return raw;
    }

    public String getCheckType() {
        return checkType;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public String getWorkCode() {
        return workCode;
    }

    public String getReserved() {
        return reserved;
    }

    public static Transaction fromStr(String line) {
        String[] flds = (line + "\t\t\t\t\t").split("\t");
        String pin = flds[0];
        LocalDateTime serverDatetime = null;
        try {
            serverDatetime = LocalDateTime.parse(flds[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception ignored) {
        }
        String checkType = flds[2];
        String verifyCode = flds[3];
        String workCode = flds[4];
        String reserved = flds[5];

        return new Transaction(pin, serverDatetime, checkType, verifyCode, workCode, reserved, line);
    }
}

