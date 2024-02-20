package com.zkteco.Enum;

public enum AlarmEnum {
	UNKNOWN("0"),
    door_close_detected("50"),
    door_open_detected("51"),
    machine_been_broken("55"),
    out_door_button("53"),
    door_broken_accidentally("54"),
    try_invalid_verification("58"),
    alarm_cancelled("65535");

    private final String value;

    AlarmEnum(String value) {
        this.value = value;
    }

    public static UnknowableEnum _missing_(String value) {
        return UnknowableEnum.unknown;
    }
}
