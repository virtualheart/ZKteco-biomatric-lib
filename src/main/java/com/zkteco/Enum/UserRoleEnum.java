package com.zkteco.Enum;

public enum UserRoleEnum {
	USER_DEFAULT(0),
	USER_ENROLLER(2),
	USER_MANAGER(6),
	USER_ADMIN(14);

    private final int role;

    private UserRoleEnum(int role) {
        this.role = role;
    }

    public int getRole() {
        return role;
    }

}
