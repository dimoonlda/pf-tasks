package interview.pf.tasks.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Long id;
    private String title;
    private LocalDateTime deadLine;
    private TaskPriority priority;
    private Boolean expired;

    public Long getId() {
        return id;
    }

    public Task setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Task setTitle(String title) {
        this.title = title;
        return this;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public Task setDeadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
        return this;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public Task setPriority(TaskPriority priority) {
        this.priority = priority;
        return this;
    }

    public Boolean isExpired() {
        return Objects.nonNull(this.deadLine) && LocalDateTime.now().isAfter(this.deadLine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id.equals(task.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", deadLine=" + deadLine +
                ", priority=" + priority +
                '}';
    }
}
