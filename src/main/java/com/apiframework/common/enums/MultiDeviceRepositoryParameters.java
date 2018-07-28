package com.apiframework.common.enums;

public enum MultiDeviceRepositoryParameters {

    USERID("userId"),
    DEVICEKEY("deviceKey"),
    AUTHTOKEN("AuthToken");


    public String value;

    MultiDeviceRepositoryParameters(String value) {
        this.value= value;
    }

    public String getValue() {
        return value;
    }
}
