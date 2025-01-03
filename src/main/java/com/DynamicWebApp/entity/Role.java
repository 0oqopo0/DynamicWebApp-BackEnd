package com.DynamicWebApp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String roleCodePer;
    private String roleCodeEng;
    private LocalDateTime createDate; // اضافه کردن فیلد CREATE_DATE

    // سایر متدها و منطق‌های مورد نیاز شما
}
