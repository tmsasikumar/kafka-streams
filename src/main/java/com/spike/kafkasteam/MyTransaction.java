package com.spike.kafkasteam;

class MyTransaction {

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    String transactionId;
    String description;
    String customerId;
    String ifscCode;
}

class BankMaster {
    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    String ifscCode;
    String branchName;
}

class OutletTransaction {
    String transactionId;
    String outletName;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }
}

class ResolvedTransaction {
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

    ResolvedTransaction(String ifscCode, String transactionId, String customerId, String description, String branchName) {
        this.ifscCode = ifscCode;
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.description = description;
        this.branchName = branchName;

    }
}

class ResolvedTransactionOutlet extends ResolvedTransaction {
    String outletName;

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public ResolvedTransactionOutlet(ResolvedTransaction transactionData, String outletName) {
        super(transactionData.ifscCode, transactionData.transactionId, transactionData.customerId, transactionData.description, transactionData.branchName);
        this.outletName = outletName;
    }
}



