package taskreminder.model;

/**
 * Prioritas tugas: LOW, MEDIUM, HIGH.
 */
public enum Priority {
    LOW("Rendah"),
    MEDIUM("Sedang"),
    HIGH("Tinggi");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Parse dari input user (angka 1-3 atau nama).
     * Return null kalau input tidak valid.
     */
    public static Priority fromInput(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        switch (trimmed) {
            case "1": return LOW;
            case "2": return MEDIUM;
            case "3": return HIGH;
            default:
                try {
                    return valueOf(trimmed.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
        }
    }
}
