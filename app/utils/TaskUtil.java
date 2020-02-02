package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import enums.TaskRule;
import enums.TaskType;
import model.Task;
import play.libs.Json;

public class TaskUtil {
	private long idPorvider = 0l;
	private Map<Long, Task> tasks = new HashMap<>();

	public static ObjectNode createResponse(Object response, boolean ok) {
		ObjectNode result = Json.newObject();
		result.put("isSuccessful", ok);
		if (response instanceof String) {
			result.put("body", (String) response);
		} else {
			result.putPOJO("body", response);
		}
		return result;
	}

	public Optional<Task> addTask(Task task) {

		if (task.getStartDate().compareTo(task.getEndDate()) > 0) {
			throw new RuntimeException("Parent task start and end dates are not aglined ");
		}

		Task parentTask = new Task();
		parentTask.setId(idPorvider);
		getNextval();// need to increment to have unique id
		parentTask.setName(task.getName());
		parentTask.setStartDate(task.getStartDate());
		parentTask.setEndDate(task.getEndDate());

		parentTask.setParentId(null);
		parentTask.setParentTask(null);
		parentTask.setTaskType(TaskType.PARENT);
		parentTask.setRule(TaskRule.AS_SOON_AS_POSSIBLE);
		tasks.put(parentTask.getId(), parentTask);
		for (Task childTask : task.getChildrenTasks()) {
			calculateTask(childTask, parentTask);
		}

		return Optional.ofNullable(parentTask);
	}

	public Optional<Task> updateTask(Task task, Long id) {
		if (task.getId() == null) {
			throw new RuntimeException("Task ID is mandatory!!!");
		}
		if (!tasks.containsKey(id)) {
			throw new RuntimeException("Task does not exist!!");
		}
		Task storedTask = tasks.get(id);
		if (task.getStartDate() != null) {
			updateTaskStartDate(storedTask, task.getStartDate());
		} else if (task.getEndDate() != null) {
			updateTaskEndDate(storedTask, task.getEndDate());
		}

		return getTask(task.getId());
	}

	public Optional<Task> getTask(long id) {
		Task task = tasks.get(id);
		List<Task> children = new ArrayList<Task>();
		if (tasks.containsKey(id)) {
			children = tasks.values().stream().filter(t -> (t.getParentId() != null && t.getParentId().equals(id)))
					.collect(Collectors.toList());
		}
		task.setChildrenTasks(children);
		return Optional.ofNullable(task);
	}

	private void calculateTask(Task task, Task parentTask) {
		validateTaskByDates(task, parentTask);
		validateTaskBytaskConstrians(task, parentTask);
		Task taskFromRequestedTask = new Task();
		taskFromRequestedTask.setId(idPorvider);
		getNextval();
		taskFromRequestedTask.setName(task.getName());
		taskFromRequestedTask.setStartDate(task.getStartDate());
		taskFromRequestedTask.setEndDate(task.getEndDate());

		taskFromRequestedTask.setParentId(parentTask.getId());
		taskFromRequestedTask.setParentTask(parentTask);
		taskFromRequestedTask.setTaskType(TaskType.CHILD);
		taskFromRequestedTask.setRule(task.getRule());
		tasks.put(taskFromRequestedTask.getId(), taskFromRequestedTask);
		if (!task.getChildrenTasks().isEmpty()) {
			for (Task childTask : task.getChildrenTasks()) {
				calculateTask(childTask, task);
			}
		}
	}

	private void getNextval() {
		idPorvider += 1;
	}

	private void validateTaskByDates(Task task, Task parentTask) {
		if (task.getStartDate().compareTo(parentTask.getStartDate()) < 0) {
			throw new RuntimeException(
					"Child task " + task.getName() + " starts before its parent " + parentTask.getName());
		}

		if (task.getEndDate().compareTo(parentTask.getEndDate()) > 0) {
			throw new RuntimeException(
					"Child task " + task.getName() + " end after its parent " + parentTask.getName());
		}

	}

	private void validateTaskBytaskConstrians(Task task, Task parentTask) {
		switch (task.getRule()) {
		case AS_SOON_AS_POSSIBLE:
		case START_NO_EARLIER_THAN:
			if (task.getStartDate().compareTo(parentTask.getStartDate()) < 0) {
				throw new RuntimeException("Task " + task.getName() + " and its Parent task " + parentTask.getName()
						+ " not satisfied constraint :" + task.getRule().toString());
			}
			break;

		case AS_LATE_AS_POSSIBLE:
		case FINISH_NO_LATER_THAN:
			if (task.getEndDate().compareTo(parentTask.getEndDate()) > 0) {
				throw new RuntimeException("Task " + task.getName() + " and its Parent task " + parentTask.getName()
						+ " not satisfied constraint :" + task.getRule().toString());
			}
			break;
		// case START_NO_LATER_THAN: case FINISH_NO_EARLIER_THAN: not applicable with
		// parent child concept
		case MUST_START_ON:
			if (task.getStartDate().compareTo(parentTask.getStartDate()) != 0) {
				throw new RuntimeException("Task " + task.getName() + " and its Parent task " + parentTask.getName()
						+ " not satisfied constraint :  Must start on.");
			}
			break;
		case FINISH_ON:
			if (task.getEndDate().compareTo(parentTask.getEndDate()) != 0) {
				throw new RuntimeException("Task " + task.getName() + " and its Parent task " + parentTask.getName()
						+ " not satisfied constraint :  Must finish on.");
			}
			break;
		default:
			System.out.println("Contraint not applicable for the task " + task.getName());
			break;
		}

	}

	private void updateTaskStartDate(Task task, Date startDate) {
		if (startDate.compareTo(task.getEndDate()) > 0) {
			throw new RuntimeException(
					"Please update End date in order to update the start date beyond end date of the task");
		}
		task.setStartDate(startDate);
		tasks.put(task.getId(), task);
		Task parentTask = task.getParentTask();
		if (parentTask != null) {
			if (startDate.compareTo(parentTask.getEndDate()) < 0) {
				updateTaskStartDate(parentTask, startDate);
			}
		}

	}

	private void updateTaskEndDate(Task task, Date endDate) {
		if (endDate.compareTo(task.getStartDate()) < 0) {
			throw new RuntimeException(
					"Please update start date in order to prepone end date before start date of the task");
		}
		task.setEndDate(endDate);
		tasks.put(task.getId(), task);
		Task parentTask = task.getParentTask();
		if (parentTask != null) {
			if (endDate.compareTo(parentTask.getEndDate()) > 0) {
				updateTaskEndDate(parentTask, endDate);
			}
		}
	}

}
