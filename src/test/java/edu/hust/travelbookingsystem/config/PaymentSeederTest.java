package edu.hust.travelbookingsystem.config;

import edu.hust.travelbookingsystem.entity.Payment;
import edu.hust.travelbookingsystem.enums.PaymentStatus;
import edu.hust.travelbookingsystem.repository.PayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class PaymentSeederTest {

    @Mock
    private PayRepository payRepository;

    @InjectMocks
    private PaymentSeeder paymentSeeder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Khởi tạo các mock
    }

    @Test
    void testPaymentSeederWhenPaymentsAreEmpty() {
        // Giả lập hành vi của phương thức count() trong PayRepository
        when(payRepository.count()).thenReturn(0L);  // Giả lập không có bản ghi thanh toán trong DB

        // Chạy phương thức paymentSeeder() của PaymentSeeder
        paymentSeeder.paymentSeeder();

        // Kiểm tra xem phương thức save() có được gọi với các Payment đúng trạng thái hay không
        verify(payRepository).save(argThat(payment -> payment.getStatus() == PaymentStatus.PAID));
        verify(payRepository).save(argThat(payment -> payment.getStatus() == PaymentStatus.UNPAID));
        verify(payRepository).save(argThat(payment -> payment.getStatus() == PaymentStatus.VERIFYING));
        verify(payRepository).save(argThat(payment -> payment.getStatus() == PaymentStatus.PAYMENT_FAILED));
    }

    @Test
    void testPaymentSeederWhenPaymentsExist() {
        // Giả lập hành vi của phương thức count() trong PayRepository
        when(payRepository.count()).thenReturn(1L);  // Giả lập đã có dữ liệu thanh toán trong DB

        // Chạy phương thức paymentSeeder()
        paymentSeeder.paymentSeeder();

        // Kiểm tra rằng phương thức save() không được gọi khi đã có dữ liệu
        verify(payRepository, never()).save(any(Payment.class));
    }
}
