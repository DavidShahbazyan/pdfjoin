package am.davsoft.pdfjoin.util;

public interface ProgressViewer {
    default void updateProgress(final int total, final int current) {
        updateProgress("Processing...", total, current);
    }
    void updateProgress(final String message, final int total, final int current);
}
