package com.coralocean.sqldefiner.parser;

import com.coralocean.sqldefiner.mock.Mock;

import java.lang.reflect.Type;

public class MockColumn {
    private String propertyName;
    private Type type;
    private Mock mock;

    public MockColumn(String propertyName, Type type) {
        this.propertyName = propertyName;
        this.type = type;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Mock getMock() {
        return mock;
    }

    public void setMock(Mock mock) {
        this.mock = mock;
    }
}
