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

    // Returns the unique identifier of this artist.

    public int getArtistId() {
        return artistId;
    }

    // Returns the name of this artist.

    public String getName() {
        return name;
    }

    // Returns the country of origin of this artist.

    public String getCountry() {
        return country;
    }

    // Sets the name of this artist. Silently ignores null or blank values.

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    // Sets the country of this artist. Silently ignores null or blank values.

    public void setCountry(String country) {
        if (country != null && !country.trim().isEmpty()) {
            this.country = country.trim();
        }
    }

    // Adds a song to this artist's song list if it is not null and not already
    // present.

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

    // Adds an album to this artist's album list if it is not null and not already
    // present.

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

    // Returns a defensive copy of the artist's song list. External callers
    // may modify the returned list without affecting the artist's internal data.

    public ArrayList<Song> getSongsCopy() {
        return new ArrayList<>(songs);
    }

    // Returns a defensive copy of the artist's album list. External callers
    // may modify the returned list without affecting the artist's internal data.

    public ArrayList<Album> getAlbumsCopy() {
        return new ArrayList<>(albums);
    }

    // Returns the number of songs associated with this artist.

    public int getSongListSize() {
        return songs.size();
    }

    // Returns the number of albums associated with this artist.

    public int getAlbumListSize() {
        return albums.size();
    }

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

    // Prints all songs by this artist to the console.

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

    // Prints all albums by this artist to the console.

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

    // Returns the total number of Artist instances created.

    public static int getArtistCount() {
        return artistCount;
    }
}
