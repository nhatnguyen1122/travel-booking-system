package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.EmailDTO;
import edu.hust.travelbookingsystem.repository.OrderRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OrderRepository orderRepository;

    public String sendEmail(EmailDTO emailDTO) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom("nhatminh.st.27@gmail.com");
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

    public Object sendAnnounceEmail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        EmailDTO emailDTO = new EmailDTO();

        String userName = order.getUser().getFullName();
        String email = order.getUser().getEmail();
        String destination = order.getDestination();
        int numberOfPeople = order.getNumberOfPeople();

        Date checkinDate = order.getCheckinDate();
        LocalDate localDate = checkinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String datecheckInFormat = localDate.format(formatter);

        Date checkOutDate = order.getCheckoutDate();
        LocalDate localDate1 = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateCheckOutFormat= localDate1.format(formatter1);

        String hotelName = order.getHotel().getHotelName();
        String flightName = order.getFlight().getAirlineName() ;
        String flightTicketClass   = order.getFlight().getTicketClass().toString() ;
        String totalPrice = String.valueOf(order.getTotalPrice());

        emailDTO.setToEmail(email) ;
        String subject = "Cảm ơn quý ông/bà " + userName + " đã đặt chuyến đi của HUST WONDER" ;
        emailDTO.setSubject(subject);

        String body = "---------<b>Thông Tin Chi Tiết Chuyến Đi</b>--------- <br>" +
                "<b>Người đặt:</b> " + userName + "<br>" +
                "<b>Địa điểm:</b> " + destination + "<br>" +
                "<b>Số người:</b> " + numberOfPeople + "<br>" +
                "<b>Thời gian check-in:</b> " + datecheckInFormat + "<br>" +
                "<b>Thời gian check-out:</b> " + dateCheckOutFormat + "<br>" +
                "<b>Tên hãng bay:</b> " + flightName + " - Hạng: " + flightTicketClass + "<br>" +
                "<b>Tên khách sạn:</b> " + hotelName + "<br>" +
                "<b>Tổng Chi Phí:</b> " + totalPrice + "<br><br>" +
                "<i>Vui lòng sớm thanh toán để có một chuyến đi tuyệt vời.</i><br>" +
                "<b>HUST WONDER TRÂN TRỌNG CẢM ƠN!</b>";
        emailDTO.setBody(body) ;

        return sendEmail(emailDTO);

    }

    public Object sendAnnouncePaySuccessEmail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        EmailDTO emailDTO = new EmailDTO();

        String userName = order.getUser().getFullName();
        String email = order.getUser().getEmail();
        String destination = order.getDestination();
        int numberOfPeople = order.getNumberOfPeople();

        Date checkinDate = order.getCheckinDate();
        LocalDate localDate = checkinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String datecheckInFormat = localDate.format(formatter);

        Date checkOutDate = order.getCheckoutDate();
        LocalDate localDate1 = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateCheckOutFormat= localDate1.format(formatter1);

        String hotelName = order.getHotel().getHotelName();
        String flightName = order.getFlight().getAirlineName() ;
        String flightTicketClass   = order.getFlight().getTicketClass().toString() ;
        String totalPrice = String.valueOf(order.getTotalPrice());

        emailDTO.setToEmail(email) ;
        String subject = "THANH TOÁN CHUYẾN ĐI THÀNH CÔNG" ;
        emailDTO.setSubject(subject);

        String body = "------------------<b>XÁC NHẬN THANH TOÁN THÀNH CÔNG</b>------------------" +"<br>" +
                "---------<b>Thông Tin Chi Tiết Chuyến Đi</b>--------- <br>" +
                "<b>Người đặt:</b> " + userName + "<br>" +
                "<b>Địa điểm:</b> " + destination + "<br>" +
                "<b>Số người:</b> " + numberOfPeople + "<br>" +
                "<b>Thời gian check-in:</b> " + datecheckInFormat + "<br>" +
                "<b>Thời gian check-out:</b> " + dateCheckOutFormat + "<br>" +
                "<b>Tên hãng bay:</b> " + flightName + " - Hạng: " + flightTicketClass + "<br>" +
                "<b>Tên khách sạn:</b> " + hotelName + "<br>" +
                "<b>Tổng Chi Phí:</b> " + totalPrice + "<br><br>" +
                "<b>HUST WONDER TRÂN TRỌNG CẢM ƠN!</b>";
        emailDTO.setBody(body) ;

        return sendEmail(emailDTO);

    }

    public Object sendAnnouncePayFalledEmail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        EmailDTO emailDTO = new EmailDTO();

        String userName = order.getUser().getFullName();
        String email = order.getUser().getEmail();
        String destination = order.getDestination();
        int numberOfPeople = order.getNumberOfPeople();

        Date checkinDate = order.getCheckinDate();
        LocalDate localDate = checkinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String datecheckInFormat = localDate.format(formatter);

        Date checkOutDate = order.getCheckoutDate();
        LocalDate localDate1 = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateCheckOutFormat= localDate1.format(formatter1);

        String hotelName = order.getHotel().getHotelName();
        String flightName = order.getFlight().getAirlineName() ;
        String flightTicketClass   = order.getFlight().getTicketClass().toString() ;
        String totalPrice = String.valueOf(order.getTotalPrice());

        emailDTO.setToEmail(email) ;
        String subject = "THANH TOÁN CHUYẾN ĐI THẤT BẠI" ;
        emailDTO.setSubject(subject);

        String body = "------------------<b>THANH TOÁN THẤT BẠI</b>------------------" +"<br>" +
                "---------------<b>HUST WONDER rất tiếc khi phải thông báo rằng bạn đã thanh toán không thành công , vui lòng kiểm tra lại</b>"+"<br>"+
                "---------<b>Thông Tin Chi Tiết Chuyến Đi</b>--------- <br>" +
                "<b>Người đặt:</b> " + userName + "<br>" +
                "<b>Địa điểm:</b> " + destination + "<br>" +
                "<b>Số người:</b> " + numberOfPeople + "<br>" +
                "<b>Thời gian check-in:</b> " + datecheckInFormat + "<br>" +
                "<b>Thời gian check-out:</b> " + dateCheckOutFormat + "<br>" +
                "<b>Tên hãng bay:</b> " + flightName + " - Hạng: " + flightTicketClass + "<br>" +
                "<b>Tên khách sạn:</b> " + hotelName + "<br>" +
                "<b>Tổng Chi Phí:</b> " + totalPrice + "<br><br>" +
                "<b>HUST WONDER TRÂN TRỌNG CẢM ƠN!</b>";
        emailDTO.setBody(body) ;

        return sendEmail(emailDTO);
    }

    public Object sendAnnouceCancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        EmailDTO emailDTO = new EmailDTO();

        String userName = order.getUser().getFullName();
        String email = order.getUser().getEmail();
        String destination = order.getDestination();
        int numberOfPeople = order.getNumberOfPeople();

        Date checkinDate = order.getCheckinDate();
        LocalDate localDate = checkinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String datecheckInFormat = localDate.format(formatter);

        Date checkOutDate = order.getCheckoutDate();
        LocalDate localDate1 = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateCheckOutFormat= localDate1.format(formatter1);

        String hotelName = order.getHotel().getHotelName();
        String flightName = order.getFlight().getAirlineName() ;
        String flightTicketClass   = order.getFlight().getTicketClass().toString() ;
        String totalPrice = String.valueOf(order.getTotalPrice());

        emailDTO.setToEmail(email) ;
        String subject = "HỦY CHUYẾN THÀNH CÔNG" ;
        emailDTO.setSubject(subject);

        String body = "------------------<b>HỦY CHUYẾN THÀNH CÔNG</b>------------------" +"<br>" +
                "---------------<b>HUST WONDER rất tiếc khi không thể đồng hành cùng bạn trong chuyến đi lần này ! </b>"+"<br>"+
                "Hẹn quý khách trong một tương lai gần nhất" +"<br>"+
                "---------<b>Thông Tin Chi Tiết Chuyến Đi</b>--------- <br>" +
                "<b>Người đặt:</b> " + userName + "<br>" +
                "<b>Địa điểm:</b> " + destination + "<br>" +
                "<b>Số người:</b> " + numberOfPeople + "<br>" +
                "<b>Thời gian check-in:</b> " + datecheckInFormat + "<br>" +
                "<b>Thời gian check-out:</b> " + dateCheckOutFormat + "<br>" +
                "<b>Tên hãng bay:</b> " + flightName + " - Hạng: " + flightTicketClass + "<br>" +
                "<b>Tên khách sạn:</b> " + hotelName + "<br>" +
                "<b>Tổng Chi Phí:</b> " + totalPrice + "<br><br>" +
                "<b>HUST WONDER TRÂN TRỌNG CẢM ƠN!</b>";
        emailDTO.setBody(body) ;

        return sendEmail(emailDTO);
    }
}
