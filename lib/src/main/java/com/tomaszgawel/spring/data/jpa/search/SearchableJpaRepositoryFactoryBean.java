package com.tomaszgawel.spring.data.jpa.search;

import com.tomaszgawel.spring.data.jpa.search.annotation.SearchFields;
import java.io.Serializable;
import javax.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class SearchableJpaRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, I> {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new SearchableJpaRepositoryFactory(entityManager);
    }

    private static class SearchableJpaRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {

        private EntityManager entityManager;

        public SearchableJpaRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Object getTargetRepository(RepositoryInformation information)  {
            if(useSearchableJpaRepository(information)) {
                return new SearchableJpaRepositoryImpl<T, I>(
                        (Class<T>) information.getDomainType(), entityManager);
            }
            return super.getTargetRepository(information);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            if(useSearchableJpaRepository(metadata)) {
                return SearchableJpaRepository.class;
            }
            return super.getRepositoryBaseClass(metadata);
        }

        private boolean useSearchableJpaRepository(RepositoryMetadata metadata) {
            final Class<?> c = metadata.getDomainType();
            if(SearchableJpaRepository.class.isAssignableFrom(c)) {
                final SearchFields a = c.getAnnotation(SearchFields.class);
                if(a != null && a.value().length > 0) {
                    return true;
                }
            }
            return false;
        }
    }
}