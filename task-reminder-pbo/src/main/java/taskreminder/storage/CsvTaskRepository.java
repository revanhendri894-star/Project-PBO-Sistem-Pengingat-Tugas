package taskreminder.storage;

import taskreminder.model.Task;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simpan/muat tugas ke file CSV.
 * Path default: /app/data/tasks.csv (di dalam container)
 * atau ./data/tasks.csv (lokal).
 */
public class CsvTaskRepository implements TaskRepository {
    private final Path filePath;

    public CsvTaskRepository(String path) {
        this.filePath = Paths.get(path);
    }

    @Override
    public List<Task> loadAll() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try {
            List<String> lines = Files.readAllLines(filePath);
            List<Task> tasks = new ArrayList<>();
            for (String line : lines) {
                if (line.isBlank()) continue;
                Task t = Task.fromCsv(line);
                if (t != null) tasks.add(t);
            }
            return tasks;
        } catch (IOException e) {
            System.err.println("Gagal membaca file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void saveAll(List<Task> tasks) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) Files.createDirectories(parent);

            List<String> lines = tasks.stream()
                    .map(Task::toCsv)
                    .collect(Collectors.toList());
            Files.write(filePath, lines, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan file: " + e.getMessage());
        }
    }
}
