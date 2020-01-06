package com.spike.kafkasteam;

class Transaction {
    String ifscCode;
    String transactionId;

    public Transaction(String ifscCode, String transactionId) {
        this.ifscCode = ifscCode;
        this.transactionId = transactionId;
    }

    public String getIfscCode() {
        return ifscCode;
    }
}

class BankMaster {
    String ifscCode;
    String branchName;
}

class ResolvedTransaction extends Transaction {
    String branchName;

    public ResolvedTransaction(String ifscCode, String transactionId, String branchName) {
        super(ifscCode, transactionId);
        this.branchName = branchName;
    }
}

