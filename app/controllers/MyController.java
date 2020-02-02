package controllers;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.text.ParseException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import model.Task;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.TaskUtil;

public class MyController extends Controller {

	private HttpExecutionContext ec;
	private TaskUtil taskUtil;

	@Inject
	public MyController(HttpExecutionContext ec, TaskUtil taskUtil) {
		this.ec = ec;
		this.taskUtil = taskUtil;
	}

	public CompletionStage<Result> create(Http.Request request) {
		JsonNode json = request.body().asJson();
		return supplyAsync(() -> {
			if (json == null) {
				return badRequest(TaskUtil.createResponse("Expecting Json data", false));
			}

			Optional<Task> taskOptional = taskUtil.addTask(Json.fromJson(json, Task.class));
			return taskOptional.map(task -> {
				JsonNode jsonObject = Json.toJson(task);
				return created(TaskUtil.createResponse(jsonObject, true));
			}).orElse(internalServerError(TaskUtil.createResponse("Could not create data.", false)));
		}, ec.current());
	}

	public CompletionStage<Result> retrieve(long id) {
		return supplyAsync(() -> {
			final Optional<Task> taskOptional = taskUtil.getTask(id);
			return taskOptional.map(task -> {
				JsonNode jsonObjects = Json.toJson(task);
				return ok(TaskUtil.createResponse(jsonObjects, true));
			}).orElse(notFound(TaskUtil.createResponse("Task with id:" + id + " not found", false)));
		}, ec.current());
	}

	public CompletionStage<Result> update(Http.Request request, Long id) throws ParseException {
		JsonNode json = request.body().asJson();
		return supplyAsync(() -> {
			if (json == null) {
				return badRequest(TaskUtil.createResponse("Expecting Json data", false));
			}
			Optional<Task> taskOptional = taskUtil.updateTask(Json.fromJson(json, Task.class),id);
			return taskOptional.map(task -> {
				if (task == null) {
					return notFound(TaskUtil.createResponse("Task not found", false));
				}
				JsonNode jsonObject = Json.toJson(task);
				return ok(TaskUtil.createResponse(jsonObject, true));
			}).orElse(internalServerError(TaskUtil.createResponse("Could not update task.", false)));
		}, ec.current());
	}

}
