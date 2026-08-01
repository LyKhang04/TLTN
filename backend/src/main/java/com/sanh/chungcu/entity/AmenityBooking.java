package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.Amenity;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "amenity_bookings")
@Getter
@Setter
public class AmenityBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "amenity_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Amenity amenity;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User resident;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private BookingStatus status;

}
