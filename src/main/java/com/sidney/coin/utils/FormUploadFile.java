package com.sidney.coin.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: liaozuliang
 * @date: 2019/3/6
 */
@Getter
@Setter
public class FormUploadFile {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 对应的表单提交参数名称
     */
    private String formParamName;

    /**
     * 文件内容
     */
    private byte[] fileContent;
}
