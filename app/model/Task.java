package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import enums.TaskRule;
import enums.TaskType;


public class Task {
	private Long id;
	private String name;
	private Date startDate;
	private Date endDate;
	private Long parentId;
	@JsonBackReference
	private Task parentTask;
	private TaskType taskType;
	private TaskRule rule;
	@JsonManagedReference
	private List<Task> childrenTasks= new ArrayList<Task>();
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Task getParentTask() {
		return parentTask;
	}
	public void setParentTask(Task parentTask) {
		this.parentTask = parentTask;
	}
	public TaskType getTaskType() {
		return taskType;
	}
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}
	public TaskRule getRule() {
		return rule;
	}
	public void setRule(TaskRule rule) {
		this.rule = rule;
	}
	public List<Task> getChildrenTasks() {
		return childrenTasks;
	}
	public void setChildrenTasks(List<Task> childrenTasks) {
		this.childrenTasks = childrenTasks;
	}
	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", startDate=" + startDate + ", endDate=" + endDate + ", parentId="
				+ parentId + ", parentTask=" + parentTask + ", taskType=" + taskType + ", rule=" + rule
				+ ", childrenTasks=" + childrenTasks + "]";
	}
	
	
	
}
