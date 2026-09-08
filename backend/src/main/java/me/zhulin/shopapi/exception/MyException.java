package me.zhulin.shopapi.exception;


import me.zhulin.shopapi.enums.ResultEnum;
import lombok.Getter;

/**
 * Created By Zhu Lin on 3/10/2018.
 */
@Getter
public class MyException extends RuntimeException {
    private Integer code;

    public MyException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public MyException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
