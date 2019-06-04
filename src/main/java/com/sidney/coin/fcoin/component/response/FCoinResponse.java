package com.sidney.coin.fcoin.component.response;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FCoinResponse {
    /**
     * 调用状态（0 为调用成功、1 为失败，返回 1 时请看【调用失败错误码】及错误码描述）
     */
    protected Byte code;
    /**
     * 业务处理状态（处理失败 INIT；处理成功 SUCCESS），平台可根据非 SUCCESS状态做相应处理，处理失败时可参考错误码及描述
     */
    protected String status;
    /**
     * 错误码
     */
    protected String errorCode;
    /**
     * 错误码描述
     */
    protected String errorMessage;

    /**
     * 判断是否调用成功,注意：因懒猫历史问题查询接口不能通过此状态判断，需具体判断ERRORCODE的ERRORMESSAGE
     * @return
     */
    public boolean isSuccess(){
        if (code == 0 && "SUCCESS".equals(status)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
