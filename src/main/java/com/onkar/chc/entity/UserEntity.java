package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer userId;

    //User Name
    @Column(name = "USER_ID",nullable = false,unique = true)
    private String userName;

    //First Name
    @Column(name = "FIRST_NAME",nullable = false)
    private String firstName;

    //Last Name
    @Column(name = "LAST_NAME",nullable = false)
    private String lastName;

    //Password
    @Column(name = "PASSWORD",nullable = false)
    private String password;

    //Email
    @Column(name = "EMAIL")
    private String email;

    //Contact No.
    @Column(name = "CONTACT_NO")
    private Long contactNo;

    //Health Card ID - Auto-generated format: PUN00000001 (null for Admin)
    @Column(name = "HEALTH_CARD_ID", unique = true)
    private String healthCardNo;

    //District (null for Admin)
    @Column(name = "DISTRICT")
    private String district;

    //User role
    @Column(name = "USER_ROLE",nullable = false)
    private String role;

    //Date of Birth.
    @Column(name = "DOB")
    private String dob;

    //Address
    @Column(name = "ADDRESS")
    private String address;

    //Gender (null for Admin)
    @Column(name = "GENDER")
    private String gender;

    @Column(name = "BLOOD_GROUP")
    private String bloodGroup;

    //Registration timestamp
    @Column(name = "CREATED_AT")
    private String createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> simpleGrantedAuthorityList=new ArrayList<>();
            simpleGrantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_"+this.role));
        return simpleGrantedAuthorityList;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
