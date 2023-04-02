package com.askus.todo;

import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

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

    @BeforeAll
     void setUp() {
        todoService = new TodoService(todoRepository);
        todoService.createTodo(new Todo(1L, "Setup1", true));
        todoService.createTodo(new Todo(2L, "Setup2", false));
        todoService.createTodo(new Todo(3L, "Setup3", false));
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
        assertEquals("TodoServiceTest getTodos", 3, todos.size());
    }

    @Test @Order(2)
    void getTodo() throws NotFoundException {
        Todo t = todoService.getTodo(2L);
        assertEquals("TodoServiceTest getTodo  Title", "Setup2", t.getTitle());
        assertFalse("TodoServiceTest getTodo  completed", t.getCompleted());
        assertEquals("TodoServiceText getTodo id", 2L, t.getId());
    }

    @Test @Order(3)
    void updateTodo() throws NotFoundException {
        Todo t = todoService.getTodo(2L);
        t.setCompleted(false);
        t.setTitle("TodoServiceTest updateTodo");
        Todo t1 = todoService.updateTodo(2L, t);
        assertEquals("TodoServiceTest updateTodo title ", "TodoServiceTest updateTodo", t1.getTitle() );
        assertFalse("TodoServiceTest updateTodo completed ", t1.getCompleted());
        assertEquals("TodoServiceTest updateTodo  id ", 2L, t1.getId());
    }

    @Test @Order(4)
    void deleteTodo() {
        todoService.deleteTodo(3L);
        List<Todo> todos = todoService.getTodos();
        assertEquals("TodoServiceTest deleteTodo", 2, todos.size());
    }
}