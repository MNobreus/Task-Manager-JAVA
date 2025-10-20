
package gerenciadortarefas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskManager {
    private List<Task> tasks;
    private TaskStorage storage;

    public TaskManager() {
        storage = new TaskStorage();
        tasks = storage.load();
    }

    public List<Task> getTasks() { return tasks; }

    public void addTask(Task t) {
        tasks.add(t);
        save();
    }

    public void updateTask(Task t) {
        Optional<Task> find = tasks.stream().filter(x -> x.getId().equals(t.getId())).findFirst();
        if (find.isPresent()) {
            Task old = find.get();
            old.setTitle(t.getTitle());
            old.setDescription(t.getDescription());
            old.setPriority(t.getPriority());
            old.setDueDate(t.getDueDate());
            old.setCompleted(t.isCompleted());
            save();
        }
    }

    public void removeTask(String id) {
        tasks.removeIf(t -> t.getId().equals(id));
        save();
    }

    public void toggleComplete(String id) {
        tasks.stream().filter(t -> t.getId().equals(id)).findFirst().ifPresent(t -> {
            t.setCompleted(!t.isCompleted());
            save();
        });
    }

    public void save() { storage.save(tasks); }
}
