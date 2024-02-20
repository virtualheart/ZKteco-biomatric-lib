package com.zkteco.iclockhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class ParsedRequest {
    private final HttpExchange exchange;
    private final String method;
    private final URI parseresult;
    private final Map<String, Object> params;
    private final byte[] body;
    private final Headers headers;

    public ParsedRequest(HttpExchange exchange, String method, URI parseresult, Map<String, Object> params, byte[] body, Headers headers) {
        this.exchange = exchange;
        this.method = method;
        this.parseresult = parseresult;
        this.params = params;
        this.body = body;
        this.headers = headers;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public String getMethod() {
        return method;
    }

    public URI getParseresult() {
        return parseresult;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public byte[] getBody() {
        return body;
    }

    public Headers getHeaders() {
        return headers;
    }

    public static ParsedRequest fromHttpExchange(HttpExchange exchange) throws IOException, URISyntaxException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();

        Map<String, Object> params = parseQueryParameters(uri.getQuery());

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        Headers headers = exchange.getRequestHeaders();

        return new ParsedRequest(exchange, method, uri, params, body.toString().getBytes(), headers);
    }

    private static Map<String, Object> parseQueryParameters(String query) throws URISyntaxException {
        Map<String, Object> params = new HashMap<>();

        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : "";
                params.put(key, value);
            }
        }

        return params;
    }
    

}
