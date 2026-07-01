package model;

import java.util.ArrayList;
public class Album extends MediaItem  {
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



    // Returns the artist who created this album.

    public Artist getArtist() {
        return artist;
    }

    // Returns the release year of this album.

    public int getReleaseYear() {
        return releaseYear;
    }

    // Sets the artist for this album.

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    // Sets the release year of this album. If the provided year is not positive,
    // it defaults to 0 and prints an error message.

    public void setReleaseYear(int releaseYear) {
        if (releaseYear > 0) {
            this.releaseYear = releaseYear;
        } else {
            this.releaseYear = 0;   
            System.err.println( "Invalid release year provided for album '" + getTitle() + "'. Setting release year to 0.");

        }
    }

    // Adds a song to this album if it is not null and not already present.

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

    // Returns a defensive copy of the album's song list. External callers
    // may modify the returned list without affecting the album's internal data.

    public ArrayList<Song> getSongsCopy() {
        return new ArrayList<>(songs);
    }

    // Returns the number of songs in this album.

    public int getSongListSize() {
        return songs.size();
    }

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

    public static int getAlbumCount() {
        return albumCount;
    }
}
