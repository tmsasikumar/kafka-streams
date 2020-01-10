package com.spike.kafkasteam.models;

public class ResolvedTransaction {
    String branchName;
    String ifscCode;
    String transactionId;
    String description;
    String customerId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ResolvedTransaction() {
    }

    public ResolvedTransaction(String ifscCode, String transactionId, String customerId, String description, String branchName) {
        this.ifscCode = ifscCode;
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.description = description;
        this.branchName = branchName;

    }
}
