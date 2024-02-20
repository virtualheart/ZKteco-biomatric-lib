package com.zkteco.iclockhelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {        
//    	_ParsedRequest.fromReq(null);
    	
      ZKTecoHttpServer xk =new ZKTecoHttpServer(8000);
      
      
    }
}

