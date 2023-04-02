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
class TodoControllerTest {
    @Autowired
    private  TodoRepository todoRepository;
    private TodoController todoController;

    @BeforeAll
    void setUp() {
        TodoService todoService = new TodoService(todoRepository);
        todoController = new TodoController(todoService);
        todoController.create(new TodoDto(1L, "TodoControllerTest Setup1", true));
        todoController.create(new TodoDto(2L, "TodoControllerTest Setup2", false));
        todoController.create(new TodoDto(3L, "TodoControllerTest Setup3", false));
    }

    @Test @Order(3)
    void create() {
        TodoDto dto = todoController.create(new TodoDto(1L, "TodoControllerTest Create 1", false));
        assertEquals("TodoControllerTest create title check", "TodoControllerTest Create 1", dto.getTitle());
        assertFalse("TodoControllerTest create complete check", dto.getCompleted());
    }

    @Test @Order(1)
    void getTodos()  {
        List<TodoDto> dtos = todoController.get();
        assertEquals("TodoControllerTest getTodos", 3, dtos.size());
    }

    @Test @Order(2)
    void getTodo() throws NotFoundException {
        TodoDto dto = todoController.get(1L);
        assertEquals("TodoControllerTest getTodo Title", "TodoControllerTest Setup1", dto.getTitle());
        assertTrue("TodoControllerTest getTodo id completed", dto.getCompleted());
        assertEquals("TodoControllerTest getTodo id", 1L, dto.getId());
    }

    @Test @Order(3)
    void put() {
        TodoDto dto = todoController.create(new TodoDto(1L, "TodoControllerTest Put 1", false));
        TodoDto savedDto = todoController.put(1L, dto);
        assertEquals("TodoControllerTest put title", "TodoControllerTest Put 1", savedDto.getTitle());
        assertFalse("TodoControllerTest put completed", savedDto.getCompleted());
    }

    @Test @Order(4)
    void deleteById() {
        List<TodoDto> todos = todoController.get();
        TodoDto found = null;
        for(int i = 0; i < todos.size(); i++) {
            TodoDto t = todos.get(i+1);
            if(t.getTitle().contains("TodoControllerTest")) {
                found = t;
                break;
            }
        }

        if(found != null) {
            final long index = found.getId();
            todoController.deleteById(index);
            NotFoundException thrown = assertThrows(
                    NotFoundException.class,
                    () -> todoController.get(index),
                    "TodoControllerTest Expected getTodo() to throw NotFoundException, but it didn't"
            );

            assertTrue("TodoControllerTest deleteTodo failed to find",
                    thrown.getMessage().contentEquals("Todo does not exist"));
        } else {
            assertFalse("TodoControllerTest delete failed to find", true);
        }
    }
}