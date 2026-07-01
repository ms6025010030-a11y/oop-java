package service;

import java.util.ArrayList;
import java.util.Collections;

import interfaces.Displayable;
import interfaces.Playable;
import interfaces.QueueManageable;
import interfaces.Searchable;
import interfaces.SongFilter;
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

    // Returns the name of this music player.

    public String getPlayerName() {
        return playerName;
    }

    // Returns the currently selected song, or null if none is set.

    public Song getCurrentSong() {
        return currentSong;
    }

    // Returns whether the player is currently playing a song.

    public boolean isPlaying() {
        return playing;
    }

    // Returns the current volume level (0-100).

    public int getVolume() {
        return volume;
    }

    // Sets the volume level. Values below 0 are clamped to 0,
    // values above 100 are clamped to 100.

    public void setVolume(int volume) {
        if (volume < 0) {
            this.volume = 0;
        } else if (volume > 100) {
            this.volume = 100;
        } else {
            this.volume = volume;
        }
    }

    // Loads songs into the player's library. Replaces any existing library content.
    // Null or duplicate songs in the provided list are silently skipped.

    public void loadLibrary(ArrayList<Song> songs) {
        songLibrary.clear();

        if (songs == null) {
            return;
        }

        for (Song song : songs) {
            if (song != null && !songLibrary.contains(song)) {
                songLibrary.add(song);
            }
        }
    }

    // Returns a defensive copy of the song library. External callers
    // may modify the returned list without affecting the player's internal data.

    public ArrayList<Song> getSongLibraryCopy() {
        return new ArrayList<>(songLibrary);
    }

    // Returns a defensive copy of the playback queue.

    public ArrayList<Song> getPlaybackQueueCopy() {
        return new ArrayList<>(playbackQueue);
    }

    // Returns a defensive copy of the playback history.

    public ArrayList<Song> getPlaybackHistoryCopy() {
        return new ArrayList<>(playbackHistory);
    }

    // Returns the number of songs in the library.

    public int getLibrarySize() {
        return songLibrary.size();
    }

    // Returns the number of songs in the playback queue.

    public int getQueueSize() {
        return playbackQueue.size();
    }

    // Returns the number of songs in the playback history.

    public int getHistorySize() {
        return playbackHistory.size();
    }

    // Sets the current song. The previously current song (if any)
    // is moved to the playback history.

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

    // Starts or resumes playback. If no current song is selected,
    // plays the next song from the queue.

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

    // Pauses the currently playing song.

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

    // Skips to the next song in the queue. The current song is moved to history.

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

    // Returns to the most recent song in playback history.
    // The current song is placed back at the front of the queue.

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

    // Adds a song to the end of the playback queue.

    @Override
    public boolean addToQueue(Song song) {
        if (song == null) {
            System.out.println("Cannot add a null song to the queue.");
            return false;
        }

        playbackQueue.add(song);
        return true;
    }

    // Adds all songs from a playlist to the end of the playback queue.

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

    // Removes all songs from the playback queue.

    @Override
    public boolean clearQueue() {
        playbackQueue.clear();
        return true;
    }

    // Randomly shuffles the order of songs in the playback queue.

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

    // Searches the library for a song whose title matches the given title
    // (case-insensitive).

    // Searches for songs by title only (partial match).
    // Returns a list since a title alone may match many songs.
    public ArrayList<Song> searchSong(String title) {
        ArrayList<Song> result = new ArrayList<>();

        if (title == null || title.trim().isEmpty()) {
            return result;
        }

        for (Song song : songLibrary) {
            if (song.getTitle().toLowerCase().contains(title.trim().toLowerCase())) {
                result.add(song);
            }
        }

        return result;
    }

    // Searches for a song by title and artist name.
    // Returns a single Song since title + artist is specific enough to expect one
    // match.
    public Song searchSong(String title, String artistName) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }

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

    // Searches for a song by title, artist name, and genre.
    // Returns a single Song since all three fields is the most specific match
    // possible.
    public Song searchSong(String title, String artistName, String genre) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }

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

    // Searches the library for all songs by a given artist name (case-insensitive).

    @Override
    public ArrayList<Song> searchSongsByArtist(String artistName) {
        ArrayList<Song> result = new ArrayList<>();

        if (artistName == null || artistName.trim().isEmpty()) {
            return result;
        }

        for (Song song : songLibrary) {
            Artist artist = song.getArtist();

            if (artist != null && artist.getName().equalsIgnoreCase(artistName.trim())) {
                result.add(song);
            }
        }

        return result;
    }

    // Searches the library for all songs from a given album title
    // (case-insensitive).

    @Override
    public ArrayList<Song> searchSongsByAlbum(String albumTitle) {
        ArrayList<Song> result = new ArrayList<>();

        if (albumTitle == null || albumTitle.trim().isEmpty()) {
            return result;
        }

        for (Song song : songLibrary) {
            Album album = song.getAlbum();

            if (album != null && album.getTitle().equalsIgnoreCase(albumTitle.trim())) {
                result.add(song);
            }
        }

        return result;
    }

    // Filters the library for all songs matching a given genre (case-insensitive).

    @Override
    public ArrayList<Song> filterSongsByGenre(String genre) {
        ArrayList<Song> result = new ArrayList<>();

        if (genre == null || genre.trim().isEmpty()) {
            return result;
        }

        for (Song song : songLibrary) {
            if (song.getGenre().equalsIgnoreCase(genre.trim())) {
                result.add(song);
            }
        }

        return result;
    }

    public ArrayList<Song> filterSongs(SongFilter songFilter) {
        ArrayList<Song> result = new ArrayList<>();
        for (Song song : songLibrary) {
            if (songFilter.test(song)) {
                result.add(song);
            }
        }
        return result;
    }
    // Prints the current playback queue to the console.

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

    // Prints the playback history to the console.

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
