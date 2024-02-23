package com.zkteco.terminal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.zkteco.Enum.OnOffenum;
import com.zkteco.Exception.DeviceNotConnectException;
import com.zkteco.command.events.EventCode;
import com.zkteco.commands.AttendanceRecord;
import com.zkteco.Enum.AttendanceStateEnum;
import com.zkteco.Enum.AttendanceTypeEnum;
import com.zkteco.Enum.CommandCodeEnum;
import com.zkteco.Enum.CommandReplyCodeEnum;
import com.zkteco.commands.GetTimeReply;
import com.zkteco.commands.SmsInfo;
import com.zkteco.commands.UserInfo;
import com.zkteco.Enum.UserRoleEnum;
import com.zkteco.commands.ZKCommand;
import com.zkteco.commands.ZKCommandReply;
import com.zkteco.utils.HexUtils;
import com.zkteco.utils.SecurityUtils;

public class ZKTerminal {
	
    private DatagramSocket socket;
    private InetAddress address;

    private final String ip;
    private final int port;

    private int sessionId;
    private int replyNo;

    public ZKTerminal(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    // Connect to devices
    public ZKCommandReply connect() throws IOException, DeviceNotConnectException {
    	if (!testPing()) {
            throw new DeviceNotConnectException("Device Not connect...!" );
    	}
        sessionId = 0;
        replyNo = 0;
        socket = new DatagramSocket(port);
        address = InetAddress.getByName(ip);
//        socket.setSoTimeout(1000);
        
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_CONNECT, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        sessionId = response[4] + (response[5] * 0x100);
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }
    
    // Test devices connect or not
    public boolean testPing() {
        try {
            Process process;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                process = Runtime.getRuntime().exec("ping -n 1 " + ip);
            } else {
                process = Runtime.getRuntime().exec("ping -c 1 -W 5 " + ip);
            }
            int returnCode = process.waitFor();
            return returnCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Disconnect Devices to this Application
    public void disconnect() throws IOException {
         int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_EXIT, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        socket.close();
    }
    
    // Enable devices
    public ZKCommandReply enableDevice() throws IOException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_ENABLEDEVICE, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }
    
    // disable devices
    public ZKCommandReply disableDevice() throws IOException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_DISABLEDEVICE, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    // Devices verification commkey
    public ZKCommandReply connectAuth(int comKey) throws IOException {
        int[] key = SecurityUtils.authKey(comKey, sessionId);
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_AUTH, sessionId, replyNo, key);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    // Set Realtime
    public ZKCommandReply enableRealtime(EventCode ... events) throws IOException {
        int allEvents = 0;
        for (EventCode event : events) {
            allEvents = allEvents | event.getCode();
        }
        String hex = StringUtils.leftPad(Integer.toHexString(allEvents), 8, "0");
        int[] eventReg = new int[4];
        int index = 3;
        while (hex.length() > 0) {
            eventReg[index] = (int) Long.parseLong(hex.substring(0, 2), 16);
            index--;
            hex = hex.substring(2);
        }
//        System.out.println(HexUtils.bytesToHex(eventReg));
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_REG_EVENT, sessionId, replyNo, eventReg);
        byte[] buf = new byte[toSend.length];
        index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
//        socket.setSoTimeout(40000);
        socket.send(packet);
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }
    
    // Get realtime Attendance log But other method not able to access(Address already in use)
    public ZKCommandReply enableRealtimeAtt() throws IOException, ParseException {
        int allEvents = EventCode.EF_ATTLOG.getCode();
        String hex = StringUtils.leftPad(Integer.toHexString(allEvents), 8, "0");
        int[] eventReg = new int[4];
        int index = 3;
        while (hex.length() > 0) {
            eventReg[index] = (int) Long.parseLong(hex.substring(0, 2), 16);
            index--;
            hex = hex.substring(2);
        }
//        System.out.println(HexUtils.bytesToHex(eventReg));
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_REG_EVENT, sessionId, replyNo, eventReg);
        byte[] buf = new byte[toSend.length];
        index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }


    // Devices Power down
    public ZKCommandReply Poweroff() throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_POWEROFF, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length,address, port);
        socket.send(packet);
        replyNo++;

          int[] response = readResponse();
          CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

          if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
//              boolean first = true;
          }
          socket.close();
          int replyId = response[6] + (response[7] * 0x100);
          int[] payloads = new int[response.length - 8];
          System.arraycopy(response, 8, payloads, 0, payloads.length);
          return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }
    
    // Restart Devices 
    public ZKCommandReply restart() throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_RESTART, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length,address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
//             boolean first = true;
        }
          
        socket.close();
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }
    

 // Get all devices Attendance Data
    public List<AttendanceRecord> getAttendanceRecords() throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_ATTLOG_RRQ, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        List<AttendanceRecord> attendanceRecords = new ArrayList<>();

        if (replyCode == CommandReplyCodeEnum.CMD_PREPARE_DATA) {
            boolean first = true;
            int lastDataRead;

            do {
                int[] readData = readResponse();
                lastDataRead = readData.length;
                String readPacket = HexUtils.bytesToHex(readData);
                String attendanceHex = readPacket.substring(first ? 24 : 16);
                List<AttendanceRecord> records = processAttendanceHex(attendanceHex);
                attendanceRecords.addAll(records);
                first = false;
            } while (lastDataRead == 1032);
        } else {
            System.out.println(response.length);
            String attendanceHex = HexUtils.bytesToHex(response).substring(24);
            List<AttendanceRecord> records = processAttendanceHex(attendanceHex);
            attendanceRecords.addAll(records);
        }

        return attendanceRecords;
    }


    public List<AttendanceRecord> getAttendanceRecordsForDateRange(String startTime, String endTime) throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_ATTLOG_RRQ, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        List<AttendanceRecord> attendanceRecords = new ArrayList<>();

        if (replyCode == CommandReplyCodeEnum.CMD_PREPARE_DATA) {
            boolean first = true;
            int lastDataRead;

            do {
                int[] readData = readResponse();
                lastDataRead = readData.length;
                String readPacket = HexUtils.bytesToHex(readData);
                String attendanceHex = readPacket.substring(first ? 24 : 16);
                List<AttendanceRecord> records = processAttendanceHex(attendanceHex);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = dateFormat.parse(startTime);
                Date endDate = dateFormat.parse(endTime);
                
                for (AttendanceRecord record : records) {
                	Date recordDate = record.getRecordTime(); 
                    if (recordDate.after(startDate) && recordDate.before(endDate)) {
                        attendanceRecords.add(record);
                    }
                }

                first = false;
            } while (lastDataRead == 1032);
        } else {
            System.out.println(response.length);
            String attendanceHex = HexUtils.bytesToHex(response).substring(24);
            List<AttendanceRecord> records = processAttendanceHex(attendanceHex);
            attendanceRecords.addAll(records);
        }

        return attendanceRecords;
    }
    
    // Helper method to process attendance hex string
    private List<AttendanceRecord> processAttendanceHex(String attendanceHex) throws ParseException {
        List<AttendanceRecord> records = new ArrayList<>();
        int recordLength = 80;  // Adjust this based on the actual length of your attendance records

        while (attendanceHex.length() >= recordLength) {
            String record = attendanceHex.substring(0, recordLength);
            int seq = Integer.valueOf(record.substring(2, 4) + record.substring(0, 2), 16);
            record = record.substring(4);
            String userId = Character.toString((char) Integer.valueOf(record.substring(0, 2), 16).intValue())
                    + Character.toString((char) Integer.valueOf(record.substring(2, 4), 16).intValue())
                    + Character.toString((char) Integer.valueOf(record.substring(4, 6), 16).intValue())
                    + Character.toString((char) Integer.valueOf(record.substring(6, 8), 16).intValue())
                    + Character.toString((char) Integer.valueOf(record.substring(8, 10), 16).intValue())
                    + Character.toString((char) Integer.valueOf(record.substring(10, 12), 16).intValue())
                    + Character.toString((char) Integer.valueOf(record.substring(12, 14), 16).intValue())
                    + Character.toString((char) Integer.valueOf(record.substring(14, 16), 16).intValue())
                    + Character.toString((char) Integer.valueOf(record.substring(16, 18), 16).intValue());

            record = record.substring(48);
            int method = Integer.valueOf(record.substring(0, 2), 16);
            record = record.substring(2);
            AttendanceTypeEnum attendanceType = AttendanceTypeEnum.values()[method];
            long encDate = Integer.valueOf(record.substring(6, 8), 16) * 0x1000000L
                    + (Integer.valueOf(record.substring(4, 6), 16) * 0x10000L)
                    + (Integer.valueOf(record.substring(2, 4), 16) * 0x100L)
                    + (Integer.valueOf(record.substring(0, 2), 16));

            Date attendanceDate = HexUtils.extractDate(encDate);

            record = record.substring(8);
            int operation = Integer.valueOf(record.substring(0, 2), 16);
            AttendanceStateEnum attendanceState = AttendanceStateEnum.values()[operation];
            // Create AttendanceRecord object
            AttendanceRecord attendanceRecord = new AttendanceRecord(seq, userId, attendanceType, attendanceDate, attendanceState);
            
            // Add the record to the list
            records.add(attendanceRecord);

            attendanceHex = attendanceHex.substring(recordLength);
        }

        return records;
    }    
    
    // Clear All Admin from device
    public ZKCommandReply ClearAdminData() throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_CLEAR_ADMIN, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length,address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
//             boolean first = true;
        }
          
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }


    // Clear All AttLog Data
    public ZKCommandReply ClearAttLogData() throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_CLEAR_ATTLOG, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length,address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
//             boolean first = true;
        }
          
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }
    
    // Get Device Name
    public String getDeviceName() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "~DeviceName".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);

            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    //
    public String getShowState() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "~ShowState".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }
    
    //
    public String getDeviceIP() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "IPAddress".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }
    
    // Get TCP port from device
    public String getDevicePORT() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "UDPPort".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    // Get Communication key from device
    public String getCommKey() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "COMKey".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    // Get device Id from device
    public String getDeviceId() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "DeviceID".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    public String iSDHCP() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "DHCP".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    public String getDNS() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "DNS".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    public String isEnableProxyServer() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "EnableProxyServer".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    public String getProxyServerIP() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "ProxyServerIP".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    public String getProxyServerPort() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "ProxyServerPort".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    // (wrong working i think)
    public String isDaylightSavingTime() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "DaylightSavingTime".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    // 69 english
    public String getLanguage() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "Language".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    public String isLockPowerKey() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "LockPowerKey".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    //Get voice on/off status
    public String isVoiceOn() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "VoiceOn".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);
//            SecurityUtils.printHexDump(byteArray);
            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    // set Device IP Address
    public ZKCommandReply setIPAddress(String ipaddress) throws IOException {
    	byte[] ipaddressbyte = ("IPAddress=" + ipaddress).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, ipaddressbyte);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
	    int replyId = response[6] + (response[7] * 0x100);
	    int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
        	
        }

	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
	}
    
    // set Communication key
    public ZKCommandReply setCommKey(int key) throws IOException {
    	byte[] COMKeybyte = ("COMKey=" + key).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, COMKeybyte);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
	    int replyId = response[6] + (response[7] * 0x100);
	    int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
        	
        }

	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
	}

    
    // set on off device voice
    public ZKCommandReply setVoiceOnOff(OnOffenum state) throws IOException {
    	byte[] voiceOn = ("VoiceOn=" + state.getOnOffState()).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, voiceOn);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
	    int replyId = response[6] + (response[7] * 0x100);
	    int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
        	
        }

	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
	}

    // set on off device voice
    public ZKCommandReply setShowStateOnOff(OnOffenum state) throws IOException {
    	byte[] ShowState = ("~ShowState=" + state.getOnOffState()).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, ShowState);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
	    int replyId = response[6] + (response[7] * 0x100);
	    int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
        	
        }

	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
	}	
    
    // get platform name
    public String getPlatform() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "~Platform".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);

            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    // lock power down key by device
    public ZKCommandReply setLockPowerKey(OnOffenum state) throws IOException {
        byte[] LockPowerKey = ("LockPowerKey=" + state.getOnOffState()).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, LockPowerKey);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

        }

        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    // wrong working i think
    public ZKCommandReply setDaylightSavingTime(OnOffenum state) throws IOException {
        byte[] DaylightSavingTime = ("DaylightSavingTime=" + state.getOnOffState()).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, DaylightSavingTime);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

        }

        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    // Set cloud server proxy port
    public ZKCommandReply setProxyServerPort(int devport) throws IOException {
        byte[] ProxyServerPort = ("ProxyServerPort=" + devport).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, ProxyServerPort);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

        }

        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    //Set device cloud Proxy Server IP
    public ZKCommandReply setProxyServerIP(String devIP) throws IOException {
        byte[] ProxyServerIP = ("ProxyServerIP=" + devIP).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, ProxyServerIP);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

        }

        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    // on, off proxy server cloud server
    public ZKCommandReply setEnableProxyServer(OnOffenum state) throws IOException {
        byte[] EnableProxyServer = ("EnableProxyServer=" + state.getOnOffState()).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, EnableProxyServer);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

        }

        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    // set dns to device
    public ZKCommandReply setDNS(String DNS) throws IOException {
        byte[] DNSbyte = ("DNS=" + DNS).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, DNSbyte);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

        }

        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    // on and off Ethernet DHCP
    public ZKCommandReply setDHCP(OnOffenum state) throws IOException {
        byte[] DHCPbyte = ("DHCP=" + state.getOnOffState()).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, DHCPbyte);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

        }

        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    // Set device id PC connection
    public ZKCommandReply setDeviceID(int DeviceID) throws IOException {
        byte[] DeviceIDbyte = ("DeviceID=" + DeviceID).getBytes();
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_WRQ, sessionId, replyNo, DeviceIDbyte);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

        }

        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }


    // Get Device Serial Number
    public String getSerialNumber() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "~SerialNumber".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";    
    }

    // get Device MAC address
    public String getMAC() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "MAC".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

            // Convert the array of integers to a byte array
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }

        return "";    
    }

    // Device Fingerprint Version
    public String getFaceVersion() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "ZKFaceVersion".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {

            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);

            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }

        return "";    
      }    
    
    // get fingerprint version
    public int getFPVersion() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "~ZKFPVersion".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteResponse = new byte[response.length - 8];
            for (int i = 0; i < byteResponse.length; i++) {
                byteResponse[i] = (byte) response[i + 8];
            }
            
            String responseString = new String(byteResponse, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                try {
                    return Integer.parseInt(responseParts[1].split("\0")[0]);
                } catch (NumberFormatException e) {

                }
            }
        }
        return 0;    
    }
    
    // Device OEM name
    public String getOEMVendor() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "~OEMVendor".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteArray = new byte[response.length - 8];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) (response[8 + i] & 0xFF);
            }

            String responseString = new String(byteArray, StandardCharsets.US_ASCII);

            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return responseParts[1].split("\0")[0];
            }
        }
        return "";
    }

    
    
 // Get Devices All users Data
    public List<UserInfo> getAllUsers() throws IOException, ParseException {
    	try {
        	int usercount = getDeviceStatus().get("userCount");
        	if(usercount==0)
        		return Collections.emptyList();
            int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_USERTEMP_RRQ, sessionId, replyNo, null);
            byte[] buf = new byte[toSend.length];
            int index = 0;

            for (int byteToSend : toSend) {
                buf[index++] = (byte) byteToSend;
            }

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            replyNo++;
            int[] response = readResponse();
            CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

            StringBuilder userBuffer = new StringBuilder();
            List<UserInfo> userList = new ArrayList<>();
            if (replyCode == CommandReplyCodeEnum.CMD_PREPARE_DATA) {
                boolean first = true;
                int lastDataRead;

                do {
                    int[] readData = readResponse();
                    lastDataRead = readData.length;
                    String readPacket = HexUtils.bytesToHex(readData);
                    userBuffer.append(readPacket.substring(first ? 24 : 16));
                    first = false;
                } while (lastDataRead == 1032);

                String usersHex = userBuffer.toString();
                byte[] usersData = HexUtils.hexStringToByteArray(usersHex);

                ByteBuffer buffer = ByteBuffer.wrap(usersData);

                while (buffer.remaining() >= 72) {
                    ByteBuffer userBuffer1 = ByteBuffer.allocate(72);
                    buffer.get(userBuffer1.array()); 
                    UserInfo user = UserInfo.encodeUser(userBuffer1, 72);
                    userList.add(user);
                }

            } else {
                System.out.println("Data Fetch failed or null");
            }

            int replyId = response[6] + (response[7] * 0x100);
            System.out.println(replyId);
            int[] payloads = new int[response.length - 8];
            System.arraycopy(response, 8, payloads, 0, payloads.length);

            return userList;
            
    	} finally {

    	}
    	
    }

    // Get work code
    public boolean getWorkCode() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_OPTIONS_RRQ, sessionId, replyNo, "WorkCode".getBytes());
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteResponse = new byte[response.length - 8];
            for (int i = 0; i < byteResponse.length; i++) {
                byteResponse[i] = (byte) response[i + 8];
            }
            
            String responseString = new String(byteResponse, StandardCharsets.US_ASCII);
            String[] responseParts = responseString.split("=", 2);

            if (responseParts.length == 2) {
                return "1".equals(responseParts[1].split("\0")[0]);
            }
        }

        return false; 
    }
  
    
    // Get devices capacity
    public Map<String, Integer> getDeviceStatus() throws IOException {
    	
	    int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_GET_FREE_SIZES, sessionId, replyNo, null);
	    byte[] buf = new byte[toSend.length];
	    int index = 0;
	
	    for (int byteToSend : toSend) {
	        buf[index++] = (byte) byteToSend;
	    }
	
	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
	    socket.send(packet);
	    replyNo++;
	
	    int[] response = readResponse();
	    CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            byte[] byteResponse = new byte[response.length - 8];
            for (int i = 0; i < byteResponse.length; i++) {
                byteResponse[i] = (byte) response[i + 8];
            }

            ByteBuffer buffer = ByteBuffer.wrap(byteResponse);

            if (buffer.remaining() >= 92) {
                // Process the device status data
                Map<String, Integer> statusMap = new HashMap<>();

                statusMap.put("adminCount", Integer.reverseBytes(buffer.getInt(48)));
                statusMap.put("userCount", Integer.reverseBytes(buffer.getInt(16)));
                statusMap.put("fpCount", Integer.reverseBytes(buffer.getInt(24)));
                statusMap.put("pwdCount", Integer.reverseBytes(buffer.getInt(52)));
                statusMap.put("oplogCount", Integer.reverseBytes(buffer.getInt(40)));
                statusMap.put("attlogCount", Integer.reverseBytes(buffer.getInt(32)));
                statusMap.put("fpCapacity", Integer.reverseBytes(buffer.getInt(56)));
                statusMap.put("userCapacity", Integer.reverseBytes(buffer.getInt(60)));
                statusMap.put("attlogCapacity", Integer.reverseBytes(buffer.getInt(64)));
                statusMap.put("remainingFp", Integer.reverseBytes(buffer.getInt(68)));
                statusMap.put("remainingUser", Integer.reverseBytes(buffer.getInt(72)));
                statusMap.put("remainingAttlog", Integer.reverseBytes(buffer.getInt(76)));
                statusMap.put("faceCount", Integer.reverseBytes(buffer.getInt(80)));
                statusMap.put("faceCapacity", Integer.reverseBytes(buffer.getInt(88)));
                
                return statusMap;

            } 

        }
        return Collections.emptyMap();
    }
    
    
    // Ensure the machine to be at the authentication (Not verify)
    public ZKCommandReply setStartVerify() throws IOException {
        int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_STARTVERIFY, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;

        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
	    int replyId = response[6] + (response[7] * 0x100);
	    int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO:
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
        	
        }

	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
	}
	

    
    // Get Devices Date And Time
    public Date getDeviceTime() throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_GET_TIME, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        GetTimeReply gettime = new GetTimeReply(replyCode, sessionId, replyId, payloads);
        return gettime.getDeviceDate();
    }

    
//    // Add enroll fingerprint (Work wrong)
//    public boolean enrollUser(int uid, int tempId, String userId) throws IOException {
//        // Prepare CMD_STARTENROLL command
//        int[] startEnrollCommand = new int[]{0x61, 0x00, uid, uid >> 8, tempId, tempId >> 8};
//
//        // Create and send the packet
//        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_STARTENROLL, sessionId, replyNo, startEnrollCommand);
//        byte[] buf = new byte[toSend.length];
//        int index = 0;
//        for (int byteToSend : toSend) {
//            buf[index++] = (byte) (byteToSend & 0xFF);
//        }
//
//        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
//        socket.send(packet);
//        
//	      // Increment reply number
//	      replyNo++;
//	
//	      // Read response from the device
//	      int[] response = readResponse();
//	      
//	      // Decode response
//	      CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
//	      int replyId = response[6] + (response[7] * 0x100);
//	      int[] payloads = Arrays.copyOfRange(response, 8, response.length);
//	
//	      // TODO: Add specific logic for CMD_ACK_OK response
//	      if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
//	          // Log success or perform additional actions if needed
//	      }
//	
//	      // Return ZKCommandReply
////	      return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
//	  
//
//        return false;
//    }

    // 
    public ZKCommandReply cancelEnrollment() throws IOException {
        // Create and send the packet
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_CANCELCAPTURE, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) (byteToSend & 0xFF);
        }
        
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        // Increment reply number
        replyNo++;

        // Read response from the device
        int[] response = readResponse();
        
        // Decode response
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = Arrays.copyOfRange(response, 8, response.length);

        // TODO: Add specific logic for CMD_ACK_OK response
        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            // Log success or perform additional actions if needed
        }

        // Return ZKCommandReply
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }
    
    //
    
    // Set current system time to device time
    	public ZKCommandReply syncTime() throws IOException {
    	    long encodedTime = HexUtils.encodeTime(new Date());
    	    int[] timeBytes = HexUtils.convertLongToLittleEndian(encodedTime);
    	    int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_SET_TIME, sessionId, replyNo, timeBytes);
    	    byte[] buf = new byte[toSend.length];
    	    int index = 0;
    	    for (int byteToSend : toSend) {
    	        buf[index++] = (byte) (byteToSend & 0xFF);
    	    }
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
    	    socket.send(packet);
    	    replyNo++;
    	    int[] response = readResponse();
    	    
    	    CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
    	    int replyId = response[6] + (response[7] * 0x100);
    	    int[] payloads = new int[response.length - 8];
    	    System.arraycopy(response, 8, payloads, 0, payloads.length);

    	    // TODO:
            if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            	
            }

    	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    	}
    	
    	// Delete User
        public ZKCommandReply delUser(int delUId) throws IOException {    	
            int[] delUIdArray = new int[]{delUId & 0xFF, (delUId >> 8) & 0xFF};
            
            int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_DELETE_USER, sessionId, replyNo, delUIdArray);
    	    byte[] buf = new byte[toSend.length];
    	    int index = 0;

    	    for (int byteToSend : toSend) {
    	        buf[index++] = (byte) (byteToSend & 0xFF); 
    	    }
            
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
    	    socket.send(packet);
//    	    System.out.println("Sending CMD_DELETE_USER packet. Payload: " + Arrays.toString(buf));
    	    replyNo++;

    	    int[] response = readResponse();
    	    CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
    	    int replyId = response[6] + (response[7] * 0x100);
    	    int[] payloads = new int[response.length - 8];
    	    System.arraycopy(response, 8, payloads, 0, payloads.length);
    	    
            if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
        	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
            } else {
            	System.out.println("User Not found...!");
            	return null;	
            }
		}
       

    	// New Add User
        public ZKCommandReply modifyUserInfo(UserInfo newUser) throws IOException {
        	
            int uid = newUser.getUid();
            String userid = newUser.getUserid();
            UserRoleEnum role = newUser.getRole();
            int role1d = role.getRole();
            String password = newUser.getPassword();
            String name = newUser.getName();
            long cardno = newUser.getCardno();
            
        	// Prepare data for the new user entry
            ByteBuffer commandBuffer = ByteBuffer.allocate(72).order(ByteOrder.LITTLE_ENDIAN);

            commandBuffer.putShort((short) uid);
            commandBuffer.putShort((short) role1d);

            byte[] passwordBytes = password.getBytes();
            commandBuffer.position(3);
            commandBuffer.put(passwordBytes, 0, Math.min(passwordBytes.length, 8));

            byte[] nameBytes = name.getBytes();
            commandBuffer.position(11);
            commandBuffer.put(nameBytes, 0, Math.min(nameBytes.length, 24));

            commandBuffer.position(35);
            commandBuffer.putShort((short) cardno);

            commandBuffer.position(40);
            commandBuffer.putInt(0);

            byte[] userIdBytes = (userid != null) ? userid.getBytes() : new byte[0];
            commandBuffer.position(48);
            commandBuffer.put(userIdBytes, 0, Math.min(userIdBytes.length, 9));

//            SecurityUtils.printHexDump(commandBuffer.array());

            int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_USER_WRQ, sessionId, replyNo, commandBuffer.array());
            byte[] buf = new byte[toSend.length];

            for (int i = 0; i < toSend.length; i++) {
                buf[i] = (byte) toSend[i];
            }

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            replyNo++;

            int[] response = readResponse();
            CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

            if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
                return new ZKCommandReply(replyCode, sessionId, replyNo, null);
            } else {
                return null;
            }
        }

        // wrong work
        public ZKCommandReply setSms(int tagp, int IDp, int validMinutesp, long startTimep, String contentp)
                throws IOException {
            SmsInfo newSms = new SmsInfo(tagp, IDp, validMinutesp, 0, startTimep, contentp);

            ByteBuffer commandBuffer = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);

            commandBuffer.put((byte) newSms.getTag());
            commandBuffer.putShort((short) newSms.getId());
            commandBuffer.putShort((short) newSms.getValidMinutes());
            commandBuffer.putShort((short) newSms.getReserved());

            // Convert startTimep to LocalDateTime
            LocalDateTime localDateTime = Instant.ofEpochMilli(startTimep).atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Pack the LocalDateTime into the buffer
            commandBuffer.putInt((int) localDateTime.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(localDateTime)));

            byte[] contentBytes = newSms.getContent().getBytes(StandardCharsets.UTF_8);
            commandBuffer.put(contentBytes, 0, Math.min(contentBytes.length, 60));

            // Print the hex dump (you can remove this in your actual code)
            SecurityUtils.printHexDump(commandBuffer.array());

            int[] toSend = ZKCommand.getPackets(CommandCodeEnum.CMD_SMS_WRQ, sessionId, replyNo, commandBuffer.array());
            byte[] buf = new byte[toSend.length];

            for (int i = 0; i < toSend.length; i++) {
                buf[i] = (byte) toSend[i];
            }

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            replyNo++;

            int[] response = readResponse();
            CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

            return new ZKCommandReply(replyCode, sessionId, replyNo, null);
        }
        
        // Working wrong(content work well)
        public SmsInfo getSms(int smsUId) throws IOException, ParseException {
            int[] smsIdArray = new int[]{smsUId & 0xFF, (smsUId >> 8) & 0xFF};

            int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_SMS_RRQ, sessionId, replyNo, smsIdArray);
            byte[] buf = new byte[toSend.length];
            int index = 0;

            for (int byteToSend : toSend) {
                buf[index++] = (byte) (byteToSend & 0xFF);
            }

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            replyNo++;

            int[] response = readResponse();
            if (( response[0] + (response[1] * 0x100)) == 4993){
                return null;
            }
            CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
            int replyId = response[6] + (response[7] * 0x100);

            byte[] byteResponse = SecurityUtils.convertIntArrayToByteArray(response);
//            SecurityUtils.printHexDump(byteResponse);

            int[] payloads = new int[response.length - 8];
            System.arraycopy(response, 8, payloads, 0, payloads.length);
//            System.out.println(response.length);
            if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
            	int tag = response[8];                
            	int id = (response[9] & 0xFF) + ((response[10] & 0xFF) << 8);
            	int validMinutes = Short.reverseBytes((short) ((response[11] << 8) | (response[10] & 0xFF))) & 0xFFFF;

            	int reserved = ((response[11] & 0xFF) << 8) | (response[10] & 0xFF);
            	long startTime = (response[15] & 0xFFL) | ((response[14] & 0xFFL) << 8) | ((response[13] & 0xFFL) << 16) | ((response[12] & 0xFFL) << 24);

            	// Assuming HexUtils.extractDate() is a method that converts the encoded date to a Date object
            	Date startDate = HexUtils.extractDate(startTime);
            	
                int contentOffset = 19;  
                byte[] contentBytes = new byte[321];
                for (int i = 0; i < 321; i++) {
                    contentBytes[i] = (byte) (response[i + contentOffset] & 0xFF);
                }
                
                String content = new String(contentBytes, StandardCharsets.UTF_8).trim();
//                SecurityUtils.printUnsignedBytes(contentBytes);

                // Print decoded values
//                System.out.println("Tag: " + tag);
//                System.out.println("ID: " + id);
//                System.out.println("Valid Minutes: " + validMinutes);
//                System.out.println("Reserved: " + reserved);
//                System.out.println("Start Time: " + startTime + " " + startDate);

                return new SmsInfo(tag, id, validMinutes, reserved, startTime, content);
            }
            return null;            
        }


    // Detect SMS
        public ZKCommandReply delSMS(int smsId) throws IOException {    	
            int[] delsmsIdArray = new int[]{smsId & 0xFF, (smsId >> 8) & 0xFF};
            
            int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_DELETE_SMS, sessionId, replyNo, delsmsIdArray);
    	    byte[] buf = new byte[toSend.length];
    	    int index = 0;

    	    for (int byteToSend : toSend) {
    	        buf[index++] = (byte) (byteToSend & 0xFF); 
    	    }
            
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
    	    socket.send(packet);
    	    replyNo++;

    	    int[] response = readResponse();
    	    CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
    	    int replyId = response[6] + (response[7] * 0x100);
    	        	    
    	    int[] payloads = new int[response.length - 8];
    	    System.arraycopy(response, 8, payloads, 0, payloads.length);
    	    
            if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
        	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
            } 
    	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);

		}

        public ZKCommandReply enrollUser(int uid, int tempId, String userId) throws IOException {
            // Prepare CMD_STARTENROLL command
            int[] startEnrollCommand = new int[]{0x61, 0x00, uid, uid >> 8, tempId, tempId >> 8};

            // Create and send the packet
            int[] startEnrollPacket = ZKCommand.getPacket(CommandCodeEnum.CMD_CAPTUREFINGER, sessionId, replyNo, startEnrollCommand);
            sendPacket(startEnrollPacket);

            // Increment reply number
            replyNo++;

            // Read response from the device
            int[] response = readResponse();

            // Decode response
            CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
            int replyId = response[6] + (response[7] * 0x100);
            int[] payloads = Arrays.copyOfRange(response, 8, response.length);

            // TODO: Add specific logic for CMD_ACK_OK response
            if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
                // Log success or perform additional actions if needed
            	
//                return true;

            }
            

            // Return ZKCommandReply
             return new ZKCommandReply(replyCode, sessionId, replyId, payloads);

//            return false;
        }

        private void sendPacket(int[] packetData) throws IOException {
            byte[] buf = new byte[packetData.length];

            for (int i = 0; i < packetData.length; i++) {
                buf[i] = (byte) packetData[i];
            }

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        }


    // Test Voice CMD_TESTVOICE
	//        play test voice:\n
	//        0 Thank You\n
	//        1 Incorrect Password\n
	//        2 Access Denied\n
	//        3 Invalid ID\n
	//        4 Please try again\n
	//        5 Duplicate ID\n
	//        6 The clock is flow\n
	//        7 The clock is full\n
	//        8 Duplicate finger\n
	//        9 Duplicated punch\n
	//        10 Beep kuko\n
	//        11 Beep siren\n
	//        12 -\n
	//        13 Beep bell\n
	//        14 -\n
	//        15 -\n
	//        16 -\n
	//        17 -\n
	//        18 Windows(R) opening sound\n
	//        19 -\n
	//        20 Fingerprint not emolt\n
	//        21 Password not emolt\n
	//        22 Badges not emolt\n
	//        23 Face not emolt\n
	//        24 Beep standard\n
	//        25 -\n
	//        26 -\n
	//        27 -\n
	//        28 -\n
	//        29 -\n
	//        30 Invalid user\n
	//        31 Invalid time period\n
	//        32 Invalid combination\n
	//        33 Illegal Access\n
	//        34 Disk space full\n
	//        35 Duplicate fingerprint\n
	//        36 Fingerprint not registered\n
	//        37 -\n
	//        38 -\n
	//        39 -\n
	//        40 -\n
	//        41 -\n
	//        42 -\n
	//        43 -\n
	//        43 -\n
	//        45 -\n
	//        46 -\n
	//        47 -\n
	//        48 -\n
	//        49 -\n
	//        50 -\n
	//        51 Focus eyes on the green box\n
	//        52 -\n
	//        53 -\n
	//        54 -\n
	//        55 -\n
	//
	//       :param index: int sound index
	//       :return: bool
    public ZKCommandReply testVoice(int voice) throws IOException {    	
    	    int[] voiceArray = new int[]{voice};
    	    int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_TESTVOICE, sessionId, replyNo, voiceArray);
    	    byte[] buf = new byte[toSend.length];
    	    int index = 0;

    	    for (int byteToSend : toSend) {
    	        buf[index++] = (byte) (byteToSend & 0xFF); 
    	    }

    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
    	    socket.send(packet);
//    	    System.out.println("Sending CMD_TESTVOICE packet. Payload: " + Arrays.toString(buf));
    	    replyNo++;

    	    int[] response = readResponse();
    	    CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));
    	    int replyId = response[6] + (response[7] * 0x100);
    	    int[] payloads = new int[response.length - 8];
    	    System.arraycopy(response, 8, payloads, 0, payloads.length);

    	    return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    	}
       
    // CMD_REFRESHDATA (Not Verified)
    public ZKCommandReply RefreshData() throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_REFRESHDATA, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length,address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
//             boolean first = true;
        }
          
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }

    //CMD_FREE_DATA (Not Verified)
    public ZKCommandReply FreeDeviceBuffer() throws IOException, ParseException {
        int[] toSend = ZKCommand.getPacket(CommandCodeEnum.CMD_FREE_DATA, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];
        int index = 0;
        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length,address, port);
        socket.send(packet);
        replyNo++;
        int[] response = readResponse();
        CommandReplyCodeEnum replyCode = CommandReplyCodeEnum.decode(response[0] + (response[1] * 0x100));

        if (replyCode == CommandReplyCodeEnum.CMD_ACK_OK) {
//             boolean first = true;
        }
          
        int replyId = response[6] + (response[7] * 0x100);
        int[] payloads = new int[response.length - 8];
        System.arraycopy(response, 8, payloads, 0, payloads.length);
        return new ZKCommandReply(replyCode, sessionId, replyId, payloads);
    }
        
    // Read response 
    public int[] readResponse() throws IOException {
        byte[] buf = new byte[1000000];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        int[] response = new int[packet.getLength()];

        for (int i = 0; i < response.length; i++) {
            response[i] = buf[i] & 0xFF;
        }

        return response;

        /*int index = 0;
        int[] data = new int[1000000];

        int read;
        int size = 0;

        boolean reading = true;

        while (reading && (read = is.read()) != -1) {
            if (index >= 4 && index <= 7) {
                size += read * Math.pow(16, index - 4);
            } else if (index > 7) {
                if (index - 7 >= size) {
                    reading = false;
                }
            }

            data[index] = read;
            index++;
        }

        int[] finalData = new int[index];

        System.arraycopy(data, 0, finalData, 0, index);

        return finalData;*/
    }

}
