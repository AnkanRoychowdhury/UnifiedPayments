package tech.ankanroychowdhury.unifiedpayments.providers.paypal;


import tech.ankanroychowdhury.unifiedpayments.payments.PaymentGateway;

import java.util.Map;

public class PaypalPaymentGateway implements PaymentGateway {

    private final PayPalGatewayBuilder builder;

    public PaypalPaymentGateway(PayPalGatewayBuilder builder) {
        this.builder = builder;
    }

    @Override
    public Map<String, Object> authorizePayment(String currencyCode, String value) throws Exception {
        return builder
                .apiMethod("create-order")
                .addRequestData("currency_code", currencyCode)
                .addRequestData("value", value)
                .execute();
    }

    @Override
    public Map<String, Object> capturePayment(String orderId) throws Exception {
        return builder
                .apiMethod("capture-order")
                .addRequestData("order_id", orderId)
                .execute();
    }

    @Override
    public Map<String, Object> refundPayment(String captureId, String value, String currencyCode) throws Exception {
        return builder
                .apiMethod("refundOrder")
                .addRequestData("capture_id", captureId)
                .addRequestData("value", value)
                .addRequestData("currency_code", currencyCode)
                .execute();
    }
}
