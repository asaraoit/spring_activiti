package com.asarao;

import com.asarao.service.ProcessManageService;
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

/*
 * @ClassName: ExclusiveGatewayTest
 * @Description: 排它网关
 * @Author: Asarao
 * @Date: 2020/6/5 9:32
 * @Version: 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExclusiveGatewayTest {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ProcessManageService processManageService;

    // 部署流程
    @Test
    public void deploy() {
        System.out.println("--创建流程定义--");
        String processResourceFile = "processes/ExclusiveGateway.bpmn";

        Deployment deployment = processManageService
                .deployByClasspathResources("报销流程", "ExclusiveGateway", null, null, processResourceFile);

        System.out.println("部署ID：" + deployment.getId());
        System.out.println("部署名称：" + deployment.getName());
        System.out.println("部署分类：" + deployment.getCategory());
        System.out.println("部署Key：" + deployment.getKey());

        System.out.println("--部署完成--");
    }

    @Autowired
    RuntimeService runtimeService;

    // 启动流程
    @Test
    public void startProcess(){
        String processInstanceKey = "ExclusiveGateway";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processInstanceKey);
        System.out.println("流程实例的ProcessInstanceId: " + processInstance.getId());
        System.out.println("流程实例的ProcessDefinitionKey: " + processInstance.getProcessDefinitionKey());
        System.out.println("流程实例的ProcessDefinitionId: " + processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ProcessDefinitionName: " + processInstance.getProcessDefinitionName());
        System.out.println("流程实例的ProcessDefinitionVersion: " + processInstance.getProcessDefinitionVersion());
    }

    @Autowired
    TaskService taskService;

    @Test
    public void searchTaskByAssignee(){
        String assignee = "张三";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        if(null != tasks && !tasks.isEmpty()){
            for (Task task: tasks) {
                System.out.println("任务ID:" + task.getId());
                System.out.println("任务名称:" + task.getName());
                System.out.println("任务的创建时间:" + task.getCreateTime());
                System.out.println("任务的办理人:" + task.getAssignee());
                System.out.println("流程实例ID：" + task.getProcessInstanceId());
                System.out.println("执行对象ID:" + task.getExecutionId());
                System.out.println("流程定义ID:" + task.getProcessDefinitionId());
            }
        }
    }

    @Test
    public void completeTask(){
        String taskId = "1624432e-a6e8-11ea-82d0-000ec6dd34b8";
        taskService.complete(taskId);
        System.out.println("任务完成");
    }

    @Test
    public void completeTaskWithVariables(){
        String taskId = "70f19ce9-a6e7-11ea-a5cc-000ec6dd34b8";
        Map<String, Object> variables = new HashMap<>();
        variables.put("money",600);
        taskService.complete(taskId,variables);
        System.out.println("任务完成");
    }
}
