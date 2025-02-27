package com.poweroftwo.potms_backend.access_key.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "access_key_table")
public class Key {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String keyName;
    private String apiKey;
    private String secreteKey;
    private Date createTime;
    @Column(name = "user_id")
    private Integer userId;
}
