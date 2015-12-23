package com.tomaszgawel.spring.data.jpa.search.sample.model;

import com.tomaszgawel.spring.data.jpa.search.annotation.SearchField;
import com.tomaszgawel.spring.data.jpa.search.annotation.SearchFields;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@SearchFields({
        @SearchField("name"),
        @SearchField("greeting")
})
@Entity
public class Hello implements Serializable {

    private static final long serialVersionUID = 5279473437046204906L;

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String greeting;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
