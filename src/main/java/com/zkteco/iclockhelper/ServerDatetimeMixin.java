package com.zkteco.iclockhelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ServerDatetimeMixin {
    private final LocalDateTime serverDatetime;

    public ServerDatetimeMixin(LocalDateTime serverDatetime) {
        this.serverDatetime = serverDatetime;
    }

    public LocalDateTime getServerDatetime() {
        return serverDatetime;
    }

    public LocalDateTime correctDatetime(ZoneId zoneId) {
        if (serverDatetime == null) {
            return null;
        }

        return ZonedDateTime.of(
                serverDatetime,
                zoneId
        ).toLocalDateTime();
    }
}
