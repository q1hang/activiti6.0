package com.q1hang.activiti.core.modeler.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.q1hang.activiti.common.exception.RRException;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 2018-07-16
 */
@Service
@Slf4j
public class ActModelService {
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    private ObjectMapper objectMapper;

    public void create(HttpServletResponse response,String name,String key) throws IOException {
        log.info("创建模型入参name：{},key:{}",name,key);
        Model model = repositoryService.newModel();
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, "");
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        model.setName(name);
        model.setKey(key);
        model.setMetaInfo(modelNode.toString());
        repositoryService.saveModel(model);

        String modelId=model.getId();
        log.info("创建模型完善ModelEditorSource入参模型ID：{}",modelId);
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace","http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        try {
            repositoryService.addModelEditorSource(modelId,editorNode.toString().getBytes("utf-8"));
        } catch (Exception e) {
            log.info("创建模型时完善ModelEditorSource服务异常：{}",e);
        }
        log.info("创建模型完善ModelEditorSource结束");
        //重定向到在线流程设计器
        response.sendRedirect("/modeler.html?modelId="+ model.getId());
        log.info("创建模型结束，返回模型ID：{}",model.getId());
    }

    public String publish(String modelId){
        log.info("流程部署入参modelId：{}",modelId);
        Model modelData = repositoryService.getModel(modelId);
        log.info("{}",modelData);
        if(null!=modelData) {
            try {

                byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
                if (bytes == null) {
                    return "部署ID:{}的模型数据为空，请先设计流程并成功保存，再进行发布" + modelId;
                }
                JsonNode modelNode = new ObjectMapper().readTree(bytes);
                BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
                Deployment deployment = repositoryService.createDeployment()
                        .name(modelData.getName())
                        .addBpmnModel(modelData.getName() + ".bpmn20.xml", model)
                        .deploy();
                modelData.setDeploymentId(deployment.getId());
                repositoryService.saveModel(modelData);
                return "success";
            } catch (Exception e) {
                return "部署modelId:{}模型服务异常:" + modelId;
            }
        }
        return "该Model不存在";
    }


    public String revokePulish(String modelId){
        log.info("撤销发布流程入参modelId：{}",modelId);
        Model modelData = repositoryService.getModel(modelId);
        if(null != modelData){
            try {
                /**
                 * 参数不加true:为普通删除，如果当前规则下有正在执行的流程，则抛异常
                 * 参数加true:为级联删除,会删除和当前规则相关的所有信息，包括历史
                 */
                List<Deployment> deployments=repositoryService.createDeploymentQuery()
                        .deploymentName(modelData.getName()).list();

                deployments.forEach(s->{
                    repositoryService.deleteDeployment(s.getId(),true);
                });
                return "success";

            } catch (Exception e) {
                log.error("撤销已部署流程服务异常：{}",e);
                return "failure";
            }
        }
        return "该Model不存在";
    }

    /**
     * 导出模型
     */
    public void export(String id, HttpServletResponse response) {
        try {
            Model model = repositoryService.getModel(id);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(model.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            IOUtils.copy(in, response.getOutputStream());
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.flushBuffer();
        } catch (Exception e) {
            throw new RRException("导出失败，模型ID为"+id);
        }
    }

    /**
     * 删除模型
     * @param id  模型ID
     */
    public void delete(String id) {
        repositoryService.deleteModel(id);
    }
}

