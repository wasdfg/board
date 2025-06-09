package com.board.Question;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter implements Converter<String, Category> {

    @Override
    public Category convert(String source) {
        for (Category category : Category.values()) {
            if (category.getValue().equals(source)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category value: " + source);
    }
}

