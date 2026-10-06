package com.taller.auth.repository;

import com.taller.auth.model.Workshop;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
    List<Workshop> findAllByOrderByNameAsc();

    @Query("""
            select count(workshop) > 0 from Workshop workshop
            where lower(workshop.name) = lower(:name)
               or lower(workshop.email) = lower(:email)
               or upper(workshop.rfc) = upper(:rfc)
            """)
    boolean existsWithDuplicateIdentity(String name, String email, String rfc);
}
