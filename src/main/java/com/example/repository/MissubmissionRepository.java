package com.example.repository;

import com.example.entity.Missubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissubmissionRepository extends JpaRepository<Missubmission, Missubmission.MissubmissionPK> {
}
    