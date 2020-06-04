package com.asarao.service;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.zip.ZipInputStream;

/*
 * @ClassName: ProcessManageService
 * @Description: 流程管理Service
 * @Author: Asarao
 * @Date: 2020/6/4 16:14
 * @Version: 1.0
 **/
@Service
@Slf4j
public class ProcessManageService {


    @Autowired
    private RepositoryService repositoryService;


    /**
     * 通过zip包的形式部署
     * @param name deploymentName
     * @param key deploymentKey
     * @param category 分类
     * @param tenantId 承办人ID
     * @param zipInputStream zip输入流
     * @return Deployment
     */
    public Deployment deployByZip(String name, String key, String category,String tenantId,
                                  ZipInputStream zipInputStream){
        Deployment deployment = null;
        try {
            deployment = repositoryService.createDeployment()
                    .addZipInputStream(zipInputStream)
                    .name(name)
                    .key(key)
                    .category(category)
                    .tenantId(tenantId)
                    .deploy();
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Zip流部署时发生异常：{}",e.getMessage());
        }
        return deployment;
    }

    /**
     * 通过类路径下资源文件部署
     * @param name deploymentName
     * @param key deploymentKey
     * @param category 分类
     * @param tenantId 承办人ID
     * @param classpathResources 文件类路径
     * @return Deployment
     */
    public Deployment deployByClasspathResources(String name, String key, String category,String tenantId,
                                  String... classpathResources){
        Deployment deployment = null;
        try {
            if(null == classpathResources || classpathResources.length == 0){
                throw new RuntimeException("文件路径不能为空");
            }
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            for (String classpathResource:classpathResources) {
                deploymentBuilder.addClasspathResource(classpathResource);
            }
            deployment = deploymentBuilder
                    .name(name)
                    .key(key)
                    .category(category)
                    .tenantId(tenantId)
                    .deploy();
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("类路径文件部署时发生异常：{}",e.getMessage());
        }
        return deployment;
    }

    /**
     * 根据部署 ID 获取流程定义信息
     * @param deploymentId 部署 ID
     * @return ProcessDefinition
     */
    public ProcessDefinition byDeploymentId(String deploymentId){
        return repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
    }

    /**
     * 根据部署 ID 获取最新版本的流程定义信息
     * @param deploymentId 部署 ID
     * @return ProcessDefinition
     */
    public ProcessDefinition lastVersionByDeploymentId(String deploymentId){
        return repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .latestVersion()
                .singleResult();
    }

    /**
     * 根据部署 IDs 获取流程定义信息
     * @param deploymentIds Set<String>
     * @return List<ProcessDefinition>
     */
    public List<ProcessDefinition> byDeploymentIds(Set<String> deploymentIds){
        return repositoryService.createProcessDefinitionQuery()
                .deploymentIds(deploymentIds)
                .list();
    }


    /**
     * 根据部署 ID 获取流程定义信息(分页查询)
     * @param deploymentIds Set<String>
     * @param pageNumber 页码
     * @param pageSize 每页记录数
     * @return List<ProcessDefinition>
     */
    public List<ProcessDefinition> byDeploymentIdsPage(Set<String> deploymentIds,
                                                       int pageNumber,int pageSize){
        return repositoryService.createProcessDefinitionQuery()
                .deploymentIds(deploymentIds)
                .listPage(pageNumber,pageSize);
    }


}
