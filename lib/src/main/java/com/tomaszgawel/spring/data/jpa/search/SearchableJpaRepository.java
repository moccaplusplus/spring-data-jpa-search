package com.tomaszgawel.spring.data.jpa.search;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SearchableJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Set<String> getSearchFields();

    Long getSearchResultCount(Map<String, ?> searchCriteria);

    List<T> search(Map<String, ?> searchCriteria);

    Page<T> search(Map<String, ?> searchCriteria, Pageable pageable);

    T searchOne(Map<String, ?> searchCriteria);
}
