package main;

import java.util.ArrayList;
import java.util.Scanner;

import db.DatabaseManager;
import model.Artist;
import model.MediaItem;
import model.Playlist;
import model.Song;
import service.MusicPlayer;
import interfaces.SongFilter;

public class Main {

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
        ArrayList<Song> songLibrary = databaseManager.loadSongs();

        MusicPlayer player = new MusicPlayer("CAM Music Player");
        player.loadLibrary(songLibrary);

        // polymorphism demostration
        ArrayList<MediaItem> library = new ArrayList<>();
        for (Song s : songLibrary) {
            library.add(s);
        }
        for (MediaItem item : library) {
            item.displayInfo();
        }

        SongFilter longSongFilter = (song) -> song.getDurationInSeconds() > 240;

        ArrayList<Song> longSongs = player.filterSongs(longSongFilter);
        System.out.println("\n--- Long songs (Lambda Filter) ---");
        for (Song s : longSongs) {
            s.displayInfo();
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
                    // Clean and simple! We call the method we created below.
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

    // Simple helper method to keep the main menu clean
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

            if (searchType.equals("x"))
                break;

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
                        System.out.println(
                                "\nArtist: " + artist.getName() + " (" + artist.getAlbumListSize() + " album(s))");
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
                    if (result2 == null)
                        System.out.println("No song found.");
                    else
                        result2.displayInfo();
                    break;
                case "f":
                    System.out.print("Enter title: ");
                    String t3 = scanner.nextLine();
                    System.out.print("Enter artist: ");
                    String a3 = scanner.nextLine();
                    System.out.print("Enter genre: ");
                    String g3 = scanner.nextLine();
                    Song result3 = player.searchSong(t3, a3, g3);
                    if (result3 == null)
                        System.out.println("No song found.");
                    else
                        result3.displayInfo();
                    break;
                default:
                    System.out.println("Invalid input.");
                    continue;
            }

            if (results != null) {
                if (results.isEmpty()) {
                    System.out.println("No results found.");
                } else {
                    for (Song s : results)
                        s.displayInfo();
                }
            }
        }
    }
}
