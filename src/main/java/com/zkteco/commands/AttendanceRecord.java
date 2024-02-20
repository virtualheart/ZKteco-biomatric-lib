package com.zkteco.commands;

import java.util.Date;

import com.zkteco.Enum.AttendanceStateEnum;
import com.zkteco.Enum.AttendanceTypeEnum;

public class AttendanceRecord {
    private int userSN;
    private String userID;
    private AttendanceTypeEnum verifyType;
    private Date recordTime;
    private AttendanceStateEnum verifyState;

    public AttendanceRecord(int userSN, String userID, AttendanceTypeEnum verifyType, Date recordTime, AttendanceStateEnum verifyState) {
        this.userSN = userSN;
        this.userID = userID;
        this.verifyType = verifyType;
        this.recordTime = recordTime;
        this.verifyState = verifyState;
    }

    // Getter and Setter methods for each field

    public int getUserSN() {
        return userSN;
    }

    public void setUserSN(int userSN) {
        this.userSN = userSN;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public AttendanceTypeEnum getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(AttendanceTypeEnum verifyType) {
        this.verifyType = verifyType;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public AttendanceStateEnum getVerifyState() {
        return verifyState;
    }

    public void setVerifyState(AttendanceStateEnum verifyState) {
        this.verifyState = verifyState;
    }

//    @Override
//    public String toString() {
//        return "AttendanceRecord{" +
//                "userSN=" + userSN +
//                ", userID='" + userID + '\'' +
//                ", verifyType=" + verifyType +
//                ", recordTime=" + recordTime +
//                ", verifyState=" + verifyState +
//                '}';
//    }
}
