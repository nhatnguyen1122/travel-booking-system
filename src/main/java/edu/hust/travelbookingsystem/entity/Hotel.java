package edu.hust.travelbookingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "hotels")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "hotel_price")
    private double hotelPriceFrom;

    @Column(name = "address")
    private String address ;

    @Column(name = "number_floor")
    private int numberFloor;

    @OneToMany(mappedBy = "hotel" , cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private List<HotelBedroom> hotelBedrooms;

    @OneToMany(mappedBy = "hotel",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders;

    public Hotel(String hotelName, double hotelPrice) {
        this.hotelName = hotelName;
        this.hotelPriceFrom = hotelPrice;
    }

}
