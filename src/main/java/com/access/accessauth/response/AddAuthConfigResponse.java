package com.access.accessauth.response;

import com.access.accessauth.dto.DoorDto;
import lombok.Data;

import java.util.List;

@Data
public class AddAuthConfigResponse {

    private String code;

    private String msg;

    private ResponseData data;


    @Data
    public class ResponseData{
        private String taskId;
    }
}
