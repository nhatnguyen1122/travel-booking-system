package edu.hust.travelbookingsystem.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    USER_EXISTS(1001,"user existed", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1002,"password mismatch", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VALID(1003,"email not valid", HttpStatus.BAD_REQUEST),
    ARGUMENT_NOT_VALID(1004,"argument not blank or not null", HttpStatus.BAD_REQUEST),
    PHONE_NOT_EXISTS(1005,"phone not existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(1006,"user not existed", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1007,"wrong password", HttpStatus.BAD_REQUEST),
    DATE_TIME_NOT_VALID(1008,"check in date must be before check out date", HttpStatus.BAD_REQUEST),
    DATE_NOT_VALID(1009,"check in date must be after now", HttpStatus.BAD_REQUEST),
    NUMBER_NOT_VALID(1029,"number  not valid", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1000,"role not found", HttpStatus.BAD_REQUEST),
    HOTEL_NOT_FOUND(1010,"hotel not found", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1011,"order not found", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_ACTIVE(1012,"account not active", HttpStatus.BAD_REQUEST),
    NOT_EXISTS(1013,"object not exists", HttpStatus.BAD_REQUEST),
    NOT_VALID_FLIGHT_DATE(1014,"ngày bay không phú hợp với lịch tour", HttpStatus.BAD_REQUEST),
    DATE_INVALID(1015,"ngày check-in khách sạn phải sau ngày đi", HttpStatus.BAD_REQUEST),
    NUMBER_CHAIR_NOT_VALID(1016,"Không thể thay đổi số ghế ít hơn số ghế đã được đặt", HttpStatus.BAD_REQUEST),
    PRICE_NOT_VALID(1017,"price not valid", HttpStatus.BAD_REQUEST),
    LENGTH_PASS_NOT_VALID(1018,"The length password must be greater than or equal to 6 characters", HttpStatus.BAD_REQUEST),
    LENGTH_PHONE_NOT_VALID(1030,"The length phone must be greater than or equal to 10 characters", HttpStatus.BAD_REQUEST),
    NUMBER_FLOOR_NOT_VALID(1019,"number floor not valid", HttpStatus.BAD_REQUEST),
    HOTEL_BEDROOM_NOT_AVAILABLE(1020,"Phòng đã có người đặt trước . Vui lòng đặt phòng khác ! ", HttpStatus.BAD_REQUEST),
    NOT_CHANGE_STATUS_ADMIN(1021,"Không thể thay đổi trạng thái của admin", HttpStatus.BAD_REQUEST),
    PAYMENT_PAID_NOT_EXISTS(1022,"payment paid not exists", HttpStatus.BAD_REQUEST),
    PAYMENT_VERIFY_NOT_EXISTS(1023,"payment verify not exists", HttpStatus.BAD_REQUEST),
    PAYMENT_UNPAID_NOT_EXISTS(1024,"payment unpaid not exists", HttpStatus.BAD_REQUEST),
    PAYMENT_FALSE_NOT_EXISTS(1025,"payment false not exists", HttpStatus.BAD_REQUEST),
    EMAIL_TO_NOT_BLANK(1026,"email can not blank", HttpStatus.BAD_REQUEST),
    SUBJECT_NOT_BLANK(1027,"subject of email can not blank", HttpStatus.BAD_REQUEST),
    BODY_NOT_BLANK(1028,"body can not blank", HttpStatus.BAD_REQUEST),
    HOTEL_END_DATE_INVALID(1031,"Hotel end date must be after start date", HttpStatus.BAD_REQUEST),
    HOTEL_DATE_EXCEEDS_CHECKOUT(1032,"Hotel stay cannot exceed order checkout date", HttpStatus.BAD_REQUEST),
    FLIGHT_DATE_EXCEEDS_CHECKOUT(1033,"Flight date cannot be after order checkout date", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_SEATS(1034,"Not enough seats available on this flight", HttpStatus.BAD_REQUEST),
    ;
    private int code ;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
