package com.zkteco.iclockhelper;

public class ZKRequest {
    public String getSn() {
		return sn;
	}

	public String getPushVersion() {
		return pushVersion;
	}

	private final String sn;
    private final String pushVersion;

    public ZKRequest(String sn, String pushVersion) {
        this.sn = sn;
        this.pushVersion = pushVersion;
    }

}
