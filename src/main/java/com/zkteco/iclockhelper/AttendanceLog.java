package com.zkteco.iclockhelper;

import java.util.ArrayList;
import java.util.List;

public class AttendanceLog {
    private final String raw;
    private final List<Transaction> transactions;

    public AttendanceLog(String raw, List<Transaction> transactions) {
        this.raw = raw;
        this.transactions = transactions;
    }

    public String getRaw() {
        return raw;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public static AttendanceLog fromStr(String data) {
        List<Transaction> transactions = new ArrayList<>();

        for (String line : data.split("\n")) {
            transactions.add(Transaction.fromStr(line));
        }

        return new AttendanceLog(data, transactions);
    }
}
