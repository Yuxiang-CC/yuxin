package com.yuxin.utils;

import com.yuxin.enums.YuixnResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义响应类
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class IYuxinJSONResult {

    private Integer status;

    private String msg;

    private Object data;

    private String ok;

    public IYuxinJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static IYuxinJSONResult build(YuixnResultEnum resultEnum, Object data) {
        return new IYuxinJSONResult(resultEnum.getStatus(), resultEnum.getMessage(), data);
    }

    public static IYuxinJSONResult build(Integer status, String msg, Object data) {
        return new IYuxinJSONResult(status, msg, data);
    }
}
