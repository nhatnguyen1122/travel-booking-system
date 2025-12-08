package edu.hust.travelbookingsystem.config;

import edu.hust.travelbookingsystem.entity.Payment;
import edu.hust.travelbookingsystem.enums.PaymentStatus;
import edu.hust.travelbookingsystem.repository.PayRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentSeeder {
    @Autowired
    private PayRepository payRepository;
    public PaymentSeeder(PayRepository payRepository) {
        this.payRepository = payRepository;
    }
    @PostConstruct
    public void paymentSeeder() {
        if (payRepository.count() == 0) {
            Payment payment = new Payment(PaymentStatus.PAID);
            payRepository.save(payment);

            Payment paymentUnPaid = new Payment(PaymentStatus.UNPAID);
            payRepository.save(paymentUnPaid);

            Payment paymentVerifying = new Payment(PaymentStatus.VERIFYING);
            payRepository.save(paymentVerifying);

            Payment paymentFalled = new Payment(PaymentStatus.PAYMENT_FAILED) ;
            payRepository.save(paymentFalled);
        }
    }
}
