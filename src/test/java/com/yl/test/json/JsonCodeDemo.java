package com.yl.test.json;

import cn.miludeer.jsoncode.JsonCode;

/**
 * @author Alex
 * @since 2019/5/9 14:30
 */
public class JsonCodeDemo {

    public static void main(String[] args) {

        String json = "{\"json\":{\"a\":{\"www\":\"ff\",\"rrr\":[\"v1\",\"v2\"]},\"b\":{\"www\":\"4567ttt\",\"rrr\":[\"v1\",\"v2\"]}}}";

        System.err.println(JsonCode.getValue(json, "$.json.b.rrr"));
        String[] valueList = JsonCode.getValueList(json, "$.json.b.rrr");
        for (String s : valueList) {
            System.err.println(s);
        }

    }

}
