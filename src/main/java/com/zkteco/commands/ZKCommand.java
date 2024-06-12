package com.zkteco.commands;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.zkteco.Enum.CommandCodeEnum;
import com.zkteco.utils.SecurityUtils;

public class ZKCommand {
    public final static int[] PACKET_START = {0x50, 0x50, 0x82, 0x7d};

    public static int[] getPacket(CommandCodeEnum commandCode, int sessionId, int replyNumber, int[] data) {
        int[] payloadForChecksum = new int[6 + (data == null ? 0 : data.length)];
        int[] finalPayload = new int[8 + (data == null ? 0 : data.length)];
//        int[] finalPacket = new int[8 + finalPayload.length];

        byte[] commandBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(commandCode.getCode()).array();
        byte[] sessionIdBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(sessionId).array();
        byte[] replyNumberBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(replyNumber).array();

        payloadForChecksum[0] = commandBytes[0] & 0xFF;
        payloadForChecksum[1] = commandBytes[1] & 0xFF;
        finalPayload[0] = commandBytes[0] & 0xFF;
        finalPayload[1] = commandBytes[1] & 0xFF;

        payloadForChecksum[2] = sessionIdBytes[0] & 0xFF;
        payloadForChecksum[3] = sessionIdBytes[1] & 0xFF;
        finalPayload[4] = sessionIdBytes[0] & 0xFF;
        finalPayload[5] = sessionIdBytes[1] & 0xFF;

        payloadForChecksum[4] = replyNumberBytes[0] & 0xFF;
        payloadForChecksum[5] = replyNumberBytes[1] & 0xFF;
        finalPayload[6] = replyNumberBytes[0] & 0xFF;
        finalPayload[7] = replyNumberBytes[1] & 0xFF;

        if (data != null) {
            System.arraycopy(data, 0, payloadForChecksum, 6, data.length);
            System.arraycopy(data, 0, finalPayload, 8, data.length);
        }

        int checksum = SecurityUtils.calculateChecksum(payloadForChecksum);

        byte[] checksumBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(checksum).array();

        finalPayload[2] = checksumBytes[0] & 0xFF;
        finalPayload[3] = checksumBytes[1] & 0xFF;

//        byte[] payloadSize = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(finalPayload.length).array();
//
//        /* Preparing final packet */
//        System.arraycopy(PACKET_START, 0, finalPacket, 0, PACKET_START.length);
//
//        finalPacket[4] = payloadSize[0];
//        finalPacket[5] = payloadSize[1];
//        finalPacket[6] = payloadSize[2];
//        finalPacket[7] = payloadSize[3];
//
//        System.arraycopy(finalPayload, 0, finalPacket, 8, finalPayload.length);

        return finalPayload;
    }
    
    public static int[] getPacketByte(CommandCodeEnum commandCode, int sessionId, int replyNumber, byte[] data) {
        int[] payloadForChecksum = new int[6 + (data == null ? 0 : data.length)];
        int[] finalPayload = new int[8 + (data == null ? 0 : data.length)];
//        int[] finalPacket = new int[8 + finalPayload.length];

        byte[] commandBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(commandCode.getCode()).array();
        byte[] sessionIdBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(sessionId).array();
        byte[] replyNumberBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(replyNumber).array();

        payloadForChecksum[0] = commandBytes[0] & 0xFF;
        payloadForChecksum[1] = commandBytes[1] & 0xFF;
        finalPayload[0] = commandBytes[0] & 0xFF;
        finalPayload[1] = commandBytes[1] & 0xFF;

        payloadForChecksum[2] = sessionIdBytes[0] & 0xFF;
        payloadForChecksum[3] = sessionIdBytes[1] & 0xFF;
        finalPayload[4] = sessionIdBytes[0] & 0xFF;
        finalPayload[5] = sessionIdBytes[1] & 0xFF;

        payloadForChecksum[4] = replyNumberBytes[0] & 0xFF;
        payloadForChecksum[5] = replyNumberBytes[1] & 0xFF;
        finalPayload[6] = replyNumberBytes[0] & 0xFF;
        finalPayload[7] = replyNumberBytes[1] & 0xFF;

        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                payloadForChecksum[6 + i] = data[i] & 0xFF;
                finalPayload[8 + i] = data[i] & 0xFF;
            }
        }

        int checksum = SecurityUtils.calculateChecksum(payloadForChecksum);

        byte[] checksumBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(checksum).array();

        finalPayload[2] = checksumBytes[0] & 0xFF;
        finalPayload[3] = checksumBytes[1] & 0xFF;

        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(finalPayload.length).array();

        return finalPayload;
    }
}
