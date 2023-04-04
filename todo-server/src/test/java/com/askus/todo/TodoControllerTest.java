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
class TodoControllerTest {
    @Autowired
    private TodoRepository todoRepository;
    private TodoController todoController;
    // Because TodServiceTest and TodoControllerTest can run in parallel, the primary key may not be what is expected
    private List<TodoDto> setupTodos;

    @BeforeAll
    void setUp() {
        TodoService todoService = new TodoService(todoRepository);
        todoController = new TodoController(todoService);
        setupTodos = new ArrayList<>();
        setupTodos.add(todoController.create(new TodoDto(1L, "TodoControllerTest Setup1", true)));
        setupTodos.add(todoController.create(new TodoDto(2L, "TodoControllerTest Setup2", false)));
        setupTodos.add(todoController.create(new TodoDto(3L, "TodoControllerTest Setup3", false)));
    }

    /**
     * Test creating a single Todo.
     */
    @Test
    @Order(3)
    void create() {
        TodoDto dto = todoController.create(new TodoDto(1L, "TodoControllerTest Create 1", false));
        assertEquals("TodoControllerTest create title check", "TodoControllerTest Create 1", dto.getTitle());
        assertFalse("TodoControllerTest create complete check", dto.getCompleted());
    }

    /**
     * Test getting all the Todos and validating that the list contains those created in Setup.
     */
    @Test
    @Order(1)
    void getTodos() {
        List<TodoDto> dtos = todoController.get();
        assertEquals("TodoControllerTest getTodos", 3, dtos.size());
    }

    /**
     * Test getting the 2nd Todo created in Setup
     *
     * @throws NotFoundException when we don't find the Todo
     */
    @Test
    @Order(2)
    void getTodo() throws NotFoundException {
        TodoDto dto = todoController.get(setupTodos.get(0).getId());
        assertEquals("TodoControllerTest getTodo Title", "TodoControllerTest Setup1", dto.getTitle());
        assertTrue("TodoControllerTest getTodo id completed", dto.getCompleted());
        assertEquals("TodoControllerTest getTodo id", setupTodos.get(0).getId(), dto.getId());
    }

    /**
     * Test updating the 2nd Todo created in Setup.
     *
     */
    @Test
    @Order(3)
    void put() {
        TodoDto dto = todoController.create(new TodoDto(setupTodos.get(1).getId(), "TodoControllerTest Put 1", false));
        TodoDto savedDto = todoController.put(setupTodos.get(1).getId(), dto);
        assertEquals("TodoControllerTest put title", "TodoControllerTest Put 1", savedDto.getTitle());
        assertFalse("TodoControllerTest put completed", savedDto.getCompleted());
        assertEquals("TodoControllerTest getTodo id", setupTodos.get(1).getId(), savedDto.getId());
    }

    /**
     * Test deleting the 3rd Todo created in Setup
     */
    @Test
    @Order(4)
    void deleteById() {
        todoController.deleteById(setupTodos.get(2).getId());
    }
}
