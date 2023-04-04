package com.askus.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to store Todo class objects in.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {}
