
package gerenciadortarefas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Task {
    public enum Priority { LOW, MEDIUM, HIGH }

    private String id;
    private String title;
    private String description;
    private Priority priority;
    private String dueDate; // armazenado como ISO yyyy-MM-dd
    private boolean completed;

    public Task() {
        this.id = UUID.randomUUID().toString();
    }

    public Task(String title, String description, Priority priority, String dueDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
    }

    // getters e setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        String status = completed ? "✓" : " ";
        String due = (dueDate == null || dueDate.isEmpty()) ? "no due" : dueDate;
        return String.format("[%s] %s (prio: %s, due: %s)", status, title, priority, due);
    }
}

