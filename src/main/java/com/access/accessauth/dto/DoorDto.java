package com.access.accessauth.dto;

import lombok.Data;

@Data
public class DoorDto {

    private String indexCode;
    private String name;
    private String resourceType;
    private String doorNo;
    private Integer doorSerial;
    private String description;
    private String parentIndexCode;
    private String regionIndexCode;
    private String treatyType;
    private String channelType;
    private String channelNo;
    private String controlOneId;
    private String controlTwoId;
    private String readerInId;
    private String readerOutId;
    private Integer isCascade;
    private String createTime;
    private String updateTime;
    private Integer sort;
    private Integer disOrder;
    private String regionName;
    private String regionPath;
    private String regionPathName;
}
