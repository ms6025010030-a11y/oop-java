# Java Visualizer — Runnable Code Snippets

All snippets are extracted from the CAM Music Player project.  
Each snippet is **self-contained** (includes imports and a `main` method) so you can paste it directly into a Java visualizer like [Java Visualizer](https://cscircles.cemc.uwaterloo.ca/java_visualize/).

---

## 1. Object Creation — Stack & Heap

```java
public class Visualizer1 {
    static class Artist {
        int id;
        String name;
        Artist(int id, String name) { this.id = id; this.name = name; }
    }
    static class Song {
        int id;
        String title;
        Artist artist;
        static int songCount = 0;
        Song(int id, String title, Artist artist) {
            this.id = id;
            this.title = title;
            this.artist = artist;
            songCount++;
        }
    }

    public static void main(String[] args) {
        Artist a = new Artist(1, "Dara");
        Song s1 = new Song(1, "Sunrise", a);
        Song s2 = new Song(2, "Coffee", a);
        System.out.println("Total songs: " + Song.songCount);
    }
}
```

**What it visualizes:**
- Stack frames with local references (`a`, `s1`, `s2`)
- Heap objects for `Artist` and two `Song` instances
- `static songCount` in the method area — **shared**, incremented by both constructors
- `s1.artist` and `s2.artist` both point to the **same** `Artist` object

---

## 2. Inheritance & Constructor Chaining

```java
public class Visualizer2 {
    static abstract class MediaItem {
        int id;
        String title;
        MediaItem(int id, String title) {
            this.id = id;
            this.title = title;
            System.out.println("MediaItem constructor done");
        }
        abstract void displayInfo();
    }

    static class Song extends MediaItem {
        String genre;
        Song(int id, String title, String genre) {
            super(id, title);               // ← calls MediaItem(id, title) first
            this.genre = genre;
            System.out.println("Song constructor done");
        }
        void displayInfo() {
            System.out.println(id + ": " + title + " (" + genre + ")");
        }
    }

    public static void main(String[] args) {
        Song s = new Song(1, "Sunrise", "Pop");
        s.displayInfo();
    }
}
```

**What it visualizes:**
- Constructor call chain: `Song()` pushes stack frame → calls `super()` → `MediaItem()` frame runs → returns → `Song()` frame resumes → `genre` assigned
- Stack unwinding: frames pop in reverse order
- Heap object has fields from **both** parent and child laid out in one object

---

## 3. Polymorphism — Dynamic Method Dispatch

```java
public class Visualizer3 {
    static abstract class MediaItem {
        abstract void displayInfo();
    }
    static class Song extends MediaItem {
        String title;
        Song(String t) { this.title = t; }
        void displayInfo() { System.out.println("Song: " + title); }
    }
    static class Album extends MediaItem {
        String name;
        Album(String n) { this.name = n; }
        void displayInfo() { System.out.println("Album: " + name); }
    }

    public static void main(String[] args) {
        MediaItem[] items = { new Song("Sunrise"), new Album("Morning") };
        for (MediaItem item : items) {
            item.displayInfo();   // ← which version runs?
        }
    }
}
```

**What it visualizes:**
- Array holds two references, both typed as `MediaItem`
- Heap objects are `Song` and `Album` respectively
- At runtime, JVM checks **actual class** → calls `Song.displayInfo()` for first, `Album.displayInfo()` for second
- Arrows from `item.displayInfo()` call site → correct override based on heap type

---

## 4. Reference Aliasing (No Copy)

```java
public class Visualizer4 {
    static class Song {
        String title;
        Song(String t) { this.title = t; }
    }

    public static void main(String[] args) {
        Song original = new Song("Sunrise");
        Song ref2 = original;        // ← no new object
        Song ref3 = original;        // ← same object, third reference

        System.out.println(original.title);  // "Sunrise"
        ref2.title = "Changed";
        System.out.println(original.title);  // "Changed" — same object!
        System.out.println(ref3.title);      // "Changed"

        System.out.println(original == ref2); // true (same reference)
    }
}
```

**What it visualizes:**
- One `Song` object on heap
- Three stack references (`original`, `ref2`, `ref3`) all pointing to it
- Changing via `ref2` is visible through all references — proves aliasing, not copying

---

## 5. Method Overloading — Compile-Time Binding

```java
public class Visualizer5 {
    static class Player {
        java.util.ArrayList<String> search(String title) {
            System.out.println("Called search(title)");
            java.util.ArrayList<String> r = new java.util.ArrayList<>();
            r.add(title); return r;
        }
        String search(String title, String artist) {
            System.out.println("Called search(title, artist)");
            return title + " by " + artist;
        }
    }

    public static void main(String[] args) {
        Player p = new Player();
        java.util.ArrayList<String> list = p.search("Sunrise");      // 1 arg
        String single = p.search("Sunrise", "Dara");                 // 2 args
        System.out.println(list);
        System.out.println(single);
    }
}
```

**What it visualizes:**
- Two `search` methods — same name, different parameter count
- Compiler resolves at **compile time** based on argument count
- Call to `search("Sunrise")` generates bytecode for the 1-param version
- Call to `search("Sunrise", "Dara")` generates bytecode for the 2-param version
- Different return types work because compiler already knows which overload

---

## 6. Guard Clause — Early Return

```java
public class Visualizer6 {
    static java.util.ArrayList<String> queue = new java.util.ArrayList<>();

    static boolean skipToNext() {
        if (queue.isEmpty()) {               // ← guard clause
            System.out.println("Queue empty. Returning false.");
            return false;                    // ← early exit
        }
        String next = queue.remove(0);
        System.out.println("Now playing: " + next);
        return true;                         // ← happy path
    }

    public static void main(String[] args) {
        System.out.println("--- Empty queue ---");
        skipToNext();                        // hits guard, returns early

        System.out.println("--- With songs ---");
        queue.add("Sunrise");
        skipToNext();                        // skips guard, plays song
    }
}
```

**What it visualizes:**
- First call: `queue.isEmpty()` is `true` → execution arrow goes into the `if` block → returns at `return false` — **rest of method never runs**
- Second call: `queue.isEmpty()` is `false` → arrow skips the `if` → goes to `remove(0)` → returns `true`
- Two separate execution traces showing branching

---

## 7. Defensive Copying

```java
public class Visualizer7 {
    static class Playlist {
        private java.util.ArrayList<String> songs = new java.util.ArrayList<>();

        void addSong(String s) { songs.add(s); }

        // Returns a COPY — caller cannot modify internal list
        java.util.ArrayList<String> getSongsCopy() {
            return new java.util.ArrayList<>(songs);
        }

        int size() { return songs.size(); }
    }

    public static void main(String[] args) {
        Playlist pl = new Playlist();
        pl.addSong("Sunrise");
        pl.addSong("Coffee");

        java.util.ArrayList<String> copy = pl.getSongsCopy();
        copy.clear();                         // clears the COPY only

        System.out.println("Copy size: " + copy.size());           // 0
        System.out.println("Original size: " + pl.size());         // 2 — unchanged!
    }
}
```

**What it visualizes:**
- `pl.songs` (internal) and `copy` are **two separate `ArrayList` objects** on the heap
- Each holds its own `Object[]` array
- `copy.clear()` modifies only the copy's array — the original's array is unaffected
- Demonstrates encapsulation: internal state is protected

---

## 8. Inverse Operations — Queue vs History

```java
public class Visualizer8 {
    static java.util.ArrayList<String> queue = new java.util.ArrayList<>();
    static java.util.ArrayList<String> history = new java.util.ArrayList<>();
    static String currentSong = null;

    static void skipToNext() {
        if (queue.isEmpty()) { System.out.println("No next"); return; }
        if (currentSong != null) history.add(currentSong);
        currentSong = queue.remove(0);        // take from FRONT of queue
        System.out.println("Now: " + currentSong);
    }

    static void skipToPrevious() {
        if (history.isEmpty()) { System.out.println("No previous"); return; }
        if (currentSong != null) queue.add(0, currentSong);  // put at FRONT
        currentSong = history.remove(history.size() - 1);    // take from END
        System.out.println("Now: " + currentSong);
    }

    public static void main(String[] args) {
        queue.add("A"); queue.add("B"); queue.add("C");

        currentSong = queue.remove(0);  // play A
        skipToNext();                    // A→history, B→current
        skipToPrevious();                // B→queue front, A→current
        System.out.println("Queue: " + queue);
        System.out.println("History: " + history);
    }
}
```

**What it visualizes:**
- Step-by-step state of **three** data structures: `queue`, `history`, `currentSong`
- `skipToNext()`: removes from queue head, appends old current to history tail
- `skipToPrevious()`: inserts old current at queue head, removes from history tail
- Two lists grow/shrink in **opposite** directions — trace both side by side

---

## 9. Static vs Instance Fields in Memory

```java
public class Visualizer9 {
    static class Song {
        String title;               // instance field — one per object
        static int songCount = 0;   // static field — shared by all

        Song(String t) {
            this.title = t;
            songCount++;            // increments the SAME counter
        }
    }

    public static void main(String[] args) {
        Song s1 = new Song("Sunrise");
        Song s2 = new Song("Coffee");
        Song s3 = new Song("Rain");

        System.out.println("s1.title = " + s1.title);
        System.out.println("s2.title = " + s2.title);
        System.out.println("s3.title = " + s3.title);
        System.out.println("Total songs: " + Song.songCount);   // 3
    }
}
```

**What it visualizes:**
- Three `Song` objects on heap, each with their own `title` — three separate boxes
- `songCount` in the **method area** — one box, accessible via `Song.songCount`
- Every `new Song(...)` increments the **same** `songCount`, not a per-object count
- Instance fields (`title`) can differ per object; static field (`songCount`) is universal

---

## 10. Lambda + Functional Interface

```java
public class Visualizer10 {
    @FunctionalInterface
    interface SongFilter {
        boolean test(Song song);
    }

    static class Song {
        String title; String genre;
        Song(String t, String g) { this.title = t; this.genre = g; }
        public String toString() { return title + " (" + genre + ")"; }
    }

    static java.util.ArrayList<Song> filter(java.util.ArrayList<Song> songs, SongFilter f) {
        java.util.ArrayList<Song> result = new java.util.ArrayList<>();
        for (Song s : songs) {
            if (f.test(s)) result.add(s);
        }
        return result;
    }

    public static void main(String[] args) {
        java.util.ArrayList<Song> library = new java.util.ArrayList<>();
        library.add(new Song("Sunrise", "Pop"));
        library.add(new Song("Jazz Night", "Jazz"));
        library.add(new Song("Pop Star", "Pop"));

        // Anonymous inner class
        SongFilter popFilterAnon = new SongFilter() {
            public boolean test(Song s) { return s.genre.equals("Pop"); }
        };

        // Lambda (equivalent)
        SongFilter popFilterLambda = s -> s.genre.equals("Pop");

        java.util.ArrayList<Song> popSongs = filter(library, popFilterAnon);
        System.out.println("Pop songs: " + popSongs);

        java.util.ArrayList<Song> jazzSongs = filter(library, s -> s.genre.equals("Jazz"));
        System.out.println("Jazz songs: " + jazzSongs);
    }
}
```

**What it visualizes:**
- `SongFilter` is a **functional interface** — exactly one abstract method
- The anonymous inner class creates an unnamed class on heap with `test()` method
- The lambda creates a lightweight functional object (no separate `.class` file)
- `filter()` loops, calling `f.test(s)` each iteration — arrow from call site to the lambda/inner class implementation
- Show that anonymous inner class and lambda produce **identical behavior**

---

## 11. Polymorphic Collection + instanceof Check

```java
public class Visualizer11 {
    static abstract class MediaItem {
        abstract void displayInfo();
    }
    static class Song extends MediaItem {
        String title;
        Song(String t) { this.title = t; }
        void displayInfo() { System.out.println("Song: " + title); }
        void sing() { System.out.println("♪ " + title + " ♪"); }
    }
    static class Album extends MediaItem {
        String name;
        Album(String n) { this.name = n; }
        void displayInfo() { System.out.println("Album: " + name); }
    }

    public static void main(String[] args) {
        MediaItem[] items = { new Song("Sunrise"), new Album("Morning"), new Song("Coffee") };

        for (MediaItem item : items) {
            item.displayInfo();                // polymorphic call
            if (item instanceof Song) {
                ((Song) item).sing();          // cast to access Song-only method
            }
        }
    }
}
```

**What it visualizes:**
- Array holds references to `Song` and `Album` objects — all typed as `MediaItem`
- `displayInfo()` dispatched dynamically (vtable lookup per iteration)
- `instanceof` check: first and third items pass → cast succeeds → `sing()` callable
- Second item fails `instanceof Song` → cast never happens → no `ClassCastException`
- Shows how not all methods are reachable through the parent type without casting

---

## 12. Bidirectional Relationships (Object Graph)

```java
public class Visualizer12 {
    static class Artist {
        String name;
        java.util.ArrayList<Song> songs = new java.util.ArrayList<>();
        Artist(String n) { this.name = n; }
        void addSong(Song s) { songs.add(s); }
    }
    static class Song {
        String title;
        Artist artist;
        Song(String t, Artist a) {
            this.title = t;
            this.artist = a;
            a.addSong(this);          // bidirectional link
        }
    }

    public static void main(String[] args) {
        Artist a = new Artist("Dara");
        Song s1 = new Song("Sunrise", a);
        Song s2 = new Song("Coffee", a);

        System.out.println("From song to artist: " + s1.artist.name);
        System.out.println("From artist to songs: " + a.songs.size());  // 2
    }
}
```

**What it visualizes:**
- Object graph on heap: `Artist` has an `ArrayList` containing references to both `Song` objects
- Each `Song` has a back-reference to the `Artist`
- Bidirectional arrows: `a.songs[0]` → `s1` and back via `s1.artist` → `a`
- Shows how objects form a **web of references**, not isolated boxes
