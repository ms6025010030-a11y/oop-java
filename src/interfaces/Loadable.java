package interfaces;

import java.util.List;
import model.Song;

// Defines data loading capabilities for the music system.

public interface Loadable {
    // Loads all songs from the data source.

    List<Song> loadSongs();
}
