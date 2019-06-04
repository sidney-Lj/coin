package com.sidney.coin.fcoin.component.response;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FCoinGetResponse {

    /**
     * 业务处理状态（处理失败 INIT；处理成功 0），平台可根据非 0状态做相应处理，处理失败时可参考错误码及描述
     */
    protected Integer status;

    /**
     * 判断是否调用成功,注意：因懒猫历史问题查询接口不能通过此状态判断，需具体判断ERRORCODE的ERRORMESSAGE
     * @return
     */
    public boolean isSuccess(){
       return status == 0;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
