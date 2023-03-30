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

    @RequestMapping(method = GET)
    public @ResponseBody List<TodoDto> get() {
        List<Todo> allTodos = todoService.getTodos();
        return allTodos.stream()
                       .map(TodoDto::fromEntity)
                       .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = GET)
    public @ResponseBody TodoDto get(@PathVariable("id") Long id) throws NotFoundException {
        Todo todo = todoService.getTodo(id);
        return TodoDto.fromEntity(todo);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public @ResponseBody TodoDto put(@PathVariable("id") Long id,
                                     @RequestBody TodoDto updatedTodoDto) {
        Todo updatedTodo = TodoDto.toEntity(updatedTodoDto);
        Todo savedTodo = todoService.updateTodo(id, updatedTodo);
        return TodoDto.fromEntity(savedTodo);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteById(@PathVariable("id") Long id) {
        todoService.deleteTodo(id);
    }

}
