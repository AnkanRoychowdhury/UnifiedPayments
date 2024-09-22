package tech.ankanroychowdhury.unifiedpayments.payments;

import tech.ankanroychowdhury.unifiedpayments.providers.paypal.PayPalGatewayBuilder;
import tech.ankanroychowdhury.unifiedpayments.providers.paypal.PaypalPaymentGateway;

public class PaymentGatewayBuilder {
    private PaymentGateway gateway;

    private PaymentGatewayBuilder() {}

    public static PaymentGatewayBuilder builder() {
        return new PaymentGatewayBuilder();
    }

    public PaymentGatewayBuilder usePaypal(PayPalGatewayBuilder paypalBuilder) {
        this.gateway = new PaypalPaymentGateway(paypalBuilder);
        return this;
    }

    public PaymentGateway build(){
        if(this.gateway == null) {
            throw new IllegalStateException("No payment gateway found");
        }
        return this.gateway;
    }
}
