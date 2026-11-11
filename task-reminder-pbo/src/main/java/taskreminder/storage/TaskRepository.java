package taskreminder.storage;

import taskreminder.model.Task;
import java.util.List;

/**
 * Kontrak penyimpanan tugas.
 * Implementasi bisa file CSV, database, atau apapun.
 */
public interface TaskRepository {
    List<Task> loadAll();
    void saveAll(List<Task> tasks);
}
