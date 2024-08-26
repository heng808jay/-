package com.access.accessauth.job;

import com.access.accessauth.dto.DoorDto;
import com.access.accessauth.dto.PersonDto;
import com.access.accessauth.model.ApplyJob;
import com.access.accessauth.response.AddAuthConfigResponse;
import com.access.accessauth.response.DeviceResourceResponse;
import com.access.accessauth.response.PersonListResponse;
import com.access.accessauth.service.ApplyJobService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 门禁定时任务类
 */
@Configuration
@EnableScheduling
@Slf4j
public class AccessJob {

    @Value("${artemisConfig.host}")
    public String HOST;

    @Value("${artemisConfig.appKey}")
    public String APP_KEY;

    @Value("${artemisConfig.appSecret}")
    public String APP_SECRET;

    public static final String DEVICERESOURCE_RESOURCES = "/api/resource/v2/door/search";

    public static final String AUTH_CONFIG_ADD = "/api/acps/v1/auth_config/add";

    public static final String ADVANCE_PERSONLIST = "/api/resource/v2/person/advance/personList";

    @Autowired
    private ApplyJobService applyJobService;

    @Scheduled(cron = "0 * * * * ?")
    public void authConfigJob(){
        log.info("开始下发权限==========================");
        //获取所有申请
        LambdaQueryWrapper<ApplyJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ApplyJob::getQjkssj);
        List<ApplyJob> applyList = applyJobService.list(wrapper);
        log.info("申请数量:" + applyList.size());
        //获取所有门禁设备
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(HOST);
        config.setAppKey(APP_KEY);
        config.setAppSecret(APP_SECRET);
        List<DoorDto> doorList = getDoor(config, 1, 100);
        //获取人员列表
        applyList.forEach(apply -> {
            List<PersonDto> person = getPerson(config, 1, 100, apply.getZjhm());
            if (!person.isEmpty()){
                //下发权限
                String s = addAuthConfig(config, person.get(0), doorList, apply);
                log.info(s);
                //删除当前下发学号的申请数据
                LambdaQueryWrapper<ApplyJob> removeWrapper = new LambdaQueryWrapper<>();
                removeWrapper.eq(ApplyJob::getXh,apply.getXh());
                applyJobService.remove(removeWrapper);
            }
        });
        log.info("下发权限完成===========================");

    }

    /**
     * 获取门禁设备
     * @param config
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<DoorDto> getDoor(ArtemisConfig config, Integer pageNo, Integer pageSize){
        List<DoorDto> list = new ArrayList<>();
        log.info("开始获取门禁设备");
        JSONObject param = new JSONObject();
        param.put("pageNo",pageNo);
        param.put("pageSize",pageSize);
//        param.put("resourceType","door");
        Map<String,String> path = new HashMap<String,String>(2){
            {
                put("https://","/artemis" + DEVICERESOURCE_RESOURCES);
            }
        };
        log.info("https://" + config.host + "/artemis" + DEVICERESOURCE_RESOURCES);
        String result = null;
        try {
            Map<String,String> header = new HashMap<>();
            header.put("X-Ca-Key",APP_KEY);
            header.put("X-Ca-Signature-Headers","x-ca-key");
            header.put("X-Ca-Signature","bXPbVfbem3Np9Ihv0jdaBIFxZxcM3BVCZTsuNIs8FO0=");
            result = ArtemisHttpUtil.doPostStringArtemis(config, path, param.toJSONString(), null, null, "application/json", null);
//            log.info("接口返回：" + result);
            DeviceResourceResponse deviceResourceResponse = JSON.parseObject(result, DeviceResourceResponse.class);
            if (deviceResourceResponse.getMsg().equals("success")){
                list = deviceResourceResponse.getData().getList();
                log.info("获取门禁设备数量：" + list.size());
            }else{
                log.info("接口调用失败");
            }
        } catch (Exception e) {
            log.info("获取门禁设备失败");
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * 获取申请人信息
     * @param config
     * @param pageNo
     * @param pageSize
     * @param certificateNo
     * @return
     */
    public List<PersonDto> getPerson(ArtemisConfig config, Integer pageNo, Integer pageSize, String certificateNo){
        List<PersonDto> list = new ArrayList<>();
        log.info("开始获取人员信息");
        JSONObject param = new JSONObject();
        param.put("pageNo",pageNo);
        param.put("pageSize",pageSize);
        param.put("certificateType",111);
        param.put("certificateNo",certificateNo);
        Map<String,String> path = new HashMap<String,String>(2){
            {
                put("https://","/artemis" + ADVANCE_PERSONLIST);
            }
        };
        log.info("https://" + config.host + "/artemis" + ADVANCE_PERSONLIST);
        String result = null;
        try {
            Map<String,String> header = new HashMap<>();
            header.put("X-Ca-Key",APP_KEY);
            header.put("X-Ca-Signature-Headers","x-ca-key");
            header.put("X-Ca-Signature","q6U05W0Qcg8qE3ks047tggTxjbdALFLsUFmhp6Ss1GA=");
            result = ArtemisHttpUtil.doPostStringArtemis(config, path, param.toJSONString(), null, null, "application/json", null);
//            log.info("接口返回：" + result);
            PersonListResponse personListResponse = JSON.parseObject(result, PersonListResponse.class);
            if (personListResponse.getMsg().equals("success")){
                list = personListResponse.getData().getList();
                log.info("获取人员数量：" + list.size());
            }else{
                log.info("接口调用失败");
            }
        } catch (Exception e) {
            log.info("获取人员信息失败");
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * 配置权限
     * @param config
     * @param personDto
     * @param doorList
     * @param apply
     * @return
     */
    public String addAuthConfig(ArtemisConfig config,PersonDto personDto,List<DoorDto> doorList,ApplyJob apply){
        log.info("开始下发权限");
        JSONObject param = new JSONObject();
        JSONArray personDatas = new JSONArray();
        JSONObject person = new JSONObject();
        JSONArray indexCodes = new JSONArray();
        indexCodes.add(personDto.getPersonId());
        person.put("indexCodes",indexCodes);
        person.put("personDataType","person");
        personDatas.add(person);
        param.put("personDatas",personDatas);

        JSONArray resourceInfos = new JSONArray();
        doorList.forEach(d -> {
            JSONObject door = new JSONObject();
            door.put("resourceIndexCode",d.getIndexCode());
            door.put("resourceType","door");
            JSONArray channelNos = new JSONArray();
            channelNos.add(d.getChannelNo());
            door.put("channelNos",channelNos);
            resourceInfos.add(door);
        });
        param.put("resourceInfos",resourceInfos);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        param.put("startTime",sdf.format(apply.getQjkssj()));
        param.put("endTime",sdf.format(apply.getQjjssj()));
        Map<String,String> path = new HashMap<String,String>(2){
            {
                put("https://","/artemis" + AUTH_CONFIG_ADD);
            }
        };
        log.info("https://" + config.host + "/artemis" + AUTH_CONFIG_ADD);
        String result = null;
        try {
            Map<String,String> header = new HashMap<>();
            header.put("X-Ca-Key",APP_KEY);
            header.put("X-Ca-Signature-Headers","x-ca-key");
            header.put("X-Ca-Signature","NKi5q4nt9vj3JFsXIqufkUQK0J6Jkl+lRyEH8CkDKfg=");
            result = ArtemisHttpUtil.doPostStringArtemis(config, path, param.toJSONString(), null, null, "application/json", null);
            log.info("接口返回：" + result);
            AddAuthConfigResponse addAuthConfigResponse = JSON.parseObject(result, AddAuthConfigResponse.class);
            if (!addAuthConfigResponse.getMsg().equals("success")){
                log.info("下发权限成功,任务id:" + addAuthConfigResponse.getData().getTaskId());
                return addAuthConfigResponse.getData().getTaskId();
            }else{
                log.info("接口调用失败");
            }
        } catch (Exception e) {
            log.info("下发权限失败");
            throw new RuntimeException(e);
        }
        return null;
    }

}
