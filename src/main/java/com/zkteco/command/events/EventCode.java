package com.zkteco.command.events;

public enum EventCode {
    //Attendance entry
    EF_ATTLOG(1),
    //Pressed finger
    EF_FINGER(2),
    //Enrolled user
    EF_ENROLLUSER(4),
    //Enrolled user
    EF_ENROLLFINGER(8),
    //Pressed keyboard key
    EF_BUTTON(16),
    EF_UNLOCK(32),
    //Registered user placed finger
    EF_VERIFY(128),
    //Fingerprint score in enroll procedure
    EF_FPFTR(256),
    //Triggered alarm
    EF_ALARM(512),
	
    // Add new
	EF_USER(5),
	EF_SMS(6),
	EF_UDATA(7);
    
	
//			USER_DEFAULT        = 0
//			USER_ENROLLER       = 2
//			USER_MANAGER        = 6
//			USER_ADMIN          = 14

    private final int code;

    private EventCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
