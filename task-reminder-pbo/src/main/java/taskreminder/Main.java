package taskreminder;

import taskreminder.service.TaskService;
import taskreminder.storage.CsvTaskRepository;
import taskreminder.storage.TaskRepository;
import taskreminder.ui.ConsoleUI;

/**
 * Entry point aplikasi Task Reminder.
 *
 * Alur:
 * 1. Buat repository (CSV file di /app/data atau ./data)
 * 2. Inisialisasi service dengan repository
 * 3. Jalankan console UI
 */
public class Main {
    public static void main(String[] args) {
        String dataPath = System.getenv("TASK_DATA_PATH");
        if (dataPath == null || dataPath.isBlank()) {
            dataPath = "data/tasks.csv";
        }

        TaskRepository repository = new CsvTaskRepository(dataPath);
        TaskService service = new TaskService(repository);
        ConsoleUI ui = new ConsoleUI(service);
        ui.run();
    }
}
