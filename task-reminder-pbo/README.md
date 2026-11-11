# Task Reminder (Sistem Pengingat Tugas - PBO Java)

Aplikasi console interaktif untuk mengelola dan memantau deadline tugas kuliah. Dibuat dengan menerapkan prinsip Pemrograman Berorientasi Objek (PBO) dan dibungkus dalam container Docker multi-stage build.

## Struktur Project

```text
task-reminder-pbo/
├── Dockerfile                  # Multi-stage build (JDK 17 build -> JRE 17 runtime)
├── docker-compose.yml          # Konfigurasi container interaktif + persistent volume
├── run.bat                     # Shortcut Windows untuk menjalankan container
├── run.sh                      # Shortcut Linux/macOS
└── src/main/java/taskreminder/
    ├── Main.java               # Entry point aplikasi
    ├── model/
    │   ├── Priority.java       # Enum prioritas (LOW, MEDIUM, HIGH)
    │   └── Task.java           # Objek tugas (enkapsulasi, formatting, perhitungan deadline)
    ├── storage/
    │   ├── TaskRepository.java # Interface abstraksi penyimpanan data
    │   └── CsvTaskRepository.java # Implementasi penyimpanan ke file CSV
    ├── service/
    │   └── TaskService.java    # Logika bisnis: CRUD, filter, reminder deadline
    └── ui/
        └── ConsoleUI.java      # Antarmuka menu console interaktif
```

## Penerapan Konsep PBO

1. **Encapsulation**: Atribut pada `Task` berstatus `private` dengan akses melalui getter/setter dan method perilaku seperti `daysUntilDeadline()` dan `isOverdue()`.
2. **Abstraction**: `TaskRepository` memisahkan kebutuhan penyimpanan dari implementasi teknis. Logika aplikasi tidak bergantung pada apakah data disimpan di file CSV atau database.
3. **Polymorphism**: `TaskService` menerima interface `TaskRepository`, memudahkan pergantian implementasi penyimpanan kapan saja.
4. **Single Responsibility**: Tiap package memisahkan peran dengan tegas (model data, penyimpanan, logika bisnis, dan interaksi user).

## Cara Menjalankan

### Opsi 1: Menggunakan Docker Compose (Direkomendasikan)

Pastikan Docker Desktop aktif di komputer Anda.

```bash
# Jalankan container interaktif
docker compose run --rm task-reminder
```

Data tugas tersimpan otomatis di Docker volume `task-data`, sehingga data tetap aman meskipun container dimatikan.

### Opsi 2: Menggunakan Docker Langsung

```bash
# 1. Build image
docker build -t task-reminder-pbo .

# 2. Jalankan interaktif dengan volume
docker run -it --rm -v task-data:/app/data task-reminder-pbo
```

### Opsi 3: Menjalankan Lokal (Tanpa Docker)

Jika JDK 17+ sudah terpasang di komputer:

```bash
# Windows PowerShell / CMD
javac -d out src/main/java/taskreminder/**/*.java src/main/java/taskreminder/*.java
java -cp out taskreminder.Main

# Linux / macOS
javac -d out $(find src/main/java -name "*.java")
java -cp out taskreminder.Main
```

## Fitur Aplikasi

- **Notifikasi Saat Buka**: Menampilkan daftar tugas yang melewati deadline dan tugas yang jatuh tempo dalam 3 hari ke depan saat aplikasi dibuka.
- **Manajemen Tugas**: Tambah, edit, tandai selesai, batalkan status selesai, dan hapus tugas.
- **Filter**: Filter berdasarkan mata kuliah atau tingkat prioritas.
- **Perhitungan Sisa Waktu**: Menghitung otomatis sisa hari atau keterlambatan dari hari ini.
- **Penyimpanan CSV**: Data disimpan ke file teks terstruktur tanpa perlu database eksternal.
