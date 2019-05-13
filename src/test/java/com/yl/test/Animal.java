package com.yl.test;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alex
 * @since 2019/4/30 10:36
 */
@Data
@Getter
@Setter
public class Animal {

    private String name;

    private int age;

    public String say(Dog dog,String age){
        return "I am " + dog + ",age of " + age;
    }

}
