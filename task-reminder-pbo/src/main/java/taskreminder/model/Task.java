package taskreminder.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representasi satu tugas.
 *
 * Field:
 * - id         : identifier unik (auto-increment)
 * - title      : judul tugas
 * - subject    : mata kuliah
 * - deadline   : tanggal batas pengumpulan
 * - priority   : LOW / MEDIUM / HIGH
 * - done       : sudah selesai atau belum
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private int id;
    private String title;
    private String subject;
    private LocalDate deadline;
    private Priority priority;
    private boolean done;

    public Task(int id, String title, String subject, LocalDate deadline, Priority priority) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.deadline = deadline;
        this.priority = priority;
        this.done = false;
    }

    // --- Getters & Setters ---

    public int getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    // --- Derived ---

    /**
     * Hitung sisa hari sampai deadline dari hari ini.
     * Negatif berarti sudah lewat.
     */
    public long daysUntilDeadline() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline);
    }

    public boolean isOverdue() {
        return !done && deadline.isBefore(LocalDate.now());
    }

    // --- Serialisasi teks (untuk penyimpanan CSV) ---

    public String toCsv() {
        return String.join(",",
                String.valueOf(id),
                escape(title),
                escape(subject),
                deadline.format(FMT),
                priority.name(),
                String.valueOf(done));
    }

    public static Task fromCsv(String line) {
        String[] parts = line.split(",", 6);
        if (parts.length < 6) return null;
        Task t = new Task(
                Integer.parseInt(parts[0].trim()),
                unescape(parts[1].trim()),
                unescape(parts[2].trim()),
                LocalDate.parse(parts[3].trim(), FMT),
                Priority.valueOf(parts[4].trim()));
        t.done = Boolean.parseBoolean(parts[5].trim());
        return t;
    }

    @Override
    public String toString() {
        String status = done ? "[V]" : (isOverdue() ? "[!]" : "[ ]");
        String deadlineStr = deadline.format(FMT);
        long days = daysUntilDeadline();
        String remaining;
        if (done) {
            remaining = "selesai";
        } else if (days < 0) {
            remaining = Math.abs(days) + " hari lewat";
        } else if (days == 0) {
            remaining = "hari ini";
        } else {
            remaining = days + " hari lagi";
        }
        return String.format("%s #%d | %-20s | %-12s | %s (%s) | %s",
                status, id, title, subject, deadlineStr, remaining, priority.getLabel());
    }

    // --- Helpers untuk escape koma di CSV ---

    private static String escape(String s) {
        return s.replace(",", "\\,");
    }

    private static String unescape(String s) {
        return s.replace("\\,", ",");
    }
}
