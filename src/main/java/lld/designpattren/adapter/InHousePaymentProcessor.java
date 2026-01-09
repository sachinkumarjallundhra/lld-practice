package lld.designpattren.adapter;

class InHousePaymentProcessor implements PaymentProcessor {
    private String transactionId;
    private boolean isPaymentSuccessful;

    @Override
    public void processPayment(double amount, String currency) {
        System.out.println("InHousePaymentProcessor: Processing payment of " + amount + " " + currency);
        transactionId = "TXN_" + System.currentTimeMillis();
        isPaymentSuccessful = true;
        System.out.println("InHousePaymentProcessor: Payment successful. Txn ID: " + transactionId);
    }

    @Override
    public boolean isPaymentSuccessful() {
        return isPaymentSuccessful;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }
}
