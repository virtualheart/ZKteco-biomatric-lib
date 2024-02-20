package com.zkteco.iclockhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OperationLog {
    private final BufferedReader raw;
    private final List<User> users;
    private final List<Fingerprint> fingerprints;
    private final List<Operation> operations;

    public OperationLog(BufferedReader br, List<User> users, List<Fingerprint> fingerprints, List<Operation> operations) {
        this.raw = br;
        this.users = users;
        this.fingerprints = fingerprints;
        this.operations = operations;
    }

    public BufferedReader getRaw() {
        return raw;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Fingerprint> getFingerprints() {
        return fingerprints;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public static OperationLog fromStr(BufferedReader br) throws IOException, IllegalAccessException, InstantiationException {
        List<User> users = new ArrayList<>();
        List<Fingerprint> fingerprints = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] ops = line.split(" ", 2);

            if (ops[0].equals("OPLOG")) {
                operations.add(Operation.fromStr(ops[1]));
            } else if (ops[0].equals("USER")) {
                users.add(User.fromStr1(ops[1]));
            } else if (ops[0].equals("FP")) {
                fingerprints.add(Fingerprint.fromStr(ops[1]));
            }
        }

        return new OperationLog(br, users, fingerprints, operations);
    }

}