package interfaces;

import model.Song;

@FunctionalInterface
public interface SongFilter {
    boolean test(Song song);
}