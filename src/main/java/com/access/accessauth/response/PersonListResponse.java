package com.access.accessauth.response;

import com.access.accessauth.dto.DoorDto;
import com.access.accessauth.dto.PersonDto;
import lombok.Data;

import java.util.List;

@Data
public class PersonListResponse {

    private String code;

    private String msg;

    private ResponseData data;

    @Data
    public class ResponseData{
        private Integer total;

        private Integer pageNo;

        private Integer pageSize;

        private List<PersonDto> list;
    }
}
