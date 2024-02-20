package com.zkteco.iclockhelper;

import java.util.Map;

public class Info {
    private String firmwareVersion;
    private String userCount;
    private String fpCount;
    private String transactionCount;
    private String ipAddress;
    private String fpAlgorithmVersion;
    private String faceAlgorithmVersion;
    private String requiredFaceCount;
    private String enrolledFaceCount;
    private boolean fingerprintFunction;
    private boolean faceFunction;
    private boolean userPhotoFunction;

    public Info() {
		// TODO Auto-generated constructor stub
	}

	
	public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getUserCount() {
        return userCount;
    }

    public String getFpCount() {
        return fpCount;
    }

    public String getTransactionCount() {
        return transactionCount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getFpAlgorithmVersion() {
        return fpAlgorithmVersion;
    }

    public String getFaceAlgorithmVersion() {
        return faceAlgorithmVersion;
    }

    public String getRequiredFaceCount() {
        return requiredFaceCount;
    }

    public String getEnrolledFaceCount() {
        return enrolledFaceCount;
    }

    public boolean isFingerprintFunction() {
        return fingerprintFunction;
    }

    public boolean isFaceFunction() {
        return faceFunction;
    }

    public boolean isUserPhotoFunction() {
        return userPhotoFunction;
    }

    public Info(Map<String, Object> infoData) {
        this.firmwareVersion = (String) infoData.get("firmwareVersion");
        this.userCount = (String) infoData.get("userCount");
        this.fpCount = (String) infoData.get("fpCount");
        this.transactionCount = (String) infoData.get("transactionCount");
        this.ipAddress = (String) infoData.get("ipAddress");
        this.fpAlgorithmVersion = (String) infoData.get("fpAlgorithmVersion");
        this.faceAlgorithmVersion = (String) infoData.get("faceAlgorithmVersion");
        this.requiredFaceCount = (String) infoData.get("requiredFaceCount");
        this.enrolledFaceCount = (String) infoData.get("enrolledFaceCount");
        this.fingerprintFunction = (boolean) infoData.getOrDefault("fingerprintFunction", false);
        this.faceFunction = (boolean) infoData.getOrDefault("faceFunction", false);
        this.userPhotoFunction = (boolean) infoData.getOrDefault("userPhotoFunction", false);
    }
}
