# CAM Music Player — Complete Documentation

## Project Overview

A console-based Java music player application that manages songs, artists, albums, and playlists with playback controls, queue management, and search functionality. Demonstrates OOP principles: inheritance, polymorphism, interfaces, encapsulation, and static members.

---

## Directory Structure

```
music/
├── .vscode/
│   └── settings.json
├── DOCUMENTATION.md          (this file)
├── src/
│   ├── .vscode/
│   │   └── settings.json
│   ├── compile_and_run.bat
│   ├── lib/                  (empty — for external JARs)
│   ├── interfaces/
│   │   ├── Displayable.java
│   │   ├── Loadable.java
│   │   ├── Playable.java
│   │   ├── QueueManageable.java
│   │   └── Searchable.java
│   ├── model/
│   │   ├── MediaItem.java
│   │   ├── Song.java
│   │   ├── Album.java
│   │   ├── Artist.java
│   │   └── Playlist.java
│   ├── db/
│   │   └── DatabaseManager.java
│   ├── service/
│   │   └── MusicPlayer.java
│   └── main/
│       └── Main.java
```

---

## Package: `interfaces` — Contracts & Abstractions

### `Displayable.java`

Defines a standard display method for objects that can be printed to the console.

```java
package interfaces;

public interface Displayable {
    void displayInfo();
}
```

---

### `Loadable.java`

Defines data loading capabilities for the music system.

```java
package interfaces;

import java.util.List;
import model.Song;

public interface Loadable {
    List<Song> loadSongs();
}
```

---

### `Playable.java`

Defines basic playback controls for a music player.

```java
package interfaces;

public interface Playable {
    boolean play();
    boolean pause();
    boolean skipToNext();
    boolean skipToPrevious();
}
```

---

### `QueueManageable.java`

Defines queue management operations for a music player.

```java
package interfaces;

import model.Song;
import model.Playlist;

public interface QueueManageable {
    boolean addToQueue(Song song);
    boolean addPlaylistToQueue(Playlist playlist);
    boolean clearQueue();
    boolean shuffleQueue();
}
```

---

### `Searchable.java`

Defines search and filter operations for the music library.

```java
package interfaces;

import java.util.ArrayList;
import model.Song;

public interface Searchable {
    ArrayList<Song> searchSongsByArtist(String artistName);
    ArrayList<Song> searchSongsByAlbum(String albumTitle);
    ArrayList<Song> filterSongsByGenre(String genre);
}
```

---

## Package: `model` — Domain Entities

### `MediaItem.java` (Abstract Base Class)

Base class for all media items. Provides common fields (`id`, `title`) and declares `displayInfo()` as abstract.

```java
package model;

import interfaces.Displayable;

public abstract class MediaItem implements Displayable {

    protected int id;
    protected String title;

    public MediaItem(int id, String title) {
        this.id = validateId(id);
        setTitle(cleanText(title, "Unknown Item"));
    }

    public static int validateId(int id) {
        if (id > 0) {
            return id;
        }
        return 0;
    }

    public static String cleanText(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    public abstract void displayInfo();
}
```

---

### `Song.java`

Represents a song with artist, album, genre, and duration.

```java
package model;

public class Song extends MediaItem {
    private Artist artist;
    private Album album;
    private String genre;
    private int durationInSeconds;

    private static int songCount = 0;

    public Song(int songId, String title, Artist artist, Album album, String genre, int durationInSeconds) {
        super(validateId(songId), cleanText(title, "Unknown Song"));
        this.artist = artist;
        this.album = album;
        this.genre = cleanText(genre, "Unknown Genre");
        setDurationInSeconds(durationInSeconds);
        songCount++;
    }

    public Artist getArtist() { return artist; }
    public Album getAlbum() { return album; }
    public String getGenre() { return genre; }
    public int getDurationInSeconds() { return durationInSeconds; }

    public void setArtist(Artist artist) { this.artist = artist; }
    public void setAlbum(Album album) { this.album = album; }

    public void setGenre(String genre) {
        if (genre != null && !genre.trim().isEmpty()) {
            this.genre = genre.trim();
        }
    }

    public void setDurationInSeconds(int durationInSeconds) {
        if (durationInSeconds > 0) {
            this.durationInSeconds = durationInSeconds;
        } else {
            System.err.println("Invalid duration provided for song '" + getTitle() + "'. Setting duration to 0.");
            this.durationInSeconds = 0;
        }
    }

    public String getFormattedDuration() {
        int minutes = durationInSeconds / 60;
        int seconds = durationInSeconds % 60;
        if (seconds < 10) {
            return minutes + ":0" + seconds;
        }
        return minutes + ":" + seconds;
    }

    @Override
    public void displayInfo() {
        String artistName = "Unknown Artist";
        String albumTitle = "Unknown Album";
        if (artist != null) {
            artistName = artist.getName();
        }
        if (album != null) {
            albumTitle = album.getTitle();
        }
        System.out.println("ID: " + id + " | Title: " + title);
        System.out.println("  Artist: " + artistName);
        System.out.println("  Album: " + albumTitle);
        System.out.println("  Genre: " + genre);
        System.out.println("  Duration: " + getFormattedDuration());
    }

    public static int getSongCount() { return songCount; }
}
```

---

### `Album.java`

Represents a collection of songs by a single artist.

```java
package model;

import java.util.ArrayList;

public class Album extends MediaItem {
    private Artist artist;
    private int releaseYear;
    private ArrayList<Song> songs;

    private static int albumCount = 0;

    public Album(int albumId, String title, Artist artist, int releaseYear) {
        super(validateId(albumId), cleanText(title, "Unknown Album"));
        this.artist = artist;
        setReleaseYear(releaseYear);
        this.songs = new ArrayList<>();
        albumCount++;
    }

    public Artist getArtist() { return artist; }
    public int getReleaseYear() { return releaseYear; }

    public void setArtist(Artist artist) { this.artist = artist; }

    public void setReleaseYear(int releaseYear) {
        if (releaseYear > 0) {
            this.releaseYear = releaseYear;
        } else {
            this.releaseYear = 0;
            System.err.println("Invalid release year provided for album '" + getTitle() + "'. Setting release year to 0.");
        }
    }

    public boolean addSong(Song song) {
        if (song == null) {
            System.out.println("Cannot add a null song to album.");
            return false;
        }
        if (!songs.contains(song)) {
            songs.add(song);
            return true;
        }
        return false;
    }

    public ArrayList<Song> getSongsCopy() { return new ArrayList<>(songs); }
    public int getSongListSize() { return songs.size(); }

    @Override
    public void displayInfo() {
        String artistName = "Unknown Artist";
        if (artist != null) {
            artistName = artist.getName();
        }
        System.out.println("ID: " + id + " | Title: " + title);
        System.out.println("  Artist: " + artistName);
        System.out.println("  Release Year: " + releaseYear);
        System.out.println("  Total Songs: " + songs.size());
    }

    public void displaySongs() {
        System.out.println("\nSongs in album " + title + ":");
        if (songs.isEmpty()) {
            System.out.println("No songs in this album yet.");
            return;
        }
        for (Song song : songs) {
            song.displayInfo();
        }
    }

    public static int getAlbumCount() { return albumCount; }
}
```

---

### `Artist.java`

Represents a musical artist with associated songs and albums. Implements `Displayable` directly (does NOT extend `MediaItem`).

```java
package model;

import java.util.ArrayList;
import interfaces.Displayable;

public class Artist implements Displayable {
    private final int artistId;
    private String name;
    private String country;
    private ArrayList<Song> songs;
    private ArrayList<Album> albums;

    private static int artistCount = 0;

    public Artist(int artistId, String name, String country) {
        this.artistId = validateId(artistId);
        this.name = cleanText(name, "Unknown Artist");
        this.country = cleanText(country, "Unknown Country");
        this.songs = new ArrayList<>();
        this.albums = new ArrayList<>();
        artistCount++;
    }

    private int validateId(int id) {
        if (id > 0) return id;
        return 0;
    }

    private String cleanText(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        return value.trim();
    }

    public int getArtistId() { return artistId; }
    public String getName() { return name; }
    public String getCountry() { return country; }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) this.name = name.trim();
    }

    public void setCountry(String country) {
        if (country != null && !country.trim().isEmpty()) this.country = country.trim();
    }

    public boolean addSong(Song song) {
        if (song == null) {
            System.out.println("Cannot add a null song to artist.");
            return false;
        }
        if (!songs.contains(song)) {
            songs.add(song);
            return true;
        }
        return false;
    }

    public boolean addAlbum(Album album) {
        if (album == null) {
            System.out.println("Cannot add a null album to artist.");
            return false;
        }
        if (!albums.contains(album)) {
            albums.add(album);
            return true;
        }
        return false;
    }

    public ArrayList<Song> getSongsCopy() { return new ArrayList<>(songs); }
    public ArrayList<Album> getAlbumsCopy() { return new ArrayList<>(albums); }
    public int getSongListSize() { return songs.size(); }
    public int getAlbumListSize() { return albums.size(); }

    @Override
    public void displayInfo() {
        System.out.println("\n========== Artist Detail ==========");
        System.out.println("Artist ID: " + artistId);
        System.out.println("Name: " + name);
        System.out.println("Country: " + country);
        System.out.println("Songs: " + songs.size());
        System.out.println("Albums: " + albums.size());
        System.out.println("===================================");
    }

    public void displaySongs() {
        System.out.println("\nSongs by " + name + ":");
        if (songs.isEmpty()) {
            System.out.println("No songs for this artist yet.");
            return;
        }
        for (Song song : songs) {
            song.displayInfo();
        }
    }

    public void displayAlbums() {
        System.out.println("\nAlbums by " + name + ":");
        if (albums.isEmpty()) {
            System.out.println("No albums for this artist yet.");
            return;
        }
        for (Album album : albums) {
            album.displayInfo();
        }
    }

    public static int getArtistCount() { return artistCount; }
}
```

---

### `Playlist.java`

Represents a named collection of songs. Implements `Displayable` directly.

```java
package model;

import java.util.ArrayList;
import interfaces.Displayable;

public class Playlist implements Displayable {
    private final int playlistId;
    private String name;
    private ArrayList<Song> songs;

    private static int playlistCount = 0;

    public Playlist(int playlistId, String name) {
        this.playlistId = validateId(playlistId);
        this.name = cleanText(name, "Untitled Playlist");
        this.songs = new ArrayList<>();
        playlistCount++;
    }

    private int validateId(int id) {
        if (id > 0) return id;
        return 0;
    }

    private String cleanText(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        return value.trim();
    }

    public int getPlaylistId() { return playlistId; }
    public String getName() { return name; }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) this.name = name.trim();
    }

    public boolean addSong(Song song) {
        if (song == null) {
            System.out.println("Cannot add a null song to playlist.");
            return false;
        }
        if (songs.contains(song)) {
            System.out.println(song.getTitle() + " is already in playlist " + name + ".");
            return false;
        }
        songs.add(song);
        return true;
    }

    public boolean addSong(Song song, int position) {
        if (song == null) {
            System.out.println("Cannot add a null song to playlist.");
            return false;
        }
        if (songs.contains(song)) {
            System.out.println(song.getTitle() + " is already in playlist " + name + ".");
            return false;
        }
        if (position < 0 || position > songs.size()) {
            System.out.println("Invalid position. Adding to the end instead.");
            songs.add(song);
        } else {
            songs.add(position, song);
        }
        return true;
    }

    public boolean removeSong(Song song) {
        if (song == null) {
            System.out.println("Cannot remove a null song from playlist.");
            return false;
        }
        return songs.remove(song);
    }

    public Song getSongAt(int index) {
        if (index < 0 || index >= songs.size()) return null;
        return songs.get(index);
    }

    public ArrayList<Song> getSongsCopy() { return new ArrayList<>(songs); }
    public int getSongListSize() { return songs.size(); }
    public boolean isEmpty() { return songs.isEmpty(); }

    @Override
    public void displayInfo() {
        System.out.println("\n========== Playlist Detail ==========");
        System.out.println("Playlist ID: " + playlistId);
        System.out.println("Name: " + name);
        System.out.println("Total Songs: " + songs.size());
        if (songs.isEmpty()) {
            System.out.println("No songs in this playlist.");
        } else {
            for (int i = 0; i < songs.size(); i++) {
                System.out.print((i + 1) + ". ");
                songs.get(i).displayInfo();
            }
        }
        System.out.println("=====================================");
    }

    public static int getPlaylistCount() { return playlistCount; }
}
```

---

## Package: `db` — Data Access

### `DatabaseManager.java`

Manages the in-memory database. Implements `Loadable` and provides pre-loaded sample data with bidirectional relationships between songs, artists, and albums.

```java
package db;

import java.util.ArrayList;
import java.util.HashMap;

import interfaces.Loadable;
import model.Album;
import model.Artist;
import model.Song;

public class DatabaseManager implements Loadable {
    private static final String DATABASE_NAME = "MusicDB";

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

    private Artist getOrCreateArtist(HashMap<Integer, Artist> artistMap, int id, String name, String country) {
        if (artistMap.containsKey(id)) {
            return artistMap.get(id);
        }
        Artist artist = new Artist(id, name, country);
        artistMap.put(id, artist);
        return artist;
    }

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

    private void connectSong(Song song) {
        if (song == null) return;
        Artist artist = song.getArtist();
        Album album = song.getAlbum();
        if (artist != null) artist.addSong(song);
        if (album != null) album.addSong(song);
    }

    public static String getDatabaseName() { return DATABASE_NAME; }
}
```

---

## Package: `service` — Core Logic

### `MusicPlayer.java`

The central service class implementing playback, queue management, and search functionality.

```java
package service;

import java.util.ArrayList;
import java.util.Collections;

import interfaces.Displayable;
import interfaces.Playable;
import interfaces.QueueManageable;
import interfaces.Searchable;
import model.Album;
import model.Artist;
import model.Playlist;
import model.Song;

public class MusicPlayer implements Displayable, Playable, QueueManageable, Searchable {
    private String playerName;
    private Song currentSong;
    private boolean playing;
    private int volume;
    private ArrayList<Song> songLibrary;
    private ArrayList<Song> playbackQueue;
    private ArrayList<Song> playbackHistory;

    public MusicPlayer(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            this.playerName = "Music Player";
        } else {
            this.playerName = playerName.trim();
        }
        this.currentSong = null;
        this.playing = false;
        this.volume = 50;
        this.songLibrary = new ArrayList<>();
        this.playbackQueue = new ArrayList<>();
        this.playbackHistory = new ArrayList<>();
    }

    // --- Getters ---
    public String getPlayerName() { return playerName; }
    public Song getCurrentSong() { return currentSong; }
    public boolean isPlaying() { return playing; }
    public int getVolume() { return volume; }

    public void setVolume(int volume) {
        if (volume < 0) this.volume = 0;
        else if (volume > 100) this.volume = 100;
        else this.volume = volume;
    }

    // --- Library Management ---
    public void loadLibrary(ArrayList<Song> songs) {
        songLibrary.clear();
        if (songs == null) return;
        for (Song song : songs) {
            if (song != null && !songLibrary.contains(song)) {
                songLibrary.add(song);
            }
        }
    }

    public ArrayList<Song> getSongLibraryCopy() { return new ArrayList<>(songLibrary); }
    public ArrayList<Song> getPlaybackQueueCopy() { return new ArrayList<>(playbackQueue); }
    public ArrayList<Song> getPlaybackHistoryCopy() { return new ArrayList<>(playbackHistory); }
    public int getLibrarySize() { return songLibrary.size(); }
    public int getQueueSize() { return playbackQueue.size(); }
    public int getHistorySize() { return playbackHistory.size(); }

    public boolean setCurrentSong(Song song) {
        if (song == null) {
            System.out.println("Cannot set current song to null.");
            return false;
        }
        if (currentSong != null) {
            playbackHistory.add(currentSong);
        }
        currentSong = song;
        return true;
    }

    // --- Playback (Playable) ---
    @Override
    public boolean play() {
        if (currentSong == null) {
            if (playbackQueue.isEmpty()) {
                System.out.println("No song to play. The queue is empty.");
                return false;
            }
            currentSong = playbackQueue.remove(0);
        }
        playing = true;
        System.out.println("Now playing: " + currentSong.getTitle());
        return true;
    }

    @Override
    public boolean pause() {
        if (currentSong == null) {
            System.out.println("No song is currently selected.");
            return false;
        }
        if (!playing) {
            System.out.println("The player is already paused.");
            return false;
        }
        playing = false;
        System.out.println("Paused: " + currentSong.getTitle());
        return true;
    }

    @Override
    public boolean skipToNext() {
        if (playbackQueue.isEmpty()) {
            System.out.println("No next song in the queue.");
            return false;
        }
        if (currentSong != null) {
            playbackHistory.add(currentSong);
        }
        currentSong = playbackQueue.remove(0);
        playing = true;
        System.out.println("Skipped to next song: " + currentSong.getTitle());
        return true;
    }

    @Override
    public boolean skipToPrevious() {
        if (playbackHistory.isEmpty()) {
            System.out.println("No previous song in history.");
            return false;
        }
        if (currentSong != null) {
            playbackQueue.add(0, currentSong);
        }
        currentSong = playbackHistory.remove(playbackHistory.size() - 1);
        playing = true;
        System.out.println("Returned to previous song: " + currentSong.getTitle());
        return true;
    }

    // --- Queue Management (QueueManageable) ---
    @Override
    public boolean addToQueue(Song song) {
        if (song == null) {
            System.out.println("Cannot add a null song to the queue.");
            return false;
        }
        playbackQueue.add(song);
        return true;
    }

    @Override
    public boolean addPlaylistToQueue(Playlist playlist) {
        if (playlist == null) {
            System.out.println("Cannot add a null playlist to the queue.");
            return false;
        }
        ArrayList<Song> songs = playlist.getSongsCopy();
        if (songs.isEmpty()) {
            System.out.println("Playlist is empty: " + playlist.getName());
            return false;
        }
        for (Song song : songs) {
            addToQueue(song);
        }
        return true;
    }

    @Override
    public boolean clearQueue() {
        playbackQueue.clear();
        return true;
    }

    @Override
    public boolean shuffleQueue() {
        if (playbackQueue.isEmpty()) {
            System.out.println("Cannot shuffle an empty queue.");
            return false;
        }
        Collections.shuffle(playbackQueue);
        System.out.println("Queue shuffled.");
        return true;
    }

    // --- Search (Searchable + overloads) ---
    public ArrayList<Song> searchSong(String title) {
        ArrayList<Song> result = new ArrayList<>();
        if (title == null || title.trim().isEmpty()) return result;
        for (Song song : songLibrary) {
            if (song.getTitle().toLowerCase().contains(title.trim().toLowerCase())) {
                result.add(song);
            }
        }
        return result;
    }

    public Song searchSong(String title, String artistName) {
        if (title == null || title.trim().isEmpty()) return null;
        for (Song song : songLibrary) {
            boolean matchesTitle = song.getTitle().toLowerCase().contains(title.trim().toLowerCase());
            boolean matchesArtist = (artistName == null || artistName.trim().isEmpty())
                || (song.getArtist() != null && song.getArtist().getName().equalsIgnoreCase(artistName.trim()));
            if (matchesTitle && matchesArtist) {
                return song;
            }
        }
        return null;
    }

    public Song searchSong(String title, String artistName, String genre) {
        if (title == null || title.trim().isEmpty()) return null;
        for (Song song : songLibrary) {
            boolean matchesTitle = song.getTitle().toLowerCase().contains(title.trim().toLowerCase());
            boolean matchesArtist = (artistName == null || artistName.trim().isEmpty())
                || (song.getArtist() != null && song.getArtist().getName().equalsIgnoreCase(artistName.trim()));
            boolean matchesGenre = (genre == null || genre.trim().isEmpty())
                || song.getGenre().equalsIgnoreCase(genre.trim());
            if (matchesTitle && matchesArtist && matchesGenre) {
                return song;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Song> searchSongsByArtist(String artistName) {
        ArrayList<Song> result = new ArrayList<>();
        if (artistName == null || artistName.trim().isEmpty()) return result;
        for (Song song : songLibrary) {
            Artist artist = song.getArtist();
            if (artist != null && artist.getName().equalsIgnoreCase(artistName.trim())) {
                result.add(song);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Song> searchSongsByAlbum(String albumTitle) {
        ArrayList<Song> result = new ArrayList<>();
        if (albumTitle == null || albumTitle.trim().isEmpty()) return result;
        for (Song song : songLibrary) {
            Album album = song.getAlbum();
            if (album != null && album.getTitle().equalsIgnoreCase(albumTitle.trim())) {
                result.add(song);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Song> filterSongsByGenre(String genre) {
        ArrayList<Song> result = new ArrayList<>();
        if (genre == null || genre.trim().isEmpty()) return result;
        for (Song song : songLibrary) {
            if (song.getGenre().equalsIgnoreCase(genre.trim())) {
                result.add(song);
            }
        }
        return result;
    }

    // --- Display ---
    public void displayQueue() {
        System.out.println("\n========== Playback Queue ==========");
        if (playbackQueue.isEmpty()) {
            System.out.println("Queue is empty.");
        } else {
            for (int i = 0; i < playbackQueue.size(); i++) {
                System.out.print((i + 1) + ". ");
                playbackQueue.get(i).displayInfo();
            }
        }
        System.out.println("====================================");
    }

    public void displayHistory() {
        System.out.println("\n========== Playback History ==========");
        if (playbackHistory.isEmpty()) {
            System.out.println("No playback history yet.");
        } else {
            for (Song song : playbackHistory) {
                song.displayInfo();
            }
        }
        System.out.println("======================================");
    }

    @Override
    public void displayInfo() {
        System.out.println("\n========== Music Player Summary ==========");
        System.out.println("Player Name: " + playerName);
        System.out.println("Playing: " + playing);
        System.out.println("Volume: " + volume);
        System.out.println("Library Songs: " + songLibrary.size());
        System.out.println("Queue Songs: " + playbackQueue.size());
        System.out.println("History Songs: " + playbackHistory.size());
        if (currentSong != null) {
            System.out.println("Current Song: " + currentSong.getTitle());
        } else {
            System.out.println("Current Song: None");
        }
        System.out.println("==========================================");
    }
}
```

---

## Package: `main` — Entry Point

### `Main.java`

Application entry point. Loads sample data, provides an interactive console menu, and demonstrates polymorphism through `MediaItem`.

```java
package main;

import java.util.ArrayList;
import java.util.Scanner;

import db.DatabaseManager;
import model.Artist;
import model.MediaItem;
import model.Playlist;
import model.Song;
import service.MusicPlayer;

public class Main {

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
        ArrayList<Song> songLibrary = databaseManager.loadSongs();

        MusicPlayer player = new MusicPlayer("CAM Music Player");
        player.loadLibrary(songLibrary);

        // Polymorphism demonstration: Song objects stored as MediaItem references
        ArrayList<MediaItem> library = new ArrayList<>();
        for (Song s : songLibrary) {
            library.add(s);
        }
        for (MediaItem item : library) {
            item.displayInfo();
        }

        ArrayList<Playlist> playlists = new ArrayList<>();
        playlists.add(new Playlist(1, "My Playlist"));
        int currentPlaylistIndex = 0;

        System.out.println("Welcome to CAM Music Player (" + songLibrary.size() + " songs loaded)");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            Playlist currentPlaylist = playlists.get(currentPlaylistIndex);

            System.out.println("\n===== MENU =====");
            System.out.println("--- Library ---");
            System.out.println("1.  List all songs");
            System.out.println("2.  Search songs");
            System.out.println("--- Playlist (" + currentPlaylist.getName() + ") ---");
            System.out.println("3.  Create new playlist");
            System.out.println("4.  Switch playlist");
            System.out.println("5.  Add song to playlist");
            System.out.println("6.  Remove song from playlist");
            System.out.println("7.  Show playlist");
            System.out.println("8.  Play song from playlist");
            System.out.println("--- Playback ---");
            System.out.println("9.  Pause");
            System.out.println("10. Next");
            System.out.println("11. Previous");
            System.out.println("--- Queue ---");
            System.out.println("12. Show queue");
            System.out.println("13. Shuffle queue");
            System.out.println("14. Clear queue");
            System.out.println("15. Add playlist to queue");
            System.out.println("--- History ---");
            System.out.println("16. Show history");
            System.out.println("0.  Exit");
            System.out.print("Choose: ");

            String input = scanner.nextLine();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                case 1:
                    System.out.println("\nAll Songs:");
                    for (Song song : songLibrary) {
                        song.displayInfo();
                    }
                    break;

                case 2:
                    handleSearchMenu(player, scanner);
                    break;

                case 3:
                    System.out.print("Enter playlist name: ");
                    String plName = scanner.nextLine();
                    int newId = playlists.size() + 1;
                    playlists.add(new Playlist(newId, plName));
                    System.out.println("Playlist '" + plName + "' created.");
                    break;

                case 4:
                    System.out.println("  Playlists:");
                    for (int i = 0; i < playlists.size(); i++) {
                        String mark = (i == currentPlaylistIndex) ? " <-- current" : "";
                        System.out.println("    " + (i + 1) + ". " + playlists.get(i).getName() + mark);
                    }
                    System.out.print("  Switch to (1-" + playlists.size() + "): ");
                    try {
                        int switchTo = Integer.parseInt(scanner.nextLine()) - 1;
                        if (switchTo >= 0 && switchTo < playlists.size()) {
                            currentPlaylistIndex = switchTo;
                            System.out.println("Switched to '" + playlists.get(currentPlaylistIndex).getName() + "'.");
                        } else {
                            System.out.println("Invalid number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                    break;

                case 5:
                    System.out.print("Enter song index (1-" + songLibrary.size() + "): ");
                    int idx;
                    try {
                        idx = Integer.parseInt(scanner.nextLine()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                        break;
                    }
                    if (idx < 0 || idx >= songLibrary.size()) {
                        System.out.println("Invalid index.");
                        break;
                    }
                    System.out.print("Enter position (0 for end): ");
                    int pos;
                    try {
                        pos = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        pos = currentPlaylist.getSongListSize();
                    }
                    if (pos <= 0) {
                        currentPlaylist.addSong(songLibrary.get(idx));
                    } else {
                        currentPlaylist.addSong(songLibrary.get(idx), pos - 1);
                    }
                    break;

                case 6:
                    System.out.print("Enter song index (1-" + currentPlaylist.getSongListSize() + "): ");
                    int removeIdx;
                    try {
                        removeIdx = Integer.parseInt(scanner.nextLine()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                        break;
                    }
                    Song toRemove = currentPlaylist.getSongAt(removeIdx);
                    if (toRemove == null) {
                        System.out.println("Invalid index.");
                    } else {
                        currentPlaylist.removeSong(toRemove);
                        System.out.println("Removed.");
                    }
                    break;

                case 7:
                    currentPlaylist.displayInfo();
                    break;

                case 8:
                    System.out.print("Enter song index (1-" + currentPlaylist.getSongListSize() + "): ");
                    try {
                        int songIdx = Integer.parseInt(scanner.nextLine()) - 1;
                        Song selectedSong = currentPlaylist.getSongAt(songIdx);
                        if (selectedSong != null) {
                            player.setCurrentSong(selectedSong);
                            player.play();
                        } else {
                            System.out.println("Invalid index.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                    break;

                case 9:
                    player.pause();
                    break;

                case 10:
                    player.skipToNext();
                    break;

                case 11:
                    player.skipToPrevious();
                    break;

                case 12:
                    player.displayQueue();
                    break;

                case 13:
                    player.shuffleQueue();
                    break;

                case 14:
                    player.clearQueue();
                    break;

                case 15:
                    player.addPlaylistToQueue(currentPlaylist);
                    break;

                case 16:
                    player.displayHistory();
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleSearchMenu(MusicPlayer player, Scanner scanner) {
        while (true) {
            System.out.println("  Search by:");
            System.out.println("    a. Title");
            System.out.println("    b. Artist");
            System.out.println("    c. Album");
            System.out.println("    d. Genre");
            System.out.println("    e. Title + Artist");
            System.out.println("    f. Title + Artist + Genre");
            System.out.println("    x. Back to menu");
            System.out.print("  Choose (a-f, x): ");
            String searchType = scanner.nextLine().trim().toLowerCase();

            if (searchType.equals("x")) break;

            ArrayList<Song> results = null;

            switch (searchType) {
                case "a":
                    System.out.print("Enter title: ");
                    String st = scanner.nextLine();
                    results = player.searchSong(st);
                    break;
                case "b":
                    System.out.print("Enter artist: ");
                    String sa = scanner.nextLine();
                    results = player.searchSongsByArtist(sa);
                    if (results != null && !results.isEmpty()) {
                        Artist artist = results.get(0).getArtist();
                        System.out.println("\nArtist: " + artist.getName() + " (" + artist.getAlbumListSize() + " album(s))");
                    }
                    break;
                case "c":
                    System.out.print("Enter album: ");
                    String sal = scanner.nextLine();
                    results = player.searchSongsByAlbum(sal);
                    break;
                case "d":
                    System.out.print("Enter genre: ");
                    String sg = scanner.nextLine();
                    results = player.filterSongsByGenre(sg);
                    break;
                case "e":
                    System.out.print("Enter title: ");
                    String t2 = scanner.nextLine();
                    System.out.print("Enter artist: ");
                    String a2 = scanner.nextLine();
                    Song result2 = player.searchSong(t2, a2);
                    if (result2 == null) System.out.println("No song found.");
                    else result2.displayInfo();
                    break;
                case "f":
                    System.out.print("Enter title: ");
                    String t3 = scanner.nextLine();
                    System.out.print("Enter artist: ");
                    String a3 = scanner.nextLine();
                    System.out.print("Enter genre: ");
                    String g3 = scanner.nextLine();
                    Song result3 = player.searchSong(t3, a3, g3);
                    if (result3 == null) System.out.println("No song found.");
                    else result3.displayInfo();
                    break;
                default:
                    System.out.println("Invalid input.");
                    continue;
            }

            if (results != null) {
                if (results.isEmpty()) {
                    System.out.println("No results found.");
                } else {
                    for (Song s : results) s.displayInfo();
                }
            }
        }
    }
}
```

---

## Configuration & Build Script

### `src/.vscode/settings.json`

```json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar"
    ]
}
```

### `.vscode/settings.json` (root)

```json
{
    "java.debug.settings.onBuildFailureProceed": true,
    "java.project.referencedLibraries": [
        "lib/**/*.jar",
        "c:\\Users\\Seng menghong\\Downloads\\mysql-connector-j-9.6.0\\mysql-connector-j-9.6.0\\mysql-connector-j-9.6.0.jar"
    ]
}
```

### `compile_and_run.bat`

```batch
@echo off
echo Compiling Java source files (packages: interfaces, model, db, service, main)...
javac interfaces/*.java model/*.java db/*.java service/*.java main/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Executing...
java main.Main
pause
```

---

## Class Hierarchy & Relationships

```
Displayable (interface)
├── MediaItem (abstract, implements Displayable)
│   ├── Song
│   └── Album
├── Artist (implements Displayable)
├── Playlist (implements Displayable)
└── MusicPlayer (implements Displayable, Playable, QueueManageable, Searchable)

Loadable (interface)
└── DatabaseManager (implements Loadable)

Playable (interface) ─── MusicPlayer
QueueManageable (interface) ─── MusicPlayer
Searchable (interface) ─── MusicPlayer
```

---

## Sample Data

| Song ID | Title       | Artist       | Album            | Genre    | Duration |
|---------|-------------|--------------|------------------|----------|----------|
| 1       | Sunrise     | Dara Music   | Morning Playlist | Pop      | 3:30     |
| 2       | Coffee Time | Dara Music   | Morning Playlist | Acoustic | 3:05     |
| 3       | City Lights | Sokha Band   | Night Drive      | Rock     | 4:00     |
| 4       | Rainy Night | Sokha Band   | Night Drive      | Jazz     | 3:20     |

---

## Menu Features

| #  | Feature                   | Description                              |
|----|---------------------------|------------------------------------------|
| 1  | List all songs            | Shows all songs in the library           |
| 2  | Search songs              | Search by title, artist, album, genre    |
| 3  | Create new playlist       | Creates a named playlist                 |
| 4  | Switch playlist           | Switch between existing playlists        |
| 5  | Add song to playlist      | Add a song at a specific position        |
| 6  | Remove song from playlist | Remove a song by index                   |
| 7  | Show playlist             | Display current playlist details         |
| 8  | Play song from playlist   | Set and play a song from playlist        |
| 9  | Pause                     | Pause current song                       |
| 10 | Next                      | Skip to next song in queue               |
| 11 | Previous                  | Go back to previous song                 |
| 12 | Show queue                | Display playback queue                   |
| 13 | Shuffle queue             | Randomly shuffle the queue               |
| 14 | Clear queue               | Remove all songs from queue              |
| 15 | Add playlist to queue     | Enqueue all songs from current playlist  |
| 16 | Show history              | Display playback history                 |
| 0  | Exit                      | Quit the application                     |

---

## Discrepancies Found vs Existing Docs

| File | Issue | Impact |
|------|-------|--------|
| `MediaItem.java` | Actual is `abstract class` with `abstract void displayInfo()`; docs show concrete class | **Compilation will fail** — subclasses cannot override a non-abstract method as abstract |
| `Song.java` | `displayInfo()` prints ID/title directly; docs show `super.displayInfo()` | **Compilation will fail** — cannot call `super.displayInfo()` on abstract parent |
| `Album.java` | Same as Song: prints directly; docs show `super.displayInfo()` | **Compilation will fail** |
| `Main.java` | Actual has polymorphism demo block (lines 22-29) + `import model.MediaItem`; docs omit this | **Compilation error** — docs miss critical code and import |
| `Loadable.java` | Actual uses `java.util.List` and `List<Song>`; docs show `ArrayList` | Still compiles (ArrayList implements List), but docs are inaccurate |
| `DatabaseManager.java` | Actual returns `ArrayList<Song>` (covariant return) | Still compiles, matches actual Loadable interface |
