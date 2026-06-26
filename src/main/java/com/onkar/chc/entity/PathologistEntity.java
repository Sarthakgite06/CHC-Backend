package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PATHOLOGIST_LIST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathologistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_NAME", nullable = false, unique = true)
    private String userName;

    @Column(name = "LICENSE_NO", nullable = false, unique = true)
    private String licenseNo;

    @Column(name = "LAB_NAME")
    private String labName;

    @Column(name = "LAB_ADDRESS")
    private String labAddress;
}
