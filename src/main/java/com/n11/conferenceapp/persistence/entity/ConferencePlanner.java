package com.n11.conferenceapp.persistence.entity;

import com.n11.conferenceapp.persistence.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "conference_planner")
public class ConferencePlanner extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private int duration;
    @NotNull
    @NotBlank
    private String sessionType;
    @NotNull
    @NotBlank
    private String name;
}
