package com.askus.todo;

import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.*;

//@TestClassOrder(ClassOrderer.ClassName.class)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integration-test.properties")
class TodoServiceTest {
    @Autowired
    private   TodoRepository todoRepository;

    private   TodoService todoService;
    // Because TodServiceTest and TodoControllerTest can run in parallel, the primary key may not be what is expected
    private List<Todo> setupTodos;

    @BeforeAll
     void setUp() {
        todoService = new TodoService(todoRepository);
        setupTodos = new ArrayList<>();
        setupTodos.add(todoService.createTodo(new Todo(1L, "TodoServiceTest Setup1", true)));
        setupTodos.add(todoService.createTodo(new Todo(2L, "TodoServiceTest Setup2", false)));
        setupTodos.add(todoService.createTodo(new Todo(3L, "TodoServiceTest Setup3", false)));
    }

    @AfterAll
     void tearDown() {
    }

    @Test @Order(3)
    void createTodo() {
        Todo todo = todoService.createTodo(new Todo(1L, "TodoServiceTest CreateTodo", false));
        assertEquals("TodoServiceTest createTodo Title not saved", "TodoServiceTest CreateTodo", todo.getTitle()  );
        assertFalse("TodoServiceTest createTodo completed boolean not saved", todo.getCompleted());
    }

    @Test @Order(1)
    void getTodos() {
        List<Todo> todos = todoService.getTodos();
        List<Todo> classTodos = new ArrayList<>();
        for(Todo todo : todos) {
            // Avoid Todos from TodoControllerTest
            if(todo.getTitle().contains("TodoServiceTest"))
                classTodos.add(todo);

        }
        assertEquals("TodoServiceTest getTodos", 3, classTodos.size());
    }

    @Test @Order(2)
    void getTodo() throws NotFoundException {
        Todo t = todoService.getTodo(setupTodos.get(1).getId());
        assertEquals("TodoServiceTest getTodo  Title", "TodoServiceTest Setup2", t.getTitle());
        assertFalse("TodoServiceTest getTodo  completed", t.getCompleted());
        assertEquals("TodoServiceText getTodo id", setupTodos.get(1).getId(), t.getId());
    }

    @Test @Order(3)
    void updateTodo() throws NotFoundException {
        Todo t = todoService.getTodo(setupTodos.get(1).getId());
        t.setCompleted(false);
        t.setTitle("TodoServiceTest updateTodo");
        Todo t1 = todoService.updateTodo(setupTodos.get(1).getId(), t);
        assertEquals("TodoServiceTest updateTodo title ", "TodoServiceTest updateTodo", t1.getTitle() );
        assertFalse("TodoServiceTest updateTodo completed ", t1.getCompleted());
        assertEquals("TodoServiceTest updateTodo  id ", setupTodos.get(1).getId(), t1.getId());
    }

    @Test @Order(4)
    void deleteTodo() {

        List<Todo> todos = todoService.getTodos();
        Todo found = null;
        for(int i = 0; i < todos.size(); i++) {
            Todo t = todos.get(i+1);
            if(t.getTitle().contains("TodoServiceTest")) {
                found = t;
                break;
            }
        }

        if(found != null) {
            final long index = found.getId();
            todoService.deleteTodo(index);
            NotFoundException thrown = assertThrows(
                    NotFoundException.class,
                    () -> todoService.getTodo(index),
                    "Expected getTodo() to throw NotFoundException, but it didn't"
            );

            assertTrue("TodoServiceTest deleteTodo failed to find",
                    thrown.getMessage().contentEquals("Todo does not exist"));
        } else {
            assertFalse("TodoServiceTest deleteTodo failed to find", true);
        }
    }
}