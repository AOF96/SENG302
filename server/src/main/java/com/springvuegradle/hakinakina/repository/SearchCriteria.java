package com.springvuegradle.hakinakina.repository;

public class SearchCriteria {
    private String key;
    private Object value;
    private SearchOperation operation;

    public SearchCriteria(String key, Object value, SearchOperation operation) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public SearchOperation getOperation() {
        return this.operation;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }
}