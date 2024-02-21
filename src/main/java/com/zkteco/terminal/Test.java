package com.zkteco.terminal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;

import com.zkteco.Enum.OnOffenum;
import com.zkteco.command.events.EventCode;
import com.zkteco.commands.AttendanceRecord;
import com.zkteco.commands.SmsInfo;
import com.zkteco.commands.UserInfo;
import com.zkteco.commands.ZKCommandReply;
import com.zkteco.iclockhelper.ZKTecoHttpServer;
import com.zkteco.utils.HexUtils;

//import java.io.IOException;
//import java.util.Calendar;
//import java.util.List;
//
//import com.zkteco.command.events.EventCode;
//import com.zkteco.commands.AttendanceRecord;
//import com.zkteco.commands.SmsInfo;
//import com.zkteco.commands.UserInfo;
//import com.zkteco.commands.UserRole;
//import com.zkteco.commands.ZKCommandReply;
//import com.zkteco.utils.HexUtils;

public class Test {
	
    public static void main(String[] args) throws Exception {
      ZKTerminal terminal = new ZKTerminal("192.168.1.205", 4370);
      ZKCommandReply reply = terminal.connect();
      reply = terminal.connectAuth(0);
      reply = terminal.disableDevice();
      
      System.out.println(reply.getCode());
      reply = terminal.enableDevice();
      
      System.out.println(reply.getCode());


    


//    SmsInfo smsinfo = terminal.getYourSmsList(3);

//    reply = terminal.setSms(253,1024,7680,System.currentTimeMillis(),"Dhana Test");

//      
//      try {
//          SmsInfo smsinfo = terminal.getYourSmsList(1);
//          
//	    System.out.println("tag: " + smsinfo.getTag()); // 253 public , 254 private , 255 draft
//	    System.out.println("ID: " + smsinfo.getId()); // 1  - 256 2 - 512 3 - 768 (id * 256)
//	    System.out.println("validMinutes: " + smsinfo.getValidMinutes()); // (min * 256)
//	    System.out.println("reserved: " + smsinfo.getReserved());
//	    System.out.println("startTime: " + smsinfo.getStartTime());
//	    System.out.println("content: " + smsinfo.getContent());
//    	    System.out.println("------------------------");
//    	  } catch (IOException e) {
//    	      e.printStackTrace(); // Handle exceptions appropriately
//    	  }  
//        
        //
//                try {
//                    List<UserInfo> userList = terminal.getAllUsers();
//        
//                    // Access and print user information
//                    for (UserInfo user : userList) {
//        	              System.out.println("User ID: " + user.getUserid());
//        	              System.out.println("Name: " + user.getName());
//        	              System.out.println("Password: " + user.getPassword());
//        	              System.out.println("Card Number: " + user.getCardno());
//        	              System.out.println("Group Number: " + user.getGroupNumber());
//        	              System.out.println("User TimeZone Flag: " + user.getUserTimeZoneFlag());
//        	              System.out.println("TimeZone1: " + user.getTimeZone1()); 
//        	              System.out.println("TimeZone2: " + user.getTimeZone2());
//        	              System.out.println("TimeZone3: " + user.getTimeZone3());
//        	              System.out.println("User Serial Number: " + user.getUid());
//        	              System.out.println("Role: " + user.getRole());
//                          System.out.println("------------------------");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace(); // Handle exceptions appropriately
//                }              
//                
      
//      try {
//          List<AttendanceRecord> attendanceRecord = terminal.getAttendanceRecordsForDateRange("2024-02-21 00:00:00", "2024-02-21 23:59:00");
//          
//        // Access and print user information
//        for (AttendanceRecord attendance : attendanceRecord) {
//              System.out.println("User ID: " + attendance.getUserID());
//              System.out.println("User SN: " + attendance.getUserSN());            
//              System.out.println("Verify State: " + attendance.getVerifyState());
//              System.out.println("Verify Type: " + attendance.getVerifyType());
//              System.out.println("Record Time: " + attendance.getRecordTime());
//              System.out.println("------------------------");
//        }
//    } catch (IOException e) {
//        e.printStackTrace(); // Handle exceptions appropriately
//    }   
      
//        ZKTecoHttpServer xk =new ZKTecoHttpServer(8000);
//        
//        terminal.getYourSmsList(01);	
//      try {
//      List<SmsInfo> SmsInfo = terminal.getYourSmsList(01);
//
//      // Access and print user information
//      for (SmsInfo smsinfo : SmsInfo) {
//            System.out.println("tag: " + smsinfo.getTag());
//            System.out.println("ID: " + smsinfo.getID());
//            System.out.println("validMinutes: " + smsinfo.getID());
//            System.out.println("reserved: " + smsinfo.getReserved());
//            System.out.println("startTime: " + smsinfo.getStartTime());
//            System.out.println("content: " + smsinfo.getContent());
//            System.out.println("------------------------");
//      }
//  } catch (IOException e) {
//      e.printStackTrace(); // Handle exceptions appropriately
//  }              
//        
//        reply = terminal.delSMS(01);

//      System.out.println(reply.getCode());
//      reply = terminal.enableDevice();
//
//      reply = terminal.enableRealtime(EventCode.EF_FINGER);//enableDevice();

//      System.out.println(terminal.getCommKey());
//      
//      reply = terminal.setCommKey(0);


//      while (true) {
//          int[] response = terminal.readResponse();
//          
//          System.out.println(HexUtils.bytesToHex(response));
//      }
      
        
        
//        reply = terminal.syncTime();
//      reply = terminal.FreeDeviceBuffer();
//        reply = terminal.RefreshData();

        // Update
//        UserInfo user  = new UserInfo(83,"83", "Gokul G", "6567", UserRole.USER_DEFAULT, 2111);
//        reply = terminal.modifyUserInfo(user);

//        System.out.println(reply.getCode());

//        reply = terminal.enableDevice();
//        System.out.println(reply.getCode());

//        reply = terminal.Poweroff();
//    System.out.println(terminal.isVoiceOn());

//      reply = terminal.ClearAdminData();       
//      reply = terminal.enableDevice();

//      reply = terminal.FreeDeviceBuffer();
//        reply = terminal.RefreshData();
//        reply = terminal.Poweroff();
//        reply = terminal.delUser(80);
//        reply = terminal.setUserGroup(9,1);
        
//      System.out.println(terminal.getDeviceIP());
//      reply = terminal.setIPAddress("192.168.1.201");

//      System.out.println(terminal.getDeviceName());
//        System.out.println("free : " + terminal.getDeviceStatus().get("userCapacity"));
//        System.out.println("free : " + terminal.getDeviceStatus().get("faceCount"));
//      System.out.println("free : " + terminal.getDeviceStatus().get("userCount"));

//      System.out.println(reply.getCode());        
//      System.out.println("getDeviceTime : " + terminal.getDeviceTime());
//      System.out.println("getDeviceName :  " + terminal.getDeviceName());
//      System.out.println("getSerialNumber : " + terminal.getSerialNumber());
//      System.out.println("getMAC : " + terminal.getMAC());
//      System.out.println("getFaceVersion : " + terminal.getFaceVersion());
//      System.out.println("getPlatform : " + terminal.getPlatform());
//      System.out.println("getWorkCode : " + terminal.getWorkCode());
//      System.out.println("getFPVersion : " + terminal.getFPVersion());
//      System.out.println("getOEMVendor : " + terminal.getOEMVendor());

//        reply = terminal.disableDevice();
//        reply = terminal.triggerAlarm();

//        reply = terminal.setDeviceSms(new Date(),1000,"This is Test");
//        reply = terminal.syncTime();

//        System.out.println(reply.getCode());        
//        reply = terminal.testVoice(0);

        //        reply = terminal.ClearAdminData();
        //      reply = terminal.ClearAttLogData();

        //        reply = terminal.DeviceFree();
//        reply = terminal.enableRealtime();
//        System.out.println(reply.getCode());
        
//        System.out.println(reply.getCode());
//        Thread.sleep(5*1000);        
//        reply = terminal.WriteSMS(0,"Hi");
//        reply = terminal.getAttendanceRecords();
//        System.out.println(terminal.getDeviceTime());
//        reply = terminal.Poweroff();
//        reply = terminal.Sleep();
//        System.out.println(reply.getCode());
//        reply = terminal.disableDevice();
//        System.out.println(reply.getCode());
//        terminal.getDeviceTime();
//        reply = terminal.enableDevice();
//        System.out.println(reply.getCode()); 
//        reply = terminal.enableRealtime(EventCode.EF_FINGER);//enableDevice();
        
//        System.out.println(reply.getCode());
        
//        System.out.println("Reading");
        
        //restart work
//        reply = terminal.restart();
//        System.out.println("Restart : " +  reply.getCode() );
        
//        while (true) {
//            int[] response = terminal.readResponse();
//            
//            System.out.println(HexUtils.bytesToHex(response));
//        }
        
//
//        try {
//            List<UserInfo> userList = terminal.getAllUsers();
//
//            // Access and print user information
//            for (UserInfo user : userList) {
//	              System.out.println("User ID: " + user.getUserid());
//	              System.out.println("Name: " + user.getName());
//	              System.out.println("Password: " + user.getPassword());
//	              System.out.println("Card Number: " + user.getCardno());
//	              System.out.println("Group Number: " + user.getGroupNumber());
//	              System.out.println("User TimeZone Flag: " + user.getUserTimeZoneFlag());
//	              System.out.println("TimeZone1: " + user.getTimeZone1()); 
//	              System.out.println("TimeZone2: " + user.getTimeZone2());
//	              System.out.println("TimeZone3: " + user.getTimeZone3());
//	              System.out.println("User Serial Number: " + user.getUid());
//	              System.out.println("Role: " + user.getRole());
//                  System.out.println("------------------------");
//            }
//        } catch (IOException e) {
//            e.printStackTrace(); // Handle exceptions appropriately
//        }              
//        
//        try {
//            List<AttendanceRecord> attendanceRecord = terminal.getAttendanceRecords();
//
//            // Access and print user information
//            for (AttendanceRecord attendance : attendanceRecord) {
//                  System.out.println("User ID: " + attendance.getUserID());
//                  System.out.println("User SN: " + attendance.getUserSN());            
//                  System.out.println("Verify State: " + attendance.getVerifyState());
//                  System.out.println("Verify Type: " + attendance.getVerifyType());
//                  System.out.println("Record Time: " + attendance.getRecordTime());
//                  System.out.println("------------------------");
//            }
//        } catch (IOException e) {
//            e.printStackTrace(); // Handle exceptions appropriately
//        }              
//
        //terminal.disconnect();
        
        /*terminal.disconnect();
        
        ZKCommandReply reply = terminal.getAttendanceRecords();
        
        System.out.println(reply.getCode());*/

        /*if (reply.getCode() == CommandReplyCode.CMD_ACK_UNAUTH) {
            reply = terminal.connectAuth(155);
        }*/

    }
}
