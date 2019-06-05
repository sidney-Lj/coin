package com.sidney.coin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserDeviceEnum {
    PC((byte) 1, "PC端"),
    MOBILE((byte) 2, "手机端");
    private byte value;
    private String desc;
}
