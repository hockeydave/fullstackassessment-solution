package com.askus.todo;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Web Service Controller for handing web requests coming into the todos rest endpoint
 */
@Controller
@RequestMapping(value = "/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @RequestMapping(method = POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody TodoDto create(@RequestBody TodoDto newTodoDto) {
        Todo newTodo = TodoDto.toEntity(newTodoDto);
        Todo createdTodo = todoService.createTodo(newTodo);
        return TodoDto.fromEntity(createdTodo);
    }

    /**
     * Get the List of Todos in DTO format
     * @return List<TodoDto></TodoDto> for all the created Todos.
     */
    @RequestMapping(method = GET)
    public @ResponseBody List<TodoDto> get() {
        List<Todo> allTodos = todoService.getTodos();
        return allTodos.stream()
                       .map(TodoDto::fromEntity)
                       .collect(Collectors.toList());
    }

    /**
     * Get a Todo by it's Id
     * @param id database primary key for this object
     * @return the TodoDto representation of this Todo object
     * @throws NotFoundException when the primary key does not exists
     */
    @RequestMapping(value = "/{id}", method = GET)
    public @ResponseBody TodoDto get(@PathVariable("id") Long id) throws NotFoundException {
        Todo todo = todoService.getTodo(id);
        return TodoDto.fromEntity(todo);
    }

    /**
     * Store a TodoDto into the data store
     * @param id the database primary key for this TodoDto
     * @param updatedTodoDto DTO representation of the Todo to store
     * @return return the DTO representation of the inserted Todo
     */
    @RequestMapping(value = "/{id}", method = PUT)
    public @ResponseBody TodoDto put(@PathVariable("id") Long id,
                                     @RequestBody TodoDto updatedTodoDto) {
        Todo updatedTodo = TodoDto.toEntity(updatedTodoDto);
        Todo savedTodo = todoService.updateTodo(id, updatedTodo);
        return TodoDto.fromEntity(savedTodo);
    }

    /**
     * Delete the Todo represented by this primary key from the data store.
     * @param id the object identifier in the data store
     */
    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteById(@PathVariable("id") Long id) {
        todoService.deleteTodo(id);
    }

}
