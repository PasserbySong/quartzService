package com.xiejs.quartz.controller;

import com.goldcn.common.util.CommonUtils;
import com.xiejs.quartz.dto.SchedulerDto;
import com.xiejs.quartz.job.RestJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * Created by Administrator on 2016/3/30.
 */
@RestController
@RequestMapping(value = "/job")
public class QuaetzController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuaetzController.class);
    @Autowired
    private Scheduler scheduler;
    private Integer SUCCESS=1;

    /**
     *创建每天定时任务
     */
    @RequestMapping(method = RequestMethod.POST)
    public Object createJob(@RequestBody SchedulerDto schedulerDto) throws Exception {

        TriggerKey triggerKey = TriggerKey.triggerKey(schedulerDto.getTaskId(),  schedulerDto.getGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        // 不存在，创建一个
        if (null == trigger) {
            JobDetail jobDetail = JobBuilder
                    .newJob(RestJob.class)
                    .withIdentity(schedulerDto.getTaskId(),  schedulerDto.getGroup())
                    .build();
            jobDetail.getJobDataMap().put("url", schedulerDto.getJobUrl());
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(schedulerDto.getTime());
            // 按新的表达式构建一个新的trigger
            trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(schedulerDto.getTaskId(),schedulerDto.getGroup())
                    .withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            // trigger已存在，则更新相应的定时设置
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(schedulerDto.getTime());
            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                    .withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        }
        return SUCCESS;
    }
    /**
     * 只执行一次的定时任务
     */
    @RequestMapping(value="/simple",method = RequestMethod.POST)
    public Object createSimpleJob(@RequestBody SchedulerDto schedulerDto) throws Exception {
            TriggerKey triggerKey = triggerKey(schedulerDto.getTaskId(), schedulerDto.getGroup());
            //停止任务
            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }
            JobKey jobKey = jobKey(schedulerDto.getTaskId(), schedulerDto.getGroup());
            JobDetail jobDetail;
            if (scheduler.checkExists(jobKey)) {
                jobDetail = scheduler.getJobDetail(jobKey);
                jobDetail.getJobDataMap().put("url", schedulerDto.getJobUrl());
                scheduler.addJob(jobDetail, true);
            } else {
                jobDetail = newJob(RestJob.class)
                        .storeDurably()
                        .withIdentity(jobKey)
                        .usingJobData("url", schedulerDto.getJobUrl())
                        .build();
                scheduler.addJob(jobDetail, false);
            }
            String format = "yyyy-MM-dd HH:mm:ss";
            Date date=CommonUtils.parse(schedulerDto.getTime(), format);
            Trigger trigger = newTrigger()
                    .withIdentity(triggerKey)
                    .forJob(jobDetail)
                    .startAt(date)
                    .usingJobData("url", schedulerDto.getJobUrl())
                    .build();

            scheduler.scheduleJob(trigger);
        return SUCCESS;
    }

    /**
     * 立刻执行任务
     */
    @RequestMapping(value="/{id}/{group}",method = RequestMethod.GET)
    public Object startJob(@PathVariable String id,@PathVariable String group) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(id, group);
        scheduler.triggerJob(jobKey);
        return SUCCESS;
    }

    /**
     * 删除任务
     */
    @RequestMapping(value="/{id}/{group}",method = RequestMethod.DELETE)
    public Object delJob(@PathVariable String id,@PathVariable String group) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(id, group);
        scheduler.deleteJob(jobKey);
        return SUCCESS;
    }

    /**
     * 暂停任务
     */
    @RequestMapping(value="/{id}/{group}/pause",method = RequestMethod.GET)
    public Object pauseJob(@PathVariable String id,@PathVariable String group) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(id, group);
        scheduler.pauseJob(jobKey);
        return SUCCESS;
    }

    /**
     * 恢复任务
     */
    @RequestMapping(value="/{id}/{group}/recovery",method = RequestMethod.GET)
    public Object resumeJob(@PathVariable String id,@PathVariable String group) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(id, group);
        scheduler.resumeJob(jobKey);
        return SUCCESS;
    }

    @RequestMapping(value="test",method = RequestMethod.GET)
    public Object test(){
        System.out.println("xxxxxxxxx");
        return null;
    }
}
