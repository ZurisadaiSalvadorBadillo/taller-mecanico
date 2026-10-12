package com.taller.auth.repository;

import com.taller.auth.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findByWorkshopId(Long workshopId, Pageable pageable);

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

    @Query("""
            select count(client) > 0 from Client client
            where client.id <> :clientId and (
                lower(client.personalEmail) in (lower(:personalEmail), lower(:workEmail))
                or lower(client.workEmail) in (lower(:personalEmail), lower(:workEmail))
                or client.personalPhone in (:personalPhone, :workPhone)
                or client.workPhone in (:personalPhone, :workPhone)
            )
            """)
    boolean existsWithAnyContactExcludingClient(
            Long clientId,
            String personalEmail,
            String workEmail,
            String personalPhone,
            String workPhone
    );

    boolean existsByFirstNameIgnoreCaseAndPaternalSurnameIgnoreCaseAndMaternalSurnameIgnoreCase(
            String firstName, String paternalSurname, String maternalSurname);

    @Query("""
            select count(client) > 0 from Client client where client.id <> :clientId
            and lower(client.firstName) = lower(:firstName)
            and lower(client.paternalSurname) = lower(:paternalSurname)
            and lower(client.maternalSurname) = lower(:maternalSurname)
            """)
    boolean existsByFullNameExcludingClient(Long clientId, String firstName, String paternalSurname, String maternalSurname);
}
