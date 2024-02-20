package com.zkteco.Enum;

public enum AttendanceStateEnum {
    CHECK_IN(0),
    CHECK_OUT(1),
    BREAK_OUT(2),
    BREAK_IN(3),
    OT_IN(4),
    OT_OUT(5);

    private final int attendanceState;

    AttendanceStateEnum(int attendanceState) {
        this.attendanceState = attendanceState;
    }

    public int getAttendanceState() {
        return attendanceState;
    }   
}