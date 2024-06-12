package com.zkteco.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class HexUtils {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(int[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    public static String byteArrayToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static Date extractDate(long encDate) throws ParseException {
        long second = encDate % 60;
        encDate = encDate / 60;
        long minute = encDate % 60;
        encDate = encDate / 60;
        long hour = encDate % 24;
        encDate = encDate / 24;
        long day = encDate % 31 + 1;
        encDate = encDate / 31;
        long month = encDate % 12 + 1;
        encDate = encDate / 12;
        long year = (long) Math.floor(encDate + 2000);

        String dateStr = year + "-"
                + StringUtils.leftPad(String.valueOf(month), 2, "0") + "-"
                + StringUtils.leftPad(String.valueOf(day), 2, "0") + " "
                + StringUtils.leftPad(String.valueOf(hour), 2, "0") + ":"
                + StringUtils.leftPad(String.valueOf(minute), 2, "0") + ":"
                + StringUtils.leftPad(String.valueOf(second), 2, "0");

        Date date = DATE_FORMAT.parse(dateStr);

        return date;
    }
    
    public static byte[] intToBytes(int value, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[length - i - 1] = (byte) (value >> (i * 8));
        }
        return result;
    }
    
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even number of characters");
        }
        byte[] data = new byte[len / 2];

        
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                                 + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }
    
    
    public static int[] convertLongToLittleEndian(long value) {
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (int) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }
    
    public static long encodeTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Formula taken from zkemsdk.c - EncodeTime
        // Can also be found in the technical manual
        return (((calendar.get(Calendar.YEAR) % 100) * 12 * 31 +
                ((calendar.get(Calendar.MONTH) - 1) * 31) +
                calendar.get(Calendar.DAY_OF_MONTH) - 1) *
                (24 * 60 * 60) +
                (calendar.get(Calendar.HOUR_OF_DAY) * 60 + 
                 calendar.get(Calendar.MINUTE)) * 60 +
                calendar.get(Calendar.SECOND));
    }

    public static long convertToSeconds() {
        LocalDateTime t = LocalDateTime.now();
        long d = (((t.getYear() % 100) * 12 * 31 + ((t.getMonthValue() - 1) * 31) + t.getDayOfMonth() - 1) *
                (24 * 60 * 60) + (t.getHour() * 60 + t.getMinute()) * 60 + t.getSecond());

        return d;
    }


    public static String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }


    
}
