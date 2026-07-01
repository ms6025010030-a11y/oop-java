package interfaces;

import java.util.ArrayList;
import model.Song;

// Defines search and filter operations for the music library.

public interface Searchable {

    // Searches for all songs by a given artist name (case-insensitive).

    ArrayList<Song> searchSongsByArtist(String artistName);

    // Searches for all songs from a given album title (case-insensitive).

    ArrayList<Song> searchSongsByAlbum(String albumTitle);

    // Filters songs by genre (case-insensitive).

    ArrayList<Song> filterSongsByGenre(String genre);
}
