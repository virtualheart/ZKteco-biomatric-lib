package com.zkteco.Enum;

public enum TableEnum {
	UNKNOWN("0"),
	OPERLOG("OPERLOG"),
    ATTLOG("ATTLOG"),
    ATTPHOTO("ATTPHOTO");

    private final String value;

    TableEnum(String value) {
        this.value = value;
    }

    public static UnknowableEnum _missing_(String value) {
        return UnknowableEnum.unknown;
    }
}