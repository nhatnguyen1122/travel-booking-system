package edu.hust.travelbookingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "hotel_bedroom")
@Entity
public class HotelBedroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "room_number")
    private Long roomNumber ;

    @Column(name = "price")
    private Double price;

    @Column(name = "room_type")
    private String roomType ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonIgnore
    private Hotel hotel ;
}
