package com.spike.kafkasteam.models;

public class ResolvedTransactionOutlet extends ResolvedTransaction {
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
