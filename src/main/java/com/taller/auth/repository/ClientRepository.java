package com.taller.auth.repository;

import com.taller.auth.model.Client;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByOrderByCreatedAtDesc();

    @Query("""
            select count(client) > 0 from Client client
            where lower(client.personalEmail) in (lower(:personalEmail), lower(:workEmail))
               or lower(client.workEmail) in (lower(:personalEmail), lower(:workEmail))
               or client.personalPhone in (:personalPhone, :workPhone)
               or client.workPhone in (:personalPhone, :workPhone)
            """)
    boolean existsWithAnyContact(
            String personalEmail,
            String workEmail,
            String personalPhone,
            String workPhone
    );
}
