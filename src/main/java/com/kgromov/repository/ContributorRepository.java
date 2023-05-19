package com.kgromov.repository;

import com.kgromov.domain.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {

    @Query("""
            select c from Contributor c
            where lower(c.firstName) like lower(concat('%', :searchTerm, '%'))
                OR lower(c.lastName) like lower(concat('%', :searchTerm, '%'))
           """)
    List<Contributor> search(String searchTerm);

    Optional<Contributor> findContributorsByLogin(String login);

}
