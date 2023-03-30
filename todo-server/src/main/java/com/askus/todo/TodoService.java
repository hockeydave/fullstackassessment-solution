package com.askus.todo;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    Todo createTodo(Todo newTodo) {
        return todoRepository.save(newTodo);
    }

    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    public Todo getTodo(Long id) throws NotFoundException {
        return todoRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Todo does not exist"));
    }

    public Todo updateTodo(Long id, Todo updatedTodo) {
        return todoRepository.save(updatedTodo);
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

}
