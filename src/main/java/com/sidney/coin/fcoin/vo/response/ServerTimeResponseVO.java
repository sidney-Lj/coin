package com.sidney.coin.fcoin.vo.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel("ServerTimeResponse")
@Data
@Api(tags = "ServerTimeResponse")
public class ServerTimeResponseVO {
    @ApiModelProperty("serverTime")
    private Long serverTime;
}
