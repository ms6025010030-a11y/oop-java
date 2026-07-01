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
        if (id > 0) {
            return id;
        }
        return 0;
    }

    private String cleanText(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    // Returns the unique identifier of this playlist.

    public int getPlaylistId() {
        return playlistId;
    }

    // Returns the name of this playlist.

    public String getName() {
        return name;
    }

    // Sets the name of this playlist. Silently ignores null or blank values.

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    // Adds a song to this playlist if it is not null and not already present.

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



    // Adds a song at a specific position in the playlist.

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

    // Removes a song from this playlist.

    public boolean removeSong(Song song) {
        if (song == null) {
            System.out.println("Cannot remove a null song from playlist.");
            return false;
        }

        return songs.remove(song);
    }

    // Returns the song at the specified index, or null if the index is out of bounds.

    public Song getSongAt(int index) {
        if (index < 0 || index >= songs.size()) {
            return null;
        }

        return songs.get(index);
    }

    // Returns a defensive copy of the playlist's song list. External callers
    // may modify the returned list without affecting the playlist's internal data.

    public ArrayList<Song> getSongsCopy() {
        return new ArrayList<>(songs);
    }

    // Returns the number of songs in this playlist.

    public int getSongListSize() {
        return songs.size();
    }

    // Checks whether this playlist has no songs.

    public boolean isEmpty() {
        return songs.isEmpty();
    }

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



    public static int getPlaylistCount() {
        return playlistCount;
    }
}
