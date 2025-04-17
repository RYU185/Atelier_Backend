package com.dw.artgallery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name="purchase_date",nullable = false)
    private LocalDate purchaseDate;

    @Column(name="is_delete")
    private Boolean isDelete;

    @OneToMany(mappedBy = "purchase" , cascade = CascadeType.ALL)
    private List<PurchaseGoods> purchaseGoodsList = new ArrayList<>();
}