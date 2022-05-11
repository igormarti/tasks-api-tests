package br.im.imrocha.tasks.apitest;

import java.time.LocalDate;

import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import br.im.imrocha.tasks.apitest.dto.Task;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class ApiTest {

	public static String objectToJSON(Object obj) {
		return new Gson().toJson(obj);
	}
	
	@BeforeClass
	public static void setupTest() {
		RestAssured.baseURI = "http://localhost:8081/tasks-backend";
	}
	
	@Test
	public void shouldReturnAllTasks() {
		RestAssured
		.given()
		.when()
			.get("/todo")
		.then()
			.statusCode(200);		
	}
	
	@Test
	public void shouldAddTask() {
		
		Task todo = new Task();
		todo.task = "tests task";
		todo.dueDate = LocalDate.now().plusMonths(1).toString();
		
		RestAssured
		.given()
			.body(objectToJSON(todo))
			.contentType(ContentType.JSON)
		.when()
			.post("/todo")
		.then()
			.statusCode(201);		
	}
	
	@Test
	public void shouldNotAddInvalidTask() {
		
		Gson gson = new Gson();
		Task todo = new Task();
		todo.task = "tests task";
		todo.dueDate = LocalDate.now().minusYears(10).toString();
		
		RestAssured
		.given()
			.body(gson.toJson(todo))
			.contentType(ContentType.JSON)
		.when()
			.post("/todo")
		.then()
			.body("message", CoreMatchers.is("Due date must not be in past"))
			.statusCode(400);		
	}

	@Test
	public void shouldRemoveTask() {
		
		Task todo = new Task();
		todo.task = "tests task";
		todo.dueDate = LocalDate.now().plusMonths(1).toString();
		
		Integer id = RestAssured
		.given()
			.body(objectToJSON(todo))
			.contentType(ContentType.JSON)
		.when()
			.post("/todo")
		.then()
			.statusCode(201).extract().path("id");
			
		RestAssured.given()
		.when()
			.delete("/todo/"+id)
		.then()
			.statusCode(204);		
	}
}

