package taskreminder.service;

import taskreminder.model.Priority;
import taskreminder.model.Task;
import taskreminder.storage.TaskRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logika bisnis: CRUD tugas, filter, sorting, pengingat deadline.
 */
public class TaskService {
    private final TaskRepository repository;
    private List<Task> tasks;
    private int nextId;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
        this.tasks = new ArrayList<>(repository.loadAll());
        this.nextId = tasks.stream()
                .mapToInt(Task::getId)
                .max()
                .orElse(0) + 1;
    }

    public Task addTask(String title, String subject, LocalDate deadline, Priority priority) {
        Task task = new Task(nextId++, title, subject, deadline, priority);
        tasks.add(task);
        save();
        return task;
    }

    public boolean markDone(int id) {
        Task task = findById(id);
        if (task == null) return false;
        task.setDone(true);
        save();
        return true;
    }

    public boolean markUndone(int id) {
        Task task = findById(id);
        if (task == null) return false;
        task.setDone(false);
        save();
        return true;
    }

    public boolean deleteTask(int id) {
        boolean removed = tasks.removeIf(t -> t.getId() == id);
        if (removed) save();
        return removed;
    }

    public Task findById(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Task> getAllTasks() {
        return List.copyOf(tasks);
    }

    public List<Task> getActiveTasks() {
        return tasks.stream()
                .filter(t -> !t.isDone())
                .sorted(Comparator.comparing(Task::getDeadline))
                .collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks() {
        return tasks.stream()
                .filter(Task::isDone)
                .collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {
        return tasks.stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Tugas yang deadline-nya dalam N hari ke depan (belum selesai).
     */
    public List<Task> getUpcoming(int withinDays) {
        return tasks.stream()
                .filter(t -> !t.isDone())
                .filter(t -> {
                    long d = t.daysUntilDeadline();
                    return d >= 0 && d <= withinDays;
                })
                .sorted(Comparator.comparing(Task::getDeadline))
                .collect(Collectors.toList());
    }

    /**
     * Filter berdasarkan mata kuliah.
     */
    public List<Task> getBySubject(String subject) {
        return tasks.stream()
                .filter(t -> t.getSubject().equalsIgnoreCase(subject))
                .sorted(Comparator.comparing(Task::getDeadline))
                .collect(Collectors.toList());
    }

    /**
     * Filter berdasarkan prioritas.
     */
    public List<Task> getByPriority(Priority priority) {
        return tasks.stream()
                .filter(t -> t.getPriority() == priority && !t.isDone())
                .sorted(Comparator.comparing(Task::getDeadline))
                .collect(Collectors.toList());
    }

    public boolean updateTask(int id, String title, String subject, LocalDate deadline, Priority priority) {
        Task task = findById(id);
        if (task == null) return false;
        if (title != null && !title.isBlank()) task.setTitle(title);
        if (subject != null && !subject.isBlank()) task.setSubject(subject);
        if (deadline != null) task.setDeadline(deadline);
        if (priority != null) task.setPriority(priority);
        save();
        return true;
    }

    private void save() {
        repository.saveAll(tasks);
    }
}
