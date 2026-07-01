package interfaces;

import model.Song;
import model.Playlist;

// Defines queue management operations for a music player.

public interface QueueManageable {
    // Adds a single song to the playback queue.

    boolean addToQueue(Song song);

    // Adds all songs from a playlist to the playback queue.

    boolean addPlaylistToQueue(Playlist playlist);

    // Removes all songs from the playback queue.

    boolean clearQueue();

    // Randomly shuffles the order of songs in the playback queue.

    boolean shuffleQueue();
}
