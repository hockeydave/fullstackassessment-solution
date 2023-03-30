package com.askus.todo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TodoDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String title;

    private Boolean completed;

    public TodoDto() {}

    public TodoDto(Long id, String title, Boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public static TodoDto fromEntity(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .build();
    }

    public static Todo toEntity(TodoDto dto) {
        return Todo.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .completed(dto.getCompleted())
                .build();
    }

    private static Builder builder() {
        return new Builder();
    }

    private static class Builder {

        private Long id;
        private String title;
        private Boolean completed;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder completed(Boolean completed) {
            this.completed = completed;
            return this;
        }

        public TodoDto build() {
            return new TodoDto(
                    this.id,
                    this.title,
                    this.completed);
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
