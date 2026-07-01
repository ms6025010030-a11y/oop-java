# Music Project — Complete Method Documentation

## Package: `interfaces`

### `Displayable.java`
| Method | What it is used for |
|--------|-------------------|
| `void displayInfo()` | Prints detailed info about the object to the console |

### `Loadable.java`
| Method | What it is used for |
|--------|-------------------|
| `List<Song> loadSongs()` | Loads all songs from the data source (returns a list) |

### `Playable.java`
| Method | What it is used for |
|--------|-------------------|
| `boolean play()` | Starts or resumes playback of the current song |
| `boolean pause()` | Pauses the currently playing song |
| `boolean skipToNext()` | Skips to the next song in the playback queue |
| `boolean skipToPrevious()` | Returns to the most recent song in playback history |

### `QueueManageable.java`
| Method | What it is used for |
|--------|-------------------|
| `boolean addToQueue(Song song)` | Adds a single song to the end of the playback queue |
| `boolean addPlaylistToQueue(Playlist playlist)` | Adds all songs from a playlist to the playback queue |
| `boolean clearQueue()` | Removes all songs from the playback queue |
| `boolean shuffleQueue()` | Randomly shuffles the order of songs in the queue |

### `Searchable.java`
| Method | What it is used for |
|--------|-------------------|
| `ArrayList<Song> searchSongsByArtist(String artistName)` | Finds all songs by a given artist name (case-insensitive) |
| `ArrayList<Song> searchSongsByAlbum(String albumTitle)` | Finds all songs from a given album title (case-insensitive) |
| `ArrayList<Song> filterSongsByGenre(String genre)` | Filters songs by genre (case-insensitive) |

### `SongFilter.java` (FunctionalInterface)
| Method | What it is used for |
|--------|-------------------|
| `boolean test(Song song)` | Lambda functional method — tests whether a song satisfies a condition |

---

## Package: `model`

### `MediaItem.java` — Abstract base class for all media items
| Method | What it is used for |
|--------|-------------------|
| `MediaItem(int id, String title)` | Constructor — initializes id and title |
| `static int validateId(int id)` | Validates that an ID is positive; returns 0 if invalid |
| `static String cleanText(String value, String defaultValue)` | Cleans text (trims, handles null/empty) with a fallback default |
| `int getId()` | Returns the unique identifier of this media item |
| `String getTitle()` | Returns the title of this media item |
| `void setTitle(String title)` | Sets the title (ignores null/blank values silently) |
| `abstract void displayInfo()` | Abstract — subclasses implement their own detail display |

### `Song.java` — Represents a single song
| Method | What it is used for |
|--------|-------------------|
| `Song(int songId, String title, Artist artist, Album album, String genre, int durationInSeconds)` | Constructor — creates a song with its artist, album, genre, and duration |
| `Artist getArtist()` | Returns the artist of this song |
| `Album getAlbum()` | Returns the album this song belongs to |
| `String getGenre()` | Returns the genre of this song |
| `int getDurationInSeconds()` | Returns the duration in seconds |
| `void setArtist(Artist artist)` | Sets/changes the artist of this song |
| `void setAlbum(Album album)` | Sets/changes the album of this song |
| `void setGenre(String genre)` | Sets the genre (ignores null/blank silently) |
| `void setDurationInSeconds(int duration)` | Sets duration; defaults to 0 and prints error if not positive |
| `String getFormattedDuration()` | Returns duration as `mm:ss` format (e.g. "3:45") |
| `void displayInfo()` | Override — prints full song details (ID, title, artist, album, genre, duration) |
| `static int getSongCount()` | Returns the total number of Song instances ever created |

### `Album.java` — Represents a music album
| Method | What it is used for |
|--------|-------------------|
| `Album(int albumId, String title, Artist artist, int releaseYear)` | Constructor — creates an album with its artist and release year |
| `Artist getArtist()` | Returns the artist who created this album |
| `int getReleaseYear()` | Returns the release year of this album |
| `void setArtist(Artist artist)` | Sets/changes the artist for this album |
| `void setReleaseYear(int releaseYear)` | Sets release year; defaults to 0 with error if not positive |
| `boolean addSong(Song song)` | Adds a song to the album if not null and not already present |
| `ArrayList<Song> getSongsCopy()` | Returns a defensive copy of the album's song list |
| `int getSongListSize()` | Returns the number of songs in the album |
| `void displayInfo()` | Override — prints album details (ID, title, artist, release year, song count) |
| `void displaySongs()` | Prints all songs in this album with their details |
| `static int getAlbumCount()` | Returns the total number of Album instances ever created |

### `Artist.java` — Represents a musical artist
| Method | What it is used for |
|--------|-------------------|
| `Artist(int artistId, String name, String country)` | Constructor — creates an artist with ID, name, and country |
| `private int validateId(int id)` | Private — validates that ID is positive (returns 0 if not) |
| `private String cleanText(String value, String defaultValue)` | Private — trims text or returns default if null/empty |
| `int getArtistId()` | Returns the unique identifier of this artist |
| `String getName()` | Returns the name of this artist |
| `String getCountry()` | Returns the country of origin |
| `void setName(String name)` | Sets the name (ignores null/blank silently) |
| `void setCountry(String country)` | Sets the country (ignores null/blank silently) |
| `boolean addSong(Song song)` | Adds a song to this artist's list if not null and not duplicate |
| `boolean addAlbum(Album album)` | Adds an album to this artist's list if not null and not duplicate |
| `ArrayList<Song> getSongsCopy()` | Returns a defensive copy of the artist's song list |
| `ArrayList<Album> getAlbumsCopy()` | Returns a defensive copy of the artist's album list |
| `int getSongListSize()` | Returns the number of songs associated with this artist |
| `int getAlbumListSize()` | Returns the number of albums associated with this artist |
| `void displayInfo()` | Override — prints artist details (ID, name, country, song/album counts) |
| `void displaySongs()` | Prints all songs by this artist |
| `void displayAlbums()` | Prints all albums by this artist |
| `static int getArtistCount()` | Returns the total number of Artist instances ever created |

### `Playlist.java` — Represents a user-created playlist
| Method | What it is used for |
|--------|-------------------|
| `Playlist(int playlistId, String name)` | Constructor — creates a playlist with ID and name |
| `private int validateId(int id)` | Private — validates positive ID |
| `private String cleanText(String value, String defaultValue)` | Private — cleans text or returns default |
| `int getPlaylistId()` | Returns the unique identifier of this playlist |
| `String getName()` | Returns the name of this playlist |
| `void setName(String name)` | Sets the playlist name (ignores null/blank) |
| `boolean addSong(Song song)` | Adds a song to the end of the playlist if not duplicate |
| `boolean addSong(Song song, int position)` | Adds a song at a specific position; invalid position appends to end |
| `boolean removeSong(Song song)` | Removes a song from the playlist |
| `Song getSongAt(int index)` | Returns the song at a given index, or null if out of bounds |
| `ArrayList<Song> getSongsCopy()` | Returns a defensive copy of the playlist's song list |
| `int getSongListSize()` | Returns the number of songs in the playlist |
| `boolean isEmpty()` | Checks whether the playlist has no songs |
| `void displayInfo()` | Override — prints playlist details with numbered song list |
| `static int getPlaylistCount()` | Returns the total number of Playlist instances ever created |

---

## Package: `service`

### `MusicPlayer.java` — Main music player engine
| Method | What it is used for |
|--------|-------------------|
| `MusicPlayer(String playerName)` | Constructor — initializes player with a name, empty library/queue/history, volume 50 |
| `String getPlayerName()` | Returns the player's name |
| `Song getCurrentSong()` | Returns the currently selected song (or null) |
| `boolean isPlaying()` | Returns whether the player is currently playing |
| `int getVolume()` | Returns the current volume level (0-100) |
| `void setVolume(int volume)` | Sets volume; clamps to 0-100 range |
| `void loadLibrary(ArrayList<Song> songs)` | Loads songs into the library (replaces existing, skips null/duplicates) |
| `ArrayList<Song> getSongLibraryCopy()` | Returns a defensive copy of the song library |
| `ArrayList<Song> getPlaybackQueueCopy()` | Returns a defensive copy of the playback queue |
| `ArrayList<Song> getPlaybackHistoryCopy()` | Returns a defensive copy of playback history |
| `int getLibrarySize()` | Returns the number of songs in the library |
| `int getQueueSize()` | Returns the number of songs in the queue |
| `int getHistorySize()` | Returns the number of songs in history |
| `boolean setCurrentSong(Song song)` | Sets current song; moves previous current song to history |
| `boolean play()` | Starts/resumes playback; auto-plays from queue if no current song |
| `boolean pause()` | Pauses the currently playing song |
| `boolean skipToNext()` | Skips to next song in queue; moves current to history |
| `boolean skipToPrevious()` | Returns to last song in history; puts current at front of queue |
| `boolean addToQueue(Song song)` | Adds a single song to end of playback queue |
| `boolean addPlaylistToQueue(Playlist playlist)` | Adds all songs from a playlist to the queue |
| `boolean clearQueue()` | Removes all songs from the playback queue |
| `boolean shuffleQueue()` | Randomly shuffles the order of songs in the queue |
| `ArrayList<Song> searchSong(String title)` | Searches library by title (partial match, case-insensitive) |
| `Song searchSong(String title, String artistName)` | Searches by title + artist (returns single Song) |
| `Song searchSong(String title, String artistName, String genre)` | Searches by title + artist + genre (most specific) |
| `ArrayList<Song> searchSongsByArtist(String artistName)` | Finds all songs by a given artist name |
| `ArrayList<Song> searchSongsByAlbum(String albumTitle)` | Finds all songs from a given album title |
| `ArrayList<Song> filterSongsByGenre(String genre)` | Filters library by genre |
| `ArrayList<Song> filterSongs(SongFilter songFilter)` | Filters library using a lambda/functional interface condition |
| `void displayQueue()` | Prints the current playback queue to console |
| `void displayHistory()` | Prints the playback history to console |
| `void displayInfo()` | Override — prints player summary (name, playing status, volume, counts) |

---

## Package: `db`

### `DatabaseManager.java` — In-memory data source
| Method | What it is used for |
|--------|-------------------|
| `ArrayList<Song> loadSongs()` | Loads 4 sample songs with their artists/albums and returns the list |
| `private Artist getOrCreateArtist(HashMap, int, String, String)` | Reuses existing artist from map or creates a new one |
| `private Album getOrCreateAlbum(HashMap, int, String, Artist, int)` | Reuses existing album from map or creates a new one; registers with artist |
| `private void connectSong(Song song)` | Links a song bidirectionally to its artist and album (adds song to their lists) |
| `static String getDatabaseName()` | Returns the database name constant "MusicDB" |

---

## Package: `main`

### `Main.java` — Entry point and CLI menu
| Method | What it is used for |
|--------|-------------------|
| `static void main(String[] args)` | Entry point — loads data, creates player, runs the interactive menu loop with 16 options |
| `private static void handleSearchMenu(MusicPlayer, Scanner)` | Helper — runs a sub-menu for searching songs by title / artist / album / genre / combinations |

---

**Total: 14 files, 89 methods across 5 packages**
