package com.zkteco.commands;

public class SmsInfo {
    private final int tag;
    private final int id;
    private final int validMinutes;
    private final int reserved;
    private final long startTime;

    private final String content;

    public SmsInfo(int tag, int id, int validMinutes, int reserved, long startTime, String content) {
        this.tag = tag;
        this.id = id;
        this.validMinutes = validMinutes;
        this.reserved = reserved;
        this.startTime = startTime;
        this.content = content;
    }

	public int getTag() {
		return tag;
	}

	public int getId() {
		return id;
	}

	public int getValidMinutes() {
		return validMinutes;
	}

	public int getReserved() {
		return reserved;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getContent() {
		return content;
	}


}
