package com.access.accessauth.dto;

import lombok.Data;

import java.util.List;

@Data
public class PersonDto {

    private String personId;
    private String personName;
    private Integer gender;
    private String orgPath;
    private String orgIndexCode;
    private String orgPathName;
    private Integer certificateType;
    private String certificateNo;
    private String createTime;
    private String updateTime;
    private String phoneNo;
    private String jobNo;
    private List<PersonPhoto> personPhoto;

    @Data
    public class PersonPhoto{
        private String personPhotoIndexCode;
        private String picUri;
        private String serverIndexCode;
    }
}
