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
    private TodoRepository todoRepository;

    private TodoService todoService;
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

    /**
     * Test creating a single Todo.
     */
    @Test
    @Order(3)
    void createTodo() {
        Todo todo = todoService.createTodo(new Todo(1L, "TodoServiceTest CreateTodo", false));
        assertEquals("TodoServiceTest createTodo Title not saved", "TodoServiceTest CreateTodo", todo.getTitle());
        assertFalse("TodoServiceTest createTodo completed boolean not saved", todo.getCompleted());
    }

    /**
     * Test getting all the Todos and validating that the list contains those created in Setup.
     */
    @Test
    @Order(1)
    void getTodos() {
        List<Todo> todos = todoService.getTodos();
        List<Todo> classTodos = new ArrayList<>();
        for (Todo todo : todos) {
            // Avoid Todos from TodoControllerTest
            if (todo.getTitle().contains("TodoServiceTest"))
                classTodos.add(todo);

        }
        assertEquals("TodoServiceTest getTodos", 3, classTodos.size());
    }

    /**
     * Test getting the 2nd Todo created in Setup
     *
     * @throws NotFoundException when the Todo is not found
     */
    @Test
    @Order(2)
    void getTodo() throws NotFoundException {
        Todo t = todoService.getTodo(setupTodos.get(1).getId());
        assertEquals("TodoServiceTest getTodo  Title", "TodoServiceTest Setup2", t.getTitle());
        assertFalse("TodoServiceTest getTodo  completed", t.getCompleted());
        assertEquals("TodoServiceText getTodo id", setupTodos.get(1).getId(), t.getId());
    }

    /**
     * Test updating the 2nd Todo created in Setup.
     *
     * @throws NotFoundException when we can't get the Todo from Setup
     */
    @Test
    @Order(3)
    void updateTodo() throws NotFoundException {
        Todo t = todoService.getTodo(setupTodos.get(1).getId());
        t.setCompleted(false);
        t.setTitle("TodoServiceTest updateTodo");
        Todo t1 = todoService.updateTodo(setupTodos.get(1).getId(), t);
        assertEquals("TodoServiceTest updateTodo title ", "TodoServiceTest updateTodo", t1.getTitle());
        assertFalse("TodoServiceTest updateTodo completed ", t1.getCompleted());
        assertEquals("TodoServiceTest updateTodo  id ", setupTodos.get(1).getId(), t1.getId());
    }

    /**
     * Test deleting the 3rd Todo created in Setup can be successfully deleted
     */
    @Test
    @Order(4)
    void deleteTodo() {
        todoService.deleteTodo(setupTodos.get(2).getId());
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> todoService.getTodo(setupTodos.get(2).getId()),
                "Expected todoController.get to throw NotFoundException, but it didn't"
        );

        assertTrue("TodoControllerTest deleteById get()",
                thrown.getMessage().contentEquals("Todo does not exist"));
    }
}
