package com.tomaszgawel.spring.data.jpa.search;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

@Configuration
public class SearchableJpaRepositoryConfig {

    @Bean
    @SuppressWarnings("rawTypes")
    public JpaRepositoryFactoryBean createSearchableJpaRepositoryFactoryBean() {
        return new SearchableJpaRepositoryFactoryBean();
    }
}
