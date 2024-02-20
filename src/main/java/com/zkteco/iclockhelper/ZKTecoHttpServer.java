package com.zkteco.iclockhelper;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ZKTecoHttpServer {

    public ZKTecoHttpServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/iclock/cdata.aspx", new CdataHandler());
        server.createContext("/iclock/getrequest.aspx", new GetRequestHandler());
        server.createContext("/iclock/devicemd.aspx", new DeviceMdHandler());
        server.createContext("/iclock/edata.aspx", new EdataHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }

    static class CdataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String requestUri = exchange.getRequestURI().toString();
            String query = exchange.getRequestURI().getQuery();

            System.out.println("Received " + requestMethod + " request: " + requestUri);
            System.out.println("Query parameters: " + query);

            if (requestUri.startsWith("/iclock/cdata.aspx")) {
                try {
                    CdataRequest cdataRequest = CdataRequest.fromReq(exchange);
                    System.out.println("Processing CdataRequest: " + cdataRequest);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, "Error processing CdataRequest");
                }
            }
        }
    }

    static class GetRequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String requestUri = exchange.getRequestURI().toString();
            String query = exchange.getRequestURI().getQuery();

            System.out.println("Received " + requestMethod + " request: " + requestUri);
            System.out.println("Query parameters: " + query);

            if (requestUri.startsWith("/iclock/getrequest.aspx")) {
                // Handle getrequest.aspx requests (GET)
                try {
                    GetRequest getRequest = GetRequest.fromReq(exchange);
                    System.out.println("Processing GetRequest: " + getRequest);
                    
                    System.out.println(getRequest.getSn());
                    System.out.println(getRequest.getPushVersion());
                                        
                    
//                    sendResponse(exchange, "OK");
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, "Error processing GetRequest");
                }
            }
        }
    }

    static class DeviceMdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle /iclock/devicemd.aspx (POST)
            String requestMethod = exchange.getRequestMethod();
            String requestUri = exchange.getRequestURI().toString();
            String query = exchange.getRequestURI().getQuery();

            System.out.println("Received " + requestMethod + " request: " + requestUri);
            System.out.println("Query parameters: " + query);

            if (requestUri.startsWith("/iclock/devicemd.aspx") && "POST".equals(requestMethod)) {
                try {
//                    DeviceMdRequest deviceMdRequest = DeviceMdRequest.fromReq(exchange);
//                    System.out.println("Processing DeviceMdRequest: " + deviceMdRequest);
//                    // Add your logic to process DeviceMdRequest
                    sendResponse(exchange, "OK");
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, "Error processing DeviceMdRequest");
                }
            }
        }
    }

    static class EdataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle /iclock/edata.aspx (POST)
            String requestMethod = exchange.getRequestMethod();
            String requestUri = exchange.getRequestURI().toString();
            String query = exchange.getRequestURI().getQuery();

            System.out.println("Received " + requestMethod + " request: " + requestUri);
            System.out.println("Query parameters: " + query);

            if (requestUri.startsWith("/iclock/edata.aspx") && "POST".equals(requestMethod)) {
                try {
//                    EdataRequest edataRequest = EdataRequest.fromReq(exchange);
//                    System.out.println("Processing EdataRequest: " + edataRequest);
//                    // Add your logic to process EdataRequest
//                    sendResponse(exchange, "OK");
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, "Error processing EdataRequest");
                }
            }
        }
    }

    
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        // Send the response back to the device
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
