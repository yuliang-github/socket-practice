package com.yl.test;

import com.alibaba.fastjson.JSONObject;

/**
 * @author Alex
 * @since 2019/4/8 15:01
 */
public class DemoClass {

    public static void say(){
        System.err.println("演示类");
    }

    public static void main(String[] args) {

        Dog dog = new Dog();

        dog.setName("pug");
        dog.setAge(1);
        dog.setFoot(2);

        JSONObject json = (JSONObject)JSONObject.toJSON(dog);

        System.err.println(json);
    }
}
