package com.zkteco.Enum;

public enum AttendanceTypeEnum {
    PASSWORD(0),
    FINGERPRINT(1),
    RF_CARD(2);
	
    private final int attendanceType;

    AttendanceTypeEnum(int attendanceType) {
        this.attendanceType = attendanceType;
    }

    public int getAttendanceType() {
        return attendanceType;
    }
}