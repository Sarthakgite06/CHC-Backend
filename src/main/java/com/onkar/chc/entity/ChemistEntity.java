package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHEMIST_LIST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChemistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_NAME", nullable = false, unique = true)
    private String userName;

    @Column(name = "CHEMIST_REGI_NO", nullable = false, unique = true)
    private Long chemistRegiNo;

    @Column(name = "SHOP_NAME")
    private String shopName;
}
