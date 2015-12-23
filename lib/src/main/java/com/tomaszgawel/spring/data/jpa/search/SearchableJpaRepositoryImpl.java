package com.tomaszgawel.spring.data.jpa.search;

import com.tomaszgawel.spring.data.jpa.search.annotation.SearchFields;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.StringUtils;

@NoRepositoryBean
public class SearchableJpaRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements SearchableJpaRepository<T, ID> {

    private final EntityManager entityManager;
    private final Set<String> searchFields;

    // There are two constructors to choose from, either can be used.
    public SearchableJpaRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        searchFields = Collections.unmodifiableSet(
                Stream.of(domainClass.getAnnotation(SearchFields.class).value())
                        .map(e -> StringUtils.isEmpty(e.alias()) ? e.value() : e.alias())
                        .collect(Collectors.toSet()));
    }

    @Override
    public Set<String> getSearchFields() {
        return searchFields;
    }

    @Override
    public Long getSearchResultCount(Map<String, ?> searchCriteria) throws IllegalArgumentException {
        checkSearchCriteria(searchCriteria);
        return createCountQuery(searchCriteria).getSingleResult();
    }

    @Override
    public List<T> search(Map<String, ?> searchCriteria) throws IllegalArgumentException {
        checkSearchCriteria(searchCriteria);
        return createSearchQuery(searchCriteria).getResultList();
    }

    @Override
    public Page<T> search(Map<String, ?> searchCriteria, Pageable pageable) throws IllegalArgumentException {
        checkSearchCriteria(searchCriteria);
        CharSequence commonSqlPart = createCommonSqlPart(searchCriteria);
        return new PageImpl<T>(
                addSearchParameters(createSearchQuery(commonSqlPart), searchCriteria)
                        .setFirstResult(pageable.getOffset())
                        .setMaxResults(pageable.getPageSize())
                                .getResultList(),
                pageable,
                addSearchParameters(createCountQuery(commonSqlPart), searchCriteria)
                        .getSingleResult());
    }

    @Override
    public T searchOne(Map<String, ?> searchCriteria) throws IllegalArgumentException {
        checkSearchCriteria(searchCriteria);
        return createSearchQuery(searchCriteria)
                        .setFirstResult(0)
                        .setMaxResults(1)
                        .getSingleResult();
    }

    private TypedQuery<T> createSearchQuery(Map<String, ?> searchCriteria) {
        return addSearchParameters(
                createSearchQuery(createCommonSqlPart(searchCriteria)), searchCriteria);
    }

    private TypedQuery<Long> createCountQuery(Map<String, ?> searchCriteria) {
        return addSearchParameters(
                createCountQuery(createCommonSqlPart(searchCriteria)), searchCriteria);
    }

    private TypedQuery<T> createSearchQuery(CharSequence commonSqlPart) {
        return entityManager.createQuery(createSql("SELECT e ", commonSqlPart), getDomainClass());
    }

    private TypedQuery<Long> createCountQuery(CharSequence commonSqlPart) {
        return entityManager.createQuery(createSql("SELECT COUNT(e) ", commonSqlPart), Long.class);
    }

    private String createSql(CharSequence select, CharSequence commonSqlPart) {
        return new StringBuilder().append(select).append(commonSqlPart).toString();
    }

    private CharSequence createCommonSqlPart(Map<String, ?> searchCriteria) {
        final StringBuilder sqlBuilder = new StringBuilder()
                .append("FROM ").append(getDomainClass().getName()).append(" e");
        if(searchCriteria.size() > 0) {
            sqlBuilder.append(" WHERE ").append(
                    searchCriteria.keySet().stream()
                            .map(e -> new StringBuilder().append(e).append(" = :").append(e))
                            .collect(Collectors.joining(" AND ")));
        }
        return sqlBuilder;
    }

    private void checkSearchCriteria(Map<String, ?> searchCriteria) throws IllegalArgumentException {
        for(String key : searchCriteria.keySet()) {
            if(!searchFields.contains(key)) {
                throw new IllegalArgumentException("Invalid search field: " + key);
            }
        }
    }

    private <T extends Query> T addSearchParameters(T query, Map<String, ?> searchCriteria) {
        searchCriteria.entrySet().stream().forEach(
                e -> query.setParameter(e.getKey(), e.getValue()));
        return query;
    }
}