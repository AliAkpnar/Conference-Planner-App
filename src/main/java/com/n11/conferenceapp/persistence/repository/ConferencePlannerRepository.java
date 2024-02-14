package com.n11.conferenceapp.persistence.repository;

import com.n11.conferenceapp.persistence.entity.ConferencePlanner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConferencePlannerRepository extends JpaRepository<ConferencePlanner, Long>
{
}
