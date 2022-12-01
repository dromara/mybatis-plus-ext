package com.tangzc.mpe.autotable.utils;

import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * @author don
 */
@AllArgsConstructor(staticName = "newInstance")
public class StringHelper {

    private String string;

    public StringHelper replace(String key, String value) {
        string = string.replace(key, value);
        return this;
    }

    public StringHelper replace(String key, Function<String, String> valueFunc) {
        string = string.replace(key, valueFunc.apply(key));
        return this;
    }

    @Override
    public String toString() {
        return string;
    }
}
