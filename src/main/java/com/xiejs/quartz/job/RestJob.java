package com.xiejs.quartz.job;


import com.goldcn.common.model.DataResult;

import net.sf.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Administrator on 2016/3/30.
 */
public class RestJob implements Job {
    @Autowired
    RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(RestJob.class);

    private String url;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("url:{}",getUrl());
        JSONObject json=restTemplate.getForObject(getUrl(),JSONObject.class);
        DataResult dataResult = (DataResult)JSONObject.toBean(json,DataResult.class);
        if(dataResult.getIsSuccess()==1)
              LOGGER.info("name:{}  josn:{}",context.getJobDetail().getKey(), json.toString());
        else
             LOGGER.error("name:{}  josn:{}",context.getJobDetail().getKey(), json.toString());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
