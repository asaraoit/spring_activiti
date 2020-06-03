package com.asarao;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityApplicationTests {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    // 部署流程
    @Test
    public void deploy() {
        System.out.println("--创建流程定义--");
        String processResourceFile = "processes/leave.bpmn";

        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource(processResourceFile)
                .name("请假流程")
                .deploy();
        System.out.println("部署ID：" + deploy.getId());//f77c6886-a57a-11ea-9197-000ec6dd34b8
        System.out.println("部署名称：" + deploy.getName());//请假流程
    }

    // 启动流程
    @Test
    public void start() {
        System.out.println("--测试步骤--");
        Map<String, Object> property = new HashMap<>();
        property.put("user", "1");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("leave", property);
        System.out.println("流程实例的ProcessInstanceId: " + pi.getId());//3369f114-a57c-11ea-b2f0-000ec6dd34b8
        System.out.println("流程实例的ProcessDefinitionKey: " + pi.getProcessDefinitionKey());//leave
        System.out.println("流程实例的ProcessDefinitionId: " + pi.getProcessDefinitionId());//leave:1:f7bb1f38-a57a-11ea-9197-000ec6dd34b8
        System.out.println("流程实例的ProcessDefinitionName: " + pi.getProcessDefinitionName());//请假流程
        System.out.println("流程实例的ProcessDefinitionVersion: " + pi.getProcessDefinitionVersion());//1
    }

    // 根据 assignee 代理人/受托人 获取任务
    @Test
    public void search() {
        System.out.println("--得到待办--");
        String assignee = "1";
        String processInstanceId = "3369f114-a57c-11ea-b2f0-000ec6dd34b8";
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .taskAssignee(assignee).list();
        for (Task task: tasks) {
            System.out.println("任务ID:" + task.getId());
            System.out.println("任务名称:" + task.getName());
            System.out.println("任务的创建时间:" + task.getCreateTime());
            System.out.println("任务的办理人:" + task.getAssignee());
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("执行对象ID:" + task.getExecutionId());
            System.out.println("流程定义ID:" + task.getProcessDefinitionId());
        }
        /**
         * 任务ID:336fe48a-a57c-11ea-b2f0-000ec6dd34b8
         * 任务名称:申请请假
         * 任务的创建时间:Wed Jun 03 17:25:44 CST 2020
         * 任务的办理人:1
         * 流程实例ID：3369f114-a57c-11ea-b2f0-000ec6dd34b8
         * 执行对象ID:336adb77-a57c-11ea-b2f0-000ec6dd34b8
         * 流程定义ID:leave:1:f7bb1f38-a57a-11ea-9197-000ec6dd34b8
         */
    }

    // 认领任务
    public void claim() {
        String taskId = "4d09180a-311f-11ea-a834-005056c00008";
        String userId = "zhangsan";
        taskService.claim(taskId, userId);
    }
}

