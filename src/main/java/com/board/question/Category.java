package com.board.question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Category {
    private String value;
    private String showValue;

    public Category(String value,String showValue){
        this.showValue = showValue;
        this.value = value;
    }
}
