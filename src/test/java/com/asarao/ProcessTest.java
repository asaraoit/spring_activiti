package com.asarao;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/*
 * @ClassName: ProcessTest
 * @Description: TODO
 * @Author: Asarao
 * @Date: 2020/6/4 9:41
 * @Version: 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessTest {

    @Autowired
    private ProcessEngine processEngine;

    // 流程部署
    @Test
    public void deploy(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("请假流程")
                .key("leave")
                .addClasspathResource("processes/leave.bpmn")
                .deploy();

        System.out.println("流程ID："+deploy.getId());
        System.out.println("流程名称："+deploy.getName());
        System.out.println("流程key："+deploy.getKey());
        System.out.println("流程Category："+deploy.getCategory());
        System.out.println("流程部署时间："+deploy.getDeploymentTime());
    }

    // 启动流程
    @Test
    public void startProcess(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String processDefinitionId = "leave:1:98b000d7-a605-11ea-931e-181deaf1ddd1";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);

        System.out.println("流程实例ID:"+ processInstance.getId());
        System.out.println("流程实例Name:"+ processInstance.getName());
        System.out.println("流程定义的Name:"+ processInstance.getProcessDefinitionName());
        System.out.println("流程定义的Key:"+ processInstance.getProcessDefinitionKey());
        System.out.println("流程开始时间:"+ processInstance.getStartTime());
        System.out.println("流程启动成功");
    }


    // 查询任务
    @Test
    public void queryTask(){
        TaskService taskService = processEngine.getTaskService();
        String assignee = "李四";
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

    // 办理任务
    @Test
    public void completeTask(){
        TaskService taskService = processEngine.getTaskService();
        String taskId = "2efa2315-a60a-11ea-813b-181deaf1ddd1";
        taskService.complete(taskId);
    }

    @Test
    public void queryNewest(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("leave")
                .latestVersion()
                .singleResult();
        System.out.println("流程版本："+processDefinition.getVersion());
    }
}
