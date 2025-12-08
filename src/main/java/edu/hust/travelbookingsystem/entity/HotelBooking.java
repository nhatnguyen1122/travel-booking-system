package edu.hust.travelbookingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "hotel_booking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id" , nullable = false)
    private Order order ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id" , nullable = false)
    private Hotel hotel ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_bedroom_id",nullable = false)
    private HotelBedroom hotelBedroom ;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate ;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date" , nullable = false)
    private Date endDate ;
}
