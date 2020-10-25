package com.hmd007.hmdbootstarter.dto;

import com.hmd007.hmdbootstarter.response.ApiResponse;

public class ErrorDto extends ApiResponse<String> {
    private static final long serialVersionUID = 1L;
    static final String ERROR_MESSAGE = "Une erreur est survenue!";
    public ErrorDto() {
        setMessage(ERROR_MESSAGE);
    }
}
