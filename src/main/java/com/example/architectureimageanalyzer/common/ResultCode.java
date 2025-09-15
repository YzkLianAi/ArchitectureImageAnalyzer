package com.example.architectureimageanalyzer.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    /**
     * 整体结果
     */
    SUCCESS(200,"成功"),
    FAIL(201, "失败"),

    /**
     * 非法操作
     */
    SERVICE_ERROR(203, "服务异常"),
    ILLEGAL_REQUEST( 204, "非法请求"),
    ARGUMENT_VALID_ERROR(206, "参数校验错误"),


    /**
     * 登录相关
     */
    REPETITION_USERNAME(226,"用户名重复"),
    LOGIN_ERROR(207, "用户名或密码错误"),
    OTHER_CLIENTS_LOGGED_IN(208, "已在其他端登陆"),
    PERMISSION(209, "没有权限"),
    LOGIN_CODE(222,"长时间未操作,会话已失效,请刷新页面后重试!"),
    CODE_ERROR(223,"验证码错误!"),
    TOKEN_ERROR(224,"Token非法!"),
    TOKEN_EXPIRED(225,"Token过期"),
    REGISTER_ERROR(226,"用户名重复"),
    NOT_LOGIN(227,"未登录"),

    ;

    private Integer code;

    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
