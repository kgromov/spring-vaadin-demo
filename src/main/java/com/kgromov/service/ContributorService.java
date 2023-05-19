package com.kgromov.service;

import com.kgromov.domain.Contributor;
import com.kgromov.repository.ContributorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorRepository contributorRepository;

    @Transactional(readOnly = true)
    public List<Contributor> findContributors(String filterValue) {
        return isBlank(filterValue)
                ? contributorRepository.findAll()
                : contributorRepository.search(filterValue);
    }

    @Transactional
    public void delete(Contributor contributor) {
        contributorRepository.delete(contributor);
    }

    @Transactional
    public void save(Contributor contributor) {
        contributorRepository.save(contributor);
    }

}
