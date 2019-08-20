package com.q1hang.activiti.core.modeler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.q1hang.activiti.core.modeler.service.ActModelService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程控制器
 */
@RestController
public class ModelerController{

    private static final Logger logger = LoggerFactory.getLogger(ModelerController.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
	private ActModelService actModelService;

    
	@RequestMapping("list")
	public List<Model> list(ModelAndView modelAndView) {
		return repositoryService.createModelQuery().listPage(0,100);
	}

    
    /**
     * 创建模型
     * @param response
     * @param name 模型名称
     * @param key 模型key
     */
    @RequestMapping("/create")
    public void create(HttpServletResponse response,String name,String key) throws IOException {
    	actModelService.create(response,name,key);
    }
    
    /**
     * 发布流程
     * @param modelId 模型ID
     * @return
     */
    @RequestMapping("/publish")
    public String publish(String modelId){
    	return actModelService.publish(modelId);
    }

	/**
	 * 撤销部署
	 * @param modelId 模型ID
	 * @return
	 */
	@RequestMapping("/revokePublish")
    public String revokePublish(String modelId){
        return actModelService.revokePulish(modelId);
    }
    
    /**
     * 删除模型
     * @param modelId 模型ID
     * @return
     */
    @RequestMapping("/delete")
    public String deleteProcessInstance(String modelId){
		actModelService.delete(modelId);
		return "删除成功";
    }

	/**
	 * 导出
	 */
	@RequestMapping(value = "export")
	public void export(String id, HttpServletResponse response) {
		actModelService.export(id, response);
	}
}
