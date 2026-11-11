package taskreminder.ui;

import taskreminder.model.Priority;
import taskreminder.model.Task;
import taskreminder.service.TaskService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Antarmuka console interaktif.
 */
public class ConsoleUI {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final TaskService service;
    private final Scanner scanner;

    public ConsoleUI(TaskService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        showStartupReminders();
        boolean running = true;
        while (running) {
            showMenu();
            String input = prompt("Pilih menu").trim();
            switch (input) {
                case "1" -> addTask();
                case "2" -> listActive();
                case "3" -> markDone();
                case "4" -> markUndone();
                case "5" -> editTask();
                case "6" -> deleteTask();
                case "7" -> showUpcoming();
                case "8" -> filterBySubject();
                case "9" -> filterByPriority();
                case "10" -> listCompleted();
                case "11" -> listOverdue();
                case "0" -> running = false;
                default -> System.out.println("Input tidak valid.");
            }
            System.out.println();
        }
        System.out.println("Sampai jumpa.");
    }

    private void showMenu() {
        System.out.println("=== TASK REMINDER - PBO ===");
        System.out.println(" 1. Tambah tugas");
        System.out.println(" 2. Lihat tugas aktif");
        System.out.println(" 3. Tandai selesai");
        System.out.println(" 4. Batalkan selesai");
        System.out.println(" 5. Edit tugas");
        System.out.println(" 6. Hapus tugas");
        System.out.println(" 7. Tugas mendatang (3 hari)");
        System.out.println(" 8. Filter mata kuliah");
        System.out.println(" 9. Filter prioritas");
        System.out.println("10. Lihat yang sudah selesai");
        System.out.println("11. Lihat yang melewati deadline");
        System.out.println(" 0. Keluar");
    }

    private void addTask() {
        String title = prompt("Judul tugas");
        if (title.isBlank()) {
            System.out.println("Judul tidak boleh kosong.");
            return;
        }
        String subject = prompt("Mata kuliah");
        if (subject.isBlank()) {
            System.out.println("Mata kuliah tidak boleh kosong.");
            return;
        }
        LocalDate deadline = promptDate("Deadline (dd-MM-yyyy)");
        if (deadline == null) return;

        System.out.println("Prioritas: 1=Rendah, 2=Sedang, 3=Tinggi");
        Priority priority = Priority.fromInput(prompt("Prioritas"));
        if (priority == null) {
            System.out.println("Prioritas tidak valid. Menggunakan MEDIUM.");
            priority = Priority.MEDIUM;
        }

        Task task = service.addTask(title, subject, deadline, priority);
        System.out.println("Tugas ditambahkan: " + task);
    }

    private void listActive() {
        List<Task> active = service.getActiveTasks();
        if (active.isEmpty()) {
            System.out.println("Tidak ada tugas aktif.");
            return;
        }
        System.out.println("--- Tugas Aktif ---");
        active.forEach(t -> System.out.println("  " + t));
    }

    private void markDone() {
        listActive();
        int id = promptInt("ID tugas yang selesai");
        if (id < 0) return;
        if (service.markDone(id)) {
            System.out.println("Tugas #" + id + " ditandai selesai.");
        } else {
            System.out.println("Tugas #" + id + " tidak ditemukan.");
        }
    }

    private void markUndone() {
        List<Task> completed = service.getCompletedTasks();
        if (completed.isEmpty()) {
            System.out.println("Tidak ada tugas yang sudah selesai.");
            return;
        }
        System.out.println("--- Tugas Selesai ---");
        completed.forEach(t -> System.out.println("  " + t));
        int id = promptInt("ID tugas yang ingin dibatalkan");
        if (id < 0) return;
        if (service.markUndone(id)) {
            System.out.println("Tugas #" + id + " dikembalikan ke aktif.");
        } else {
            System.out.println("Tugas #" + id + " tidak ditemukan.");
        }
    }

    private void editTask() {
        listActive();
        int id = promptInt("ID tugas yang ingin diedit");
        if (id < 0) return;
        Task existing = service.findById(id);
        if (existing == null) {
            System.out.println("Tugas #" + id + " tidak ditemukan.");
            return;
        }
        System.out.println("Kosongkan field untuk tidak mengubah.");
        String title = prompt("Judul baru [" + existing.getTitle() + "]");
        String subject = prompt("Mata kuliah baru [" + existing.getSubject() + "]");
        String dateStr = prompt("Deadline baru [" + existing.getDeadline().format(FMT) + "]");
        LocalDate deadline = null;
        if (!dateStr.isBlank()) {
            deadline = parseDate(dateStr);
            if (deadline == null) {
                System.out.println("Format tanggal salah, deadline tidak diubah.");
            }
        }
        System.out.println("Prioritas: 1=Rendah, 2=Sedang, 3=Tinggi");
        String priStr = prompt("Prioritas baru [" + existing.getPriority().getLabel() + "]");
        Priority priority = priStr.isBlank() ? null : Priority.fromInput(priStr);

        service.updateTask(id, title, subject, deadline, priority);
        System.out.println("Tugas #" + id + " diperbarui.");
    }

    private void deleteTask() {
        listActive();
        int id = promptInt("ID tugas yang ingin dihapus");
        if (id < 0) return;
        String confirm = prompt("Yakin hapus tugas #" + id + "? (y/n)");
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("Dibatalkan.");
            return;
        }
        if (service.deleteTask(id)) {
            System.out.println("Tugas #" + id + " dihapus.");
        } else {
            System.out.println("Tugas #" + id + " tidak ditemukan.");
        }
    }

    private void showUpcoming() {
        List<Task> upcoming = service.getUpcoming(3);
        if (upcoming.isEmpty()) {
            System.out.println("Tidak ada tugas dalam 3 hari ke depan.");
            return;
        }
        System.out.println("--- Deadline dalam 3 Hari ---");
        upcoming.forEach(t -> System.out.println("  " + t));
    }

    private void filterBySubject() {
        String subject = prompt("Nama mata kuliah");
        List<Task> result = service.getBySubject(subject);
        if (result.isEmpty()) {
            System.out.println("Tidak ada tugas untuk \"" + subject + "\".");
            return;
        }
        System.out.println("--- " + subject + " ---");
        result.forEach(t -> System.out.println("  " + t));
    }

    private void filterByPriority() {
        System.out.println("1=Rendah, 2=Sedang, 3=Tinggi");
        Priority p = Priority.fromInput(prompt("Prioritas"));
        if (p == null) {
            System.out.println("Prioritas tidak valid.");
            return;
        }
        List<Task> result = service.getByPriority(p);
        if (result.isEmpty()) {
            System.out.println("Tidak ada tugas dengan prioritas " + p.getLabel() + ".");
            return;
        }
        System.out.println("--- Prioritas: " + p.getLabel() + " ---");
        result.forEach(t -> System.out.println("  " + t));
    }

    private void listCompleted() {
        List<Task> completed = service.getCompletedTasks();
        if (completed.isEmpty()) {
            System.out.println("Belum ada tugas yang selesai.");
            return;
        }
        System.out.println("--- Tugas Selesai ---");
        completed.forEach(t -> System.out.println("  " + t));
    }

    private void listOverdue() {
        List<Task> overdue = service.getOverdueTasks();
        if (overdue.isEmpty()) {
            System.out.println("Tidak ada tugas yang melewati deadline.");
            return;
        }
        System.out.println("--- Melewati Deadline ---");
        overdue.forEach(t -> System.out.println("  " + t));
    }

    private void showStartupReminders() {
        List<Task> overdue = service.getOverdueTasks();
        List<Task> upcoming = service.getUpcoming(3);
        if (!overdue.isEmpty()) {
            System.out.println("!! " + overdue.size() + " tugas sudah melewati deadline:");
            overdue.forEach(t -> System.out.println("   " + t));
            System.out.println();
        }
        if (!upcoming.isEmpty()) {
            System.out.println(">> " + upcoming.size() + " tugas deadline dalam 3 hari:");
            upcoming.forEach(t -> System.out.println("   " + t));
            System.out.println();
        }
    }

    // --- Helpers ---

    private String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine();
    }

    private int promptInt(String label) {
        String s = prompt(label);
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            System.out.println("Input harus angka.");
            return -1;
        }
    }

    private LocalDate promptDate(String label) {
        String s = prompt(label);
        LocalDate d = parseDate(s);
        if (d == null) {
            System.out.println("Format tanggal salah. Gunakan dd-MM-yyyy.");
        }
        return d;
    }

    private LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s.trim(), FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
