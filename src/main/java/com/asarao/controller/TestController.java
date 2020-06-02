package com.asarao.controller;

import com.asarao.service.ActivityService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    // 发起流程
    @RequestMapping("/initiationProcess")
    public String initiationProcess(){
        System.out.println("method startActivityDemo begin....");

        System.out.println( "调用流程存储服务，已部署流程数量："
                + repositoryService.createDeploymentQuery().count());

        Map<String,Object> map = new HashMap<>();
        // 流程图里写的${user} ，这里传进去user
        map.put("user","liqiye");

        //流程启动
        identityService.setAuthenticatedUserId("liqiye");    // 指定流程的发起者 不指定发起者的字段就为空，注意跟审核人分开
        ExecutionEntity pi = (ExecutionEntity) runtimeService.startProcessInstanceByKey("leave",map);
        System.out.println("启动流程成功！");

        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("任务ID: "+task.getId());
        System.out.println("任务的办理人: "+task.getAssignee());
        System.out.println("任务名称: "+task.getName());
        System.out.println("任务的创建时间: "+task.getCreateTime());
        System.out.println("流程实例ID: "+task.getProcessInstanceId());

        Map<String,Object> map2 = new HashMap<>();
        map2.put("users","lisi,wangwu");
        taskService.complete(task.getId(),map2);  // 开启后，环节会走到发起请假请求，要完成这个环节，才能到下一个审核环节

        System.out.println("method startActivityDemo end....");
        return "success";
    }

    // 根据 taskid 审核任务
    @RequestMapping("/audit")
    public String audit(String taskId){

        Map<String,Object> map = new HashMap<String,Object>();
        // 流程图里写的${users} ，这里传进去users
        map.put("users","lisi,wangwu");

        taskService.complete(taskId,map);
        return "success";
    }

    // 通过用户名查询该用户的所有任务
    @RequestMapping("/checkByUser")
    public String checkByUser(String user){
        List<Task> tasks = taskService//与任务相关的Service
                .createTaskQuery()//创建一个任务查询对象
                .taskAssignee(user)
                .list();
        if(tasks !=null && tasks.size()>0){
            for(Task task:tasks){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("流程实例ID:"+task.getProcessInstanceId());
            }
        }
        return "success";
    }

    // 通过发起者查询该用户发起的所有任务
    @RequestMapping("/checkByInitiator")
    public String checkByInitiator(String user){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().startedBy(user).list();  //获取该用户发起的所有流程实例
        // System.out.println(list.toString());
        for (ProcessInstance processInstance : list) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            if(tasks !=null && tasks.size()>0){
                for(Task task:tasks){
                    System.out.println("任务ID:"+task.getId());
                    System.out.println("任务的办理人:"+task.getAssignee());
                    System.out.println("任务名称:"+task.getName());
                    System.out.println("任务的创建时间:"+task.getCreateTime());
                    System.out.println("流程实例ID:"+task.getProcessInstanceId());
                }
            }
        }
        return "success";
    }

    /**
     * 获取流程图 执行到哪里高亮显示
     * @param procDefId 部署的流程id  在 act_re_procdef 这张表里
     * @param execId  要查询的流程执行的id（开启了一个流程就会生成一条执行的数据）  在 act_ru_execution 这张表里（该表下PROC_DEF_ID_字段可以判断哪个流程）
     * @param response
     * @throws Exception
     */
    @RequestMapping("/getActPic/{procDefId}/{execId}")
    public void  getActPic(@PathVariable("procDefId") String procDefId,
                           @PathVariable("execId") String execId, HttpServletResponse response)throws Exception {
        InputStream imageStream = activityService.tracePhoto(procDefId, execId);
        // 输出资源内容到相应对象
        byte[] b = new byte[1024];
        int len;
        while ((len = imageStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }
}
