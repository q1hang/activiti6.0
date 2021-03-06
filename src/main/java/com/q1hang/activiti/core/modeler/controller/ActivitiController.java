package com.q1hang.activiti.core.modeler.controller;


import com.q1hang.activiti.core.modeler.dto.TaskDto;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.q1hang.activiti.core.modeler.service.Activitiservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author q1hang 2019-08-12
 */
@RestController
@RequestMapping("/activiti")
public class ActivitiController {

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private Activitiservice activitiService;

	@RequestMapping("list/{business}")
	public List<TaskDto> listTask(@PathVariable String business){
		List<Task> taskList = taskService.createTaskQuery()
				.processInstanceBusinessKey(business)
				.listPage(0, 100);
		List<TaskDto> result= new ArrayList<>();
		if (!CollectionUtils.isEmpty(taskList)) {
			for (Task task : taskList) {
				result.add(new TaskDto(task).pending());
			}
		}
		return result;
	}

	@RequestMapping("second/{business}")
	public TaskDto selectProcess(@PathVariable String business){
		Task task = taskService.createTaskQuery()//创建查询对象
				.processInstanceBusinessKey(business)//通过流程 business key来查询当前任务
				.singleResult();//获取单个查询结果
		taskService.complete(task.getId());
		return new TaskDto(task).pass();
	}

	@RequestMapping("start/{business}")
	public List<TaskDto> setSingleAssignee(@PathVariable String business) {
		List<TaskDto> tasks = activitiService.setSingleAssignee(business);
		return  tasks;
	}

	@RequestMapping("multiAssignee")
	public void setMultiAssignee() {

		//根据bpmn文件部署流程
		repositoryService.createDeployment().addClasspathResource("MultiAssignee.bpmn").deploy();
		// 设置多个处理人变量 这里设置了三个人
		Map<String, Object> variables = new HashMap<>();
		List<String> userList = new ArrayList<>();
		userList.add("user1");
		userList.add("user2");
		userList.add("user3");
		variables.put("userList", userList);
		//采用key来启动流程定义并设置流程变量，返回流程实例
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("multiAssigneeProcess", variables);
		String processId = pi.getId();
		System.out.println("流程创建成功，当前流程实例ID："+processId);

		// 查看user1的任务
		List<Task> list = taskService.createTaskQuery().taskAssignee("user1").list();
		if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID："+task.getId());
                System.out.println("任务的办理人："+task.getAssignee());
                System.out.println("任务名称："+task.getName());
                System.out.println("任务的创建时间："+task.getCreateTime());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("#######################################");
            }
        }

		// 查看user2的任务
		List<Task> list2 = taskService.createTaskQuery().taskAssignee("user2").list();
		if(list2!=null && list2.size()>0){
            for(Task task:list2){
                System.out.println("任务ID："+task.getId());
                System.out.println("任务的办理人："+task.getAssignee());
                System.out.println("任务名称："+task.getName());
                System.out.println("任务的创建时间："+task.getCreateTime());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("#######################################");
            }
        }

		// 查看user3的任务
		List<Task> list3 = taskService.createTaskQuery().taskAssignee("user3").list();
		if(list3!=null && list3.size()>0){
            for(Task task:list3){
                System.out.println("任务ID："+task.getId());
                System.out.println("任务的办理人："+task.getAssignee());
                System.out.println("任务名称："+task.getName());
                System.out.println("任务的创建时间："+task.getCreateTime());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("#######################################");
            }
        }

	}

	@RequestMapping("exclusiveGateway")
	public void exclusiveGateway() {

		//根据bpmn文件部署流程
		repositoryService.createDeployment().addClasspathResource("exclusiveGateway.bpmn").deploy();
		// 设置User Task1受理人变量
		Map<String, Object> variables = new HashMap<>();
		variables.put("user1", "007");
		//采用key来启动流程定义并设置流程变量，返回流程实例
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("exclusiveGatewayAndTimerBoundaryEventProcess", variables);
		String processId = pi.getId();
		System.out.println("流程创建成功，当前流程实例ID："+processId);

		// 注意 这里需要拿007来查询，key-value需要拿value来获取任务
		List<Task> list = taskService.createTaskQuery().taskAssignee("007").list();
		Map<String, Object> variables1 = new HashMap<>();
		variables1.put("user2", "lili"); // 设置User Task2的受理人变量
		variables1.put("operate", ""); // 设置用户的操作 为空 表示走flow3的默认路线
		taskService.complete(list.get(0).getId(), variables1);
		System.out.println("User Task1被完成了，此时流程已流转到User Task2");

		List<Task> list1 = taskService.createTaskQuery().taskAssignee("lili").list();
		Map<String, Object> variables2 = new HashMap<>();
		variables2.put("user4", "bobo");
		variables2.put("startTime", "2018-6-11T14:22:00"); // 设置定时边界任务的触发时间 注意：后面的时间必须是ISO 8601时间格式的字符串！！！
		taskService.complete(list1.get(0).getId(), variables2);

		List<Task> list2 = taskService.createTaskQuery().taskAssignee("bobo").list();
		if(list2!=null && list2.size()>0){
            for(Task task:list2){
                System.out.println("任务ID："+task.getId());
                System.out.println("任务的办理人："+task.getAssignee());
                System.out.println("任务名称："+task.getName());
                System.out.println("任务的创建时间："+task.getCreateTime());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("#######################################");
            }
        }
	}

}
