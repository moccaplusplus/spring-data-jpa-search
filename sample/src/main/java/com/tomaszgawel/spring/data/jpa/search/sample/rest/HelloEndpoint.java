package com.tomaszgawel.spring.data.jpa.search.sample.rest;

import com.tomaszgawel.spring.data.jpa.search.sample.model.Hello;
import com.tomaszgawel.spring.data.jpa.search.sample.repository.HelloRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloEndpoint {

    @Autowired
    private HelloRepository repository;

    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Hello> getAll(@RequestParam(value = "offset", required = false) int offset,
            @RequestParam(value = "limit", required = false) int limit) {
        if(offset == 0 && limit == 0) {
            return repository.findAll();
        }
        return repository.findAll(new PageRequest(offset, limit)).getContent();
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(Hello hello) {
        if(hello.getId() != 0L) {
            throw new IllegalArgumentException();
        }
        repository.save(hello);
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(Hello hello) {
        if(!repository.exists(hello.getId())) {
            throw new IllegalArgumentException("Hello does not exists!");
        }
        repository.save(hello);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable("id") long id) {
        repository.delete(id);
    }

    @RequestMapping(
            value = "/count",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public long getCount() {
        return repository.count();
    }

    @RequestMapping(
            value = "/search",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public List<Hello> search(Map<String, String> searchCriteria) {
        return repository.search(searchCriteria);
    }

    @RequestMapping(
            value = "/search/count",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public int getSearchResultCount(Map<String, String> searchCriteria) {
        return repository.getSearchResultCount(searchCriteria).intValue();
    }
}
