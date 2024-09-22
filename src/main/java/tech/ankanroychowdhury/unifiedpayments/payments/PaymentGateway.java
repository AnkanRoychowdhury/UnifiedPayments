package tech.ankanroychowdhury.unifiedpayments.payments;
import java.util.*;

public interface PaymentGateway {
    // Method to authorize a payment
    Map<String, Object> authorizePayment(String currencyCode, String value) throws Exception;
    // Method to capture a payment
    Map<String, Object> capturePayment(String orderId) throws Exception;
    // Method to refund a payment
    Map<String, Object> refundPayment(String captureId, String value, String currencyCode) throws Exception;
}
