package edu.hust.travelbookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Table(name = "orders")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "destination")
    private String destination;

    @NotNull
    @Column(name = "number_of_people")
    private int numberOfPeople;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date")
    private Date orderDate;

    @DateTimeFormat
    @Column(name = "check_in_date")
    private Date checkinDate;

    @DateTimeFormat
    @Column(name = "check_out_date")
    private Date checkoutDate;

    @DateTimeFormat
    @Column(name = "start_hotel")
    private Date startHotel;

    @DateTimeFormat
    @Column(name = "end_hotel")
    private Date endHotel;

    @Column(name = "total_price")
    private double totalPrice = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "flight_id",nullable = true)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Column(name = "bedrooms")
    private String listBedrooms;

    public Order(String destination, int numberOfPeople, Date checkinDate, Date checkoutDate) {
        this.destination = destination;
        this.numberOfPeople = numberOfPeople;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
    }

}
