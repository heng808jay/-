package com.access.accessauth.service.impl;

import com.access.accessauth.dao.ApplyJobDao;
import com.access.accessauth.model.ApplyJob;
import com.access.accessauth.service.ApplyJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApplyJobServiceImpl extends ServiceImpl<ApplyJobDao, ApplyJob> implements ApplyJobService {

    @Autowired
    private ApplyJobDao applyJobDao;
}
