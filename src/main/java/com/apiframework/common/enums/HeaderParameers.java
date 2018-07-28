package com.apiframework.common.enums;

public enum HeaderParameers {

CONTENTTYPE ("CONTENT-TYPE"),
AUTHORIZATION("Authorization"),
ACCEPLANGUAGE("Accept-Language"),
XUSERID("X-USER-ID"),
XAPIKEY("X-Api-Key");

public String value;


    HeaderParameers(String value) {
        this.value=value;
    }

    public String getValue()


    {
        return value;
    }
}
