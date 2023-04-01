package com.askus.todo;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Web service to handle creation, retrieval, and update of Todo items
 */

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    /**
     * Construct a TodoService from a todoRepository (data storage).  Called by SpringBoot
     * @param todoRepository data storage (typically database)
     */
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * Store a new Todo item into the todoRepository
     * @param newTodo Todo item to be saved to the repository
     * @return the Todo that was created
     */
    Todo createTodo(Todo newTodo) {
        return todoRepository.save(newTodo);
    }

    /**
     * Get List of the Todo items from the todoRepository
     * @return List<Todo>all items from todoRepository</Todo>
     */
    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    /**
     * Get a Todo item by Id (primary key) from the todoRepository
     * @param id primary key of the Todo item in the repository
     * @return Todo item referenced by id.
     * @throws NotFoundException if the id (primary key) does not exist in the repository
     */
    public Todo getTodo(Long id) throws NotFoundException {
        return todoRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Todo does not exist"));
    }

    /**
     * Update a Todo item referenced by primary key id in the todoRepository
     * @param id repository primary key
     * @param updatedTodo Todo containing the updates
     * @return the updated Todo
     */
    public Todo updateTodo(Long id, Todo updatedTodo) {
        return todoRepository.save(updatedTodo);
    }

    /**
     * Delete a Todo referenced by primary key id in the todoRepository
     * @param id
     */
    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

}
