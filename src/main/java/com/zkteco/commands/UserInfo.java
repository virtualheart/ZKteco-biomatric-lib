package com.zkteco.commands;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.zkteco.Enum.UserRoleEnum;

public class UserInfo {

    private int uid;
    private UserRoleEnum role;
    private String password;
    private String name;
    private long cardno;
    private String userid;
    private String groupNumber;
    private int userTimeZoneFlag;
    private int timeZone1;
    private int timeZone2;
    private int timeZone3;
	private boolean enabled;

    
    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum userDefault) {
        this.role = userDefault;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCardno() {
        return cardno;
    }

    public void setCardno(long cardno) {
        this.cardno = cardno;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public int getUserTimeZoneFlag() {
        return userTimeZoneFlag;
    }

    public void setUserTimeZoneFlag(int userTimeZoneFlag) {
        this.userTimeZoneFlag = userTimeZoneFlag;
    }

    public int getTimeZone1() {
        return timeZone1;
    }

    public void setTimeZone1(int timeZone1) {
        this.timeZone1 = timeZone1;
    }

    public int getTimeZone2() {
        return timeZone2;
    }

    public void setTimeZone2(int timeZone2) {
        this.timeZone2 = timeZone2;
    }

    public int getTimeZone3() {
        return timeZone3;
    }

    public void setTimeZone3(int timeZone3) {
        this.timeZone3 = timeZone3;
    }
    
    public UserInfo(int uid,String user_id,String name,String password,UserRoleEnum privilege,long cardno) {
    	this.userid = user_id;
    	this.name = name;
    	this.role = privilege;
    	this.cardno = cardno;
    	this.password = password;
    	this.groupNumber = "0";
    	this.uid = uid;
    }

    public UserInfo(String user_id,String name,String password,UserRoleEnum privilege,long cardno) {
    	this.userid = user_id;
    	this.name = name;
    	this.role = privilege;
    	this.cardno = cardno;
    	this.password = password;
    	this.groupNumber = "0";
    }
    
   public UserInfo() {
    	
    }

    public static UserInfo encodeUser(ByteBuffer buffer, int userPacketSize) {
//        System.out.println(buffer.position());
    	UserInfo user = new UserInfo();

        byte permissionToken = buffer.get(2);
        int roleBits = (permissionToken >> 1) & 0x07; // Bits 3-1
        int userStateBit = permissionToken & 0x01; // Bit 0

        switch (roleBits) {
            case 0b000:
                user.setRole(UserRoleEnum.USER_DEFAULT);
                break;
            case 0b001:
                user.setRole(UserRoleEnum.USER_ENROLLER);
                break;
            case 0b011:
                user.setRole(UserRoleEnum.USER_MANAGER);
                break;
            case 0b111:
                user.setRole(UserRoleEnum.USER_ADMIN);
                break;
            default:
            	user.setRole(UserRoleEnum.USER_DEFAULT);
                break;
        }

        user.setEnabled(userStateBit == 0);

        byte[] data = new byte[userPacketSize];
        buffer.get(data);
        
        byte[] passwordByte = Arrays.copyOfRange(data, 3, 12);
        byte[] nameByte = Arrays.copyOfRange(data, 11, 35);
        byte[] userIdByte = Arrays.copyOfRange(data, 48, 57);
        byte[] pattern = new byte[]{0x00};
        
        passwordByte = split(pattern, passwordByte).get(0);
        nameByte = split(pattern, nameByte).get(0);

        user.setUid(Short.reverseBytes(buffer.getShort(0)));
        user.setPassword(new String(passwordByte));
        user.setName(new String(nameByte).trim());
        user.setCardno(Integer.reverseBytes(buffer.getInt(35)));
        user.setGroupNumber(String.valueOf(buffer.get(38)));
        user.setUserTimeZoneFlag(Short.reverseBytes(buffer.getShort(40)));
        user.setTimeZone1(Short.reverseBytes(buffer.getShort(42)));
        user.setTimeZone2(Short.reverseBytes(buffer.getShort(44)));
        user.setTimeZone3(Short.reverseBytes(buffer.getShort(46)));
        user.setUserid(new String(userIdByte));
        return user;
    }

		public static List<byte[]> split(byte[] pattern, byte[] input) {
		    List<byte[]> l = new LinkedList<byte[]>();
		    int blockStart = 0;
		    for(int i=0; i<input.length; i++) {
		       if(isMatch(pattern,input,i)) {
		          l.add(Arrays.copyOfRange(input, blockStart, i));
		          blockStart = i+pattern.length;
		          i = blockStart;
		       }
		    }
		    l.add(Arrays.copyOfRange(input, blockStart, input.length ));
		    return l;
		}
		
		public static boolean isMatch(byte[] pattern, byte[] input, int pos) {
		    for(int i=0; i< pattern.length; i++) {
		        if(pattern[i] != input[pos+i]) {
		            return false;
		        }
		    }
		    return true;
		}
}
