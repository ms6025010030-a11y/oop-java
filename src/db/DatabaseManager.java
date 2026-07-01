package db;

import java.util.ArrayList;
import java.util.HashMap;

import interfaces.Loadable;
import model.Album;
import model.Artist;
import model.Song;

// Manages the in-memory database for the music application.
// Provides pre-loaded sample data for artists, albums, and songs.

public class DatabaseManager implements Loadable {
    private static final String DATABASE_NAME = "MusicDB";

    // Loads and returns a list of sample songs with their associated
    // artists and albums. Establishes bidirectional relationships
    // between songs, artists, and albums.

    @Override
    public ArrayList<Song> loadSongs() {
        ArrayList<Song> songs = new ArrayList<>();

        HashMap<Integer, Artist> artistMap = new HashMap<>();
        HashMap<Integer, Album> albumMap = new HashMap<>();

        Artist artist1 = getOrCreateArtist(artistMap, 1, "Dara Music", "Cambodia");
        Artist artist2 = getOrCreateArtist(artistMap, 2, "Sokha Band", "Cambodia");

        Album album1 = getOrCreateAlbum(albumMap, 1, "Morning Playlist", artist1, 2024);
        Album album2 = getOrCreateAlbum(albumMap, 2, "Night Drive", artist2, 2025);

        Song song1 = new Song(1, "Sunrise", artist1, album1, "Pop", 210);
        Song song2 = new Song(2, "Coffee Time", artist1, album1, "Acoustic", 185);
        Song song3 = new Song(3, "City Lights", artist2, album2, "Rock", 240);
        Song song4 = new Song(4, "Rainy Night", artist2, album2, "Jazz", 200);

        connectSong(song1);
        connectSong(song2);
        connectSong(song3);
        connectSong(song4);

        songs.add(song1);
        songs.add(song2);
        songs.add(song3);
        songs.add(song4);

        return songs;
    }

    // Retrieves an existing artist from the map or creates a new one
    // if not already present.

    private Artist getOrCreateArtist(HashMap<Integer, Artist> artistMap, int id, String name, String country) {
        if (artistMap.containsKey(id)) {
            return artistMap.get(id);
        }

        Artist artist = new Artist(id, name, country);
        artistMap.put(id, artist);
        return artist;
    }

    // Retrieves an existing album from the map or creates a new one.
    // If a new album is created, it is also registered with the artist.

    private Album getOrCreateAlbum(HashMap<Integer, Album> albumMap, int id, String title, Artist artist, int year) {
        if (albumMap.containsKey(id)) {
            return albumMap.get(id);
        }

        Album album = new Album(id, title, artist, year);
        albumMap.put(id, album);

        if (artist != null) {
            artist.addAlbum(album);
        }

        return album;
    }

    // Establishes bidirectional links between a song and its artist/album.
    // Adds the song to the artist's and album's internal lists.

    private void connectSong(Song song) {
        if (song == null) {
            return;
        }

        Artist artist = song.getArtist();
        Album album = song.getAlbum();

        if (artist != null) {
            artist.addSong(song);
        }

        if (album != null) {
            album.addSong(song);
        }
    }

    // Returns the name of the database.

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }
}
