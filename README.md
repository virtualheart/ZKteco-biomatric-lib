JavaZK-Lib
Documentation Status Updates

Java lib to access ZKTeco's standalone devices


Introduction

This project is part of an effort to make an alternative to ZKTeco's software, to manage attendance devices, it was made using the protocol spec shown in zk-protocol <a href="https://github.com/adrobinoga/zk-protocol">repo</a>.

Project overview

Functions in this module follow a similar grouping used on zk-protocol repo.
ZK Modules

    Access: Includes functions to get/set access parameters.
    Data Record: Includes functions to manage the attendance records and operation records.
    Data User: Includes functions to manage users info, including passwords, fingerprints, names, verification styles, etc).
    Realtime: Includes functions to receive and parse realtime events (e.g. user auth at door).
    Terminal: Includes functions to get/set device parameters.
    Other: Misc operations (enable/disable device, restart, power off, sms, etc).

For more info about these operations take a look at zk-protocol.

Develop

Capture files of network traffic of documented tests are welcomed, to expand the protocol spec and then add support in the lib.

special things : <br>
https://github.com/mkhoudary/ZKTeco4J/<br>
https://github.com/adrobinoga/zk-protocol/<br>
https://docs.nufaza.com/docs/devices/zkteco_attendance/push_protocol/<br>
https://zkteco.com/<br>