package com.tomaszgawel.spring.data.jpa.search.sample.repository;

import com.tomaszgawel.spring.data.jpa.search.SearchableJpaRepository;
import com.tomaszgawel.spring.data.jpa.search.sample.model.Hello;

public interface HelloRepository extends SearchableJpaRepository<Hello, Long> {
}
