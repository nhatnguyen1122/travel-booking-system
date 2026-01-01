package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.EmailDTO;
import edu.hust.travelbookingsystem.repository.OrderRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@Slf4j
public class EmailService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${app.email.sender}")
    private String senderEmail;

    public String sendEmail(EmailDTO emailDTO) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(senderEmail);
            helper.setTo(emailDTO.getToEmail());
            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getBody(), true);
            mailSender.send(mimeMessage);
            return "Email Sent";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "Lỗi khi gửi email" ;
        }
    }

    private String formatDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(DATE_FORMATTER);
    }

    private String buildOrderDetailsHtml(Order order) {
        String userName = order.getUser().getFullName();
        String destination = order.getDestination();
        int numberOfPeople = order.getNumberOfPeople();
        String checkInFormatted = formatDate(order.getCheckinDate());
        String checkOutFormatted = formatDate(order.getCheckoutDate());
        String hotelName = order.getHotel().getHotelName();
        String flightName = order.getFlight().getAirlineName();
        String flightTicketClass = order.getFlight().getTicketClass().toString();
        String totalPrice = String.valueOf(order.getTotalPrice());

        return "<b>Người đặt:</b> " + userName + "<br>" +
                "<b>Địa điểm:</b> " + destination + "<br>" +
                "<b>Số người:</b> " + numberOfPeople + "<br>" +
                "<b>Thời gian check-in:</b> " + checkInFormatted + "<br>" +
                "<b>Thời gian check-out:</b> " + checkOutFormatted + "<br>" +
                "<b>Tên hãng bay:</b> " + flightName + " - Hạng: " + flightTicketClass + "<br>" +
                "<b>Tên khách sạn:</b> " + hotelName + "<br>" +
                "<b>Tổng Chi Phí:</b> " + totalPrice + "<br><br>";
    }

    public Object sendAnnounceEmail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        EmailDTO emailDTO = new EmailDTO();

        String userName = order.getUser().getFullName();
        emailDTO.setToEmail(order.getUser().getEmail());
        emailDTO.setSubject("Cảm ơn quý ông/bà " + userName + " đã đặt chuyến đi của HUST WONDER");

        String body = "---------<b>Thông Tin Chi Tiết Chuyến Đi</b>--------- <br>" +
                buildOrderDetailsHtml(order) +
                "<i>Vui lòng sớm thanh toán để có một chuyến đi tuyệt vời.</i><br>" +
                "<b>HUST WONDER TRÂN TRỌNG CẢM ƠN!</b>";
        emailDTO.setBody(body);

        return sendEmail(emailDTO);
    }

    public Object sendAnnouncePaySuccessEmail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        EmailDTO emailDTO = new EmailDTO();

        emailDTO.setToEmail(order.getUser().getEmail());
        emailDTO.setSubject("THANH TOÁN CHUYẾN ĐI THÀNH CÔNG");

        String body = "------------------<b>XÁC NHẬN THANH TOÁN THÀNH CÔNG</b>------------------<br>" +
                "---------<b>Thông Tin Chi Tiết Chuyến Đi</b>--------- <br>" +
                buildOrderDetailsHtml(order) +
                "<b>HUST WONDER TRÂN TRỌNG CẢM ƠN!</b>";
        emailDTO.setBody(body);

        return sendEmail(emailDTO);
    }

    public Object sendAnnouncePayFalledEmail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        EmailDTO emailDTO = new EmailDTO();

        emailDTO.setToEmail(order.getUser().getEmail());
        emailDTO.setSubject("THANH TOÁN CHUYẾN ĐI THẤT BẠI");

        String body = "------------------<b>THANH TOÁN THẤT BẠI</b>------------------<br>" +
                "---------------<b>HUST WONDER rất tiếc khi phải thông báo rằng bạn đã thanh toán không thành công , vui lòng kiểm tra lại</b><br>" +
                "---------<b>Thông Tin Chi Tiết Chuyến Đi</b>--------- <br>" +
                buildOrderDetailsHtml(order) +
                "<b>HUST WONDER TRÂN TRỌNG CẢM ƠN!</b>";
        emailDTO.setBody(body);

        return sendEmail(emailDTO);
    }

    public Object sendAnnouceCancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        EmailDTO emailDTO = new EmailDTO();

        emailDTO.setToEmail(order.getUser().getEmail());
        emailDTO.setSubject("HỦY CHUYẾN THÀNH CÔNG");

        String body = "------------------<b>HỦY CHUYẾN THÀNH CÔNG</b>------------------<br>" +
                "---------------<b>HUST WONDER rất tiếc khi không thể đồng hành cùng bạn trong chuyến đi lần này ! </b><br>" +
                "Hẹn quý khách trong một tương lai gần nhất<br>" +
                "---------<b>Thông Tin Chi Tiết Chuyến Đi</b>--------- <br>" +
                buildOrderDetailsHtml(order) +
                "<b>HUST WONDER TRÂN TRỌNG CẢM ƠN!</b>";
        emailDTO.setBody(body);

        return sendEmail(emailDTO);
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setToEmail(toEmail);
        emailDTO.setSubject("HUST WONDER - Password Reset Request");

        String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #29b862;'>Password Reset Request</h2>" +
                "<p>You have requested to reset your password for your HUST WONDER account.</p>" +
                "<p>Click the button below to reset your password. This link will expire in <b>1 hour</b>.</p>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + resetLink + "' style='background-color: #29b862; color: white; padding: 12px 30px; " +
                "text-decoration: none; border-radius: 5px; font-weight: bold;'>Reset Password</a>" +
                "</div>" +
                "<p>If you did not request this password reset, please ignore this email.</p>" +
                "<p>If the button doesn't work, copy and paste this link into your browser:</p>" +
                "<p style='word-break: break-all; color: #666;'>" + resetLink + "</p>" +
                "<hr style='margin: 30px 0; border: none; border-top: 1px solid #eee;'>" +
                "<p style='color: #999; font-size: 12px;'>This is an automated email from HUST WONDER. Please do not reply.</p>" +
                "</div>";
        emailDTO.setBody(body);

        sendEmail(emailDTO);
    }
}
