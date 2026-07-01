package interfaces;

// Defines basic playback controls for a music player.

public interface Playable {
    // Starts or resumes playback.

    boolean play();

    // Pauses the currently playing song.

    boolean pause();

    // Skips to the next song in the queue.

    boolean skipToNext();

    // Returns to the previous song in history.

    boolean skipToPrevious();
}
