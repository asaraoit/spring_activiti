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
public class OaActivityApplicationTests {

    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    private String processInstanceKey = "test";
    @Test
    public void createLiucheng() {
        System.out.println("--创建流程定义--");
        String processResourceFile = "processes/leave.bpmn";

        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource(processResourceFile)
                .name(processInstanceKey)
                .deploy();
        System.out.println("部署ID：" + deploy.getId()); //2501
        System.out.println("部署名称：" + deploy.getName());//test
    }

    @Test
    public void firstLiucheng() {
        System.out.println("--测试步骤--");
        Map<String,Object> property = new HashMap<>();
        property.put("user", "1");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("test",property);
        System.out.println("流程实例的ProcessInstanceId: "+pi.getId());//5001
        System.out.println("流程实例的ProcessDefinitionKey: "+pi.getProcessDefinitionKey());//test
        System.out.println("流程实例的ProcessDefinitionId: "+pi.getProcessDefinitionId());//test:1:2504
        System.out.println("流程实例的ProcessDefinitionName: "+pi.getProcessDefinitionName());//null
        System.out.println("流程实例的ProcessDefinitionVersion: "+pi.getProcessDefinitionVersion());//null
    }

    @Test
    public void getDaiban() {
        System.out.println("--得到待办--");
        String assignnee = "1";
        String processInstanceId = "5001";
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .taskAssignee(assignnee).list();
        for(Task task:tasks){
            System.out.println("任务id: "+task.getId());//5005
            System.out.println("任务name: "+task.getName());// 测试
            System.out.println("任务formkey: "+task.getFormKey());//null
        }
    }

}

