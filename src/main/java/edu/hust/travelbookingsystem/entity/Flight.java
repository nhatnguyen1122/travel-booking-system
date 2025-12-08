package edu.hust.travelbookingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.hust.travelbookingsystem.enums.TicketClass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@Entity
@Table(name = "flight")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id ;
    @Column(name = "ticket_class")
    @Enumerated(EnumType.STRING)
    private TicketClass ticketClass;
    @Column(name = "airline_name")
    private String airlineName ;
    @Column(name = "price")
    private double price;
    @Column(name = "check_in_date")
    @DateTimeFormat
    private Date checkInDate ;
    @Column(name = "check_out_date")
    @DateTimeFormat
    private Date checkOutDate ;
    @Column(name = "numberOfChairs")
    private int numberOfChairs ;
    @Column(name = "seatAvailable")
    private int seatAvailable ;

    @OneToMany(mappedBy = "flight" ,cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Order> orders ;


}
