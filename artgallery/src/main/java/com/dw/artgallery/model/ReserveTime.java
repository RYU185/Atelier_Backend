package com.dw.artgallery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reserve_time")
@Entity
public class ReserveTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name = "reserve_date_id", nullable = false)
    private ReserveDate reserveDate;

    @OneToMany(mappedBy = "reserveTime", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}