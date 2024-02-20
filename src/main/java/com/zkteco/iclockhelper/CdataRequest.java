package com.zkteco.iclockhelper;

import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.zkteco.Enum.TableEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CdataRequest extends ZKRequest {

	private final String method;
    private final String pin;
    private final boolean save;
    private final String body;
    private final String stamp;
    private final String operationStamp;
    private final TableEnum table;
    private final AttendanceLog attendanceLog;
    private final OperationLog operationLog;
    private final AttendancePhotoLog attendancePhotoLog;
    private final String options;
    private final String info;
    
    public String getMethod() {
		return method;
	}

	public String getPin() {
		return pin;
	}

	public boolean isSave() {
		return save;
	}

	public String getBody() {
		return body;
	}

	public String getStamp() {
		return stamp;
	}

	public String getOperationStamp() {
		return operationStamp;
	}

	public TableEnum getTable() {
		return table;
	}

	public AttendanceLog getAttendanceLog() {
		return attendanceLog;
	}

	public OperationLog getOperationLog() {
		return operationLog;
	}

	public AttendancePhotoLog getAttendancePhotoLog() {
		return attendancePhotoLog;
	}

    
    public CdataRequest(String sn, String pushVersion, String method, String pin, boolean save, String body, String stamp, String operationStamp, TableEnum table, AttendanceLog attendanceLog, OperationLog operationLog, AttendancePhotoLog attendancePhotoLog,String options) {
        super(sn, pushVersion);
        this.method = method;
        this.pin = pin;
        this.save = save;
        this.body = body;
        this.stamp = stamp;
        this.operationStamp = operationStamp;
        this.table = table;
        this.attendanceLog = attendanceLog;
        this.operationLog = operationLog;
        this.attendancePhotoLog = attendancePhotoLog;
        this.options=options;
		this.info = "";
    }

    public static CdataRequest fromReq(HttpExchange exchange) throws Exception, IOException {
        String requestMethod = exchange.getRequestMethod();
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String query = exchange.getRequestURI().getQuery();
        ParsedRequest parsedReq = ParsedRequest.fromHttpExchange(exchange);
        String[] sn = RequestUtils.extractSnVersion(parsedReq);

        Map<String, Object> uri = RequestUtils.StringtoMaps(query);
        Map<String, Object> action = RequestUtils.buffertoMaps(br);

        if (uri == null) {
            return new CdataRequest(sn[0], sn[1], requestMethod, "", false, "", "", "", TableEnum.UNKNOWN, null, null, null, null);
        }

        if ("GET".equals(requestMethod)) {
            return new CdataRequest(sn[0], sn[1], requestMethod, "", true, "", "", "", TableEnum.UNKNOWN, null, null, null, null);
        }

        if ("POST".equals(requestMethod)) {
            String stamp = getStringOrNull(uri, "Stamp");
            String operationStamp = getStringOrNull(uri, "OpStamp");
            TableEnum table = getTableEnumOrNull(uri, "table");

            AttendanceLog attLog = null;
            OperationLog operLog = null;
            AttendancePhotoLog attPhotoLog = null;

            if (table == TableEnum.OPERLOG) {
                operLog = OperationLog.fromStr(br);
            }

            if (table == TableEnum.ATTLOG) {
//                 attLog = AttendanceLog.fromStr(br);
            }

            if (table == TableEnum.ATTPHOTO) {
                // attPhotoLog = AttendancePhotoLog.fromRequestPin(_fromMaps("PIN", "", parsedReq.getParams()), body);
            }

            return new CdataRequest(sn[0], sn[1], requestMethod, "", false, "Br", stamp, operationStamp, table, attLog, operLog, attPhotoLog, null);
        }

        return new CdataRequest(sn[0], sn[1], requestMethod, "", false, "", "", "", TableEnum.UNKNOWN, null, null, null, null);
    }

    // Helper method to get a string or return an empty string if null
    private static String getStringOrNull(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    // Helper method to get a TableEnum or return UNKNOWN if null or invalid
    private static TableEnum getTableEnumOrNull(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value != null) {
            try {
                return TableEnum.valueOf(value.toString());
            } catch (IllegalArgumentException e) {
                // Handle invalid enum value
            }
        }
        return TableEnum.UNKNOWN;
    }

    
}

