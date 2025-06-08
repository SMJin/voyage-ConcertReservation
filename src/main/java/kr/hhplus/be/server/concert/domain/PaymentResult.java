package kr.hhplus.be.server.concert.domain;

public class PaymentResult {

    private final boolean success;
    private final String transactionId;

    public PaymentResult(boolean success, String transactionId) {
        this.success = success;
        this.transactionId = transactionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public static PaymentResult success(String transactionId) {
        return new PaymentResult(true, transactionId);
    }

    public static PaymentResult fail() {
        return new PaymentResult(false, null);
    }
}
