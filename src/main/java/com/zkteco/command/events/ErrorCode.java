package com.zkteco.command.events;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCode {
	
    // Connection error codes
    CONNECTED_SUCCESSFULLY(0, "Connected successfully"),
    FAILED_TO_INVOKE_INTERFACE(1, "Failed to invoke the interface"),
    FAILED_TO_INITIALIZE(2, "Failed to initialize"),
    FAILED_TO_INITIALIZE_PARAMETERS(3, "Failed to initialize parameters"),
    DATA_MODE_READ_ERROR(5, "Data mode read error"),
    WRONG_PASSWORD(6, "Wrong password"),
    REPLY_ERROR(7, "Reply error"),
    RECEIVE_TIMEOUT(8, "Receive timeout"),
    CONNECTION_TIMEOUT(307, "Connection timeout"),

    // Other interface invocation error codes
    DEVICE_BUSY(201, "Device is busy"),
    NEW_MODE(199, "New Mode"),
    FACE_VERSION_ERROR(103, "Device sends back an error of face version error"),
    FACE_TEMPLATE_VERSION_ERROR(102, "Face template version error, e.g., sending 8.0 face template to 7.0 device"),
    MALLOC_MEMORY_FAILED(101, "Malloc memory failed"),
    NOT_SUPPORTED_OR_DATA_NOT_EXIST(100, "Not supported or the data does not exist"),
    INCORRECT_LENGTH_OF_TRANSMITTED_DATA(10, "The length of transmitted data is incorrect"),
    DATA_ALREADY_EXISTS(5, "Data already exists"),
    INSUFFICIENT_SPACE(4, "Insufficient space"),
    WRONG_SIZE(3, "Wrong size"),
    FILE_READ_WRITE_ERROR(2, "File read/write error"),
    SDK_NOT_INITIALIZED(1, "The SDK is not initialized and needs to be reconnected"),
    DATA_NOT_FOUND_OR_DUPLICATE_DATA(0, "Data not found or duplicate data"),
    CORRECT_OPERATION(1, "Correct operation"),
    PARAMETER_ERROR(4, "Parameter error"),
    BUFFER_ALLOCATION_ERROR(101, "Buffer allocation error"),
    REPEAT_INVOKING(102, "Repeat invoking"),

    // Socket error codes
    SOCKET_CREATION_TIMEOUT(12001, "Socket creation timeout (connection timeout)"),
    INSUFFICIENT_MEMORY(12002, "Insufficient memory"),
    WRONG_SOCKET_VERSION(12003, "Wrong Socket version"),
    NOT_TCP_PROTOCOL(12004, "Not TCP protocol"),
    WAITING_TIMEOUT(12005, "Waiting timeout"),
    DATA_TRANSMISSION_TIMEOUT(12006, "Data transmission timeout"),
    DATA_READING_TIMEOUT(12007, "Data reading timeout"),
    FAILED_TO_READ_SOCKET(12008, "Failed to read Socket"),
    WAITING_EVENT_ERROR(13009, "Waiting event error"),
    EXCEEDED_RETRY_ATTEMPTS(13010, "Exceeded retry attempts"),
    WRONG_REPLY_ID(13011, "Wrong reply ID"),
    CHECKSUM_ERROR(13012, "Checksum error"),
    WAITING_EVENT_TIMEOUT(13013, "Waiting event timeout"),
    DIRTY_DATA(13014, "DIRTY_DATA"),
    BUFFER_SIZE_TOO_SMALL(13015, "Buffer size too small"),
    WRONG_DATA_LENGTH(13016, "Wrong data length"),
    INVALID_DATA_READ1(13017, "Invalid data read1"),
    INVALID_DATA_READ2(13018, "Invalid data read2"),
    INVALID_DATA_READ3(13019, "Invalid data read3"),
    DATA_LOSS(13020, "Data loss"),
    MEMORY_INITIALIZATION_ERROR(13021, "Memory initialization error"),

    // Error codes for getdevicedata and setdevicedata invocation
    ERROR_IN_OBTAINING_TABLE_STRUCTURE(15100, "Error occurs in obtaining table structure"),
    CONDITION_FIELD_NOT_EXIST(15101, "The condition field does not exist in the table structure"),
    INCONSISTENCY_IN_TOTAL_NUMBER_OF_FIELDS(15102, "Inconsistency in the total number of fields"),
    INCONSISTENCY_IN_SORTING_FIELDS(15103, "Inconsistency in sorting fields"),
    MEMORY_ALLOCATION_ERROR(15104, "Memory allocation error"),
    DATA_PARSING_ERROR(15105, "Data parsing error"),
    DATA_OVERFLOW_EXCEEDS_4M(15106, "Data overflow as the transmitted data exceeds 4M"),
    INVALID_OPTIONS(15108, "Invalid options"),
    DATA_PARSING_ERROR_TABLE_ID_NOT_FOUND(15113, "Data parsing error: table ID not found"),
    DATA_EXCEPTION_SMALLER_THAN_OR_EQUAL_TO_0(15114, "A data exception is returned as the number of fields is smaller than or equal to 0"),
    DATA_EXCEPTION_INCONSISTENT_TOTAL_NUMBER_OF_TABLE_FIELDS(15115, "A data exception is returned as the total number of table fields is inconsistent with the total number of fields of the data"),

    // Firmware error codes
    RETURN_OK_TO_EXECUTE(2000, "Return OK to execute"),
    RETURN_FAIL_TO_EXECUTE_COMMAND(2001, "Return Fail to execute command"),
    RETURN_DATA(2002, "Return Data"),
    REGISTERED_EVENT_OCCURRED(2003, "Registered event occurred"),
    RETURN_REPEAT_COMMAND(2004, "Return REPEAT Command"),
    RETURN_UNAUTH_COMMAND(2005, "Return UNAUTH Command"),
    RETURN_UNKNOWN_COMMAND(0xFFFF, "Return Unknown Command"),
    DEVICE_PARAM_READ_ERROR(4999, "Device parameter read error"),
    DEVICE_PARAM_WRITE_ERROR(4998, "Device parameter write error"),
    DATA_LENGTH_INCORRECT(4997, "The length of the data sent by the software to the device is incorrect (228, 229)"),
    PARAM_ERROR_IN_DATA(4996, "A parameter error exists in the data sent by the software to the device"),
    FAILED_TO_ADD_DATA_TO_DATABASE(4995, "Failed to add data to the database"),
    FAILED_TO_UPDATE_DATABASE(4994, "Failed to update the database"),
    FAILED_TO_READ_DATA_FROM_DATABASE(4993, "Failed to read data from the database"),
    FAILED_TO_DELETE_DATA_IN_DATABASE(4992, "Failed to delete data in the database"),
    DATA_NOT_FOUND_IN_DATABASE(4991, "Data not found in the database"),
    DATA_AMOUNT_IN_DATABASE_REACHES_LIMIT(4990, "The data amount in the database reaches the limit"),
    FAILED_TO_ALLOCATE_MEMORY_TO_SESSION(4989, "Failed to allocate memory to a session"),
    INSUFFICIENT_SPACE_IN_SESSION_MEMORY(4988, "Insufficient space in the memory allocated to a session"),
    SESSION_MEMORY_OVERFLOW(4987, "The memory allocated to a session overflows"),
    FILE_NOT_EXIST(4986, "File does not exist"),
    FILE_READ_FAILURE(4985, "File read failure"),
    FILE_WRITE_FAILURE(4984, "File write failure"),
    FAILED_TO_CALCULATE_HASH_VALUE(4983, "Failed to calculate the hash value"),
    FAILED_TO_ALLOCATE_MEMORY(-4982, "Failed to allocate memory");
    
    private final int code;
    private final String errorMessage;

    // Map to store ErrorCode instances by their error code
    private static final Map<Integer, ErrorCode> BY_CODE = new HashMap<>();

    static {
        for (ErrorCode errorCode : values()) {
            BY_CODE.put(errorCode.code, errorCode);
        }
    }

    private ErrorCode(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Static method to get ErrorCode instance based on the error code
    public static ErrorCode getByCode(int code) {
        return BY_CODE.get(code);
    }
}
