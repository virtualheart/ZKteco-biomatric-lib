package com.zkteco.iclockhelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AttendancePhotoLog extends ServerDatetimeMixin {
    private final String raw;
    private final String pin;
    private final boolean isUploadPhoto;
    private final boolean isRealUpload;
    private final String data;

    public AttendancePhotoLog(String raw, String pin, LocalDateTime serverDatetime, boolean isUploadPhoto, boolean isRealUpload, String data) {
        super(serverDatetime);
        this.raw = raw;
        this.pin = pin;
        this.isUploadPhoto = isUploadPhoto;
        this.isRealUpload = isRealUpload;
        this.data = data;
    }

    public String getRaw() {
        return raw;
    }

    public String getPin() {
        return pin;
    }

    public boolean isUploadPhoto() {
        return isUploadPhoto;
    }

    public boolean isRealUpload() {
        return isRealUpload;
    }

    public String getData() {
        return data;
    }

    public static AttendancePhotoLog fromRequestPin(String reqPin, String body) {
        String pin = "";
        if (reqPin == null || reqPin.isEmpty()) {
            Map<String, String> bodyDict = buildDict(body.split("CMD=")[0], "\n");
            reqPin = bodyDict.get("PIN");
        }

        String[] pinSplit = reqPin.split("\\.")[0].split("-");
        String dt = pinSplit[0];
        if (pinSplit.length == 2) {
            pin = pinSplit[1];
        }

        LocalDateTime serverDatetime = LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String imageDate = "";
        boolean isUploadPhoto = false;
        boolean isRealUpload = false;

        if (body.contains("CMD=uploadphoto")) {
            imageDate = body.split("CMD=uploadphoto")[1];
            isUploadPhoto = true;
        }
        if (body.contains("CMD=realupload")) {
            imageDate = body.split("CMD=realupload")[1];
            isRealUpload = true;
        }

        return new AttendancePhotoLog(reqPin + body, pin, serverDatetime, isUploadPhoto, isRealUpload, imageDate);
    }

    private static Map<String, String> buildDict(String ops1, String separator) {
        Map<String, String> flds = new HashMap<>();
        for (String item : ops1.split(separator)) {
            int index = item.indexOf('=');
            if (index > 0) {
                flds.put(item.substring(0, index), item.substring(index + 1));
            }
        }

        return flds;
    }
}
