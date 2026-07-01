# CAM Music Player — OOP Oral Exam Questions

---

## 1. LOGIC QUESTIONS — Why & Purpose

### Q1: Why did you make `MediaItem` abstract instead of a normal class?

**A:** Because `MediaItem` represents a *generic* media concept — it has fields all media share (`id`, `title`) and a `cleanText()`/`validateId()` utility, but it is *incomplete*. A "media item" on its own is too vague to display meaningfully. By marking it `abstract` and declaring `abstract void displayInfo()` (line 181 of DOCUMENTATION.md), I force every subclass — `Song` and `Album` — to provide their own `displayInfo()`. This guarantees that no one can accidentally instantiate a meaningless `MediaItem` object.

---

### Q2: Why does `Artist` implement `Displayable` directly instead of extending `MediaItem`?

**A:** Because an artist is **not** a media item — it has no `id`/`title` in the same sense. An artist has an `artistId`, `name`, `country`, and contains *lists* of songs and albums. Extending `MediaItem` would force an unnatural `getTitle()` to return the artist name, which is semantically wrong. Instead, `Artist` implements `Displayable` directly to satisfy the "I can be displayed" contract without inheriting fields it should not have.

---

### Q3: Why do you have five separate interfaces (`Displayable`, `Loadable`, `Playable`, `QueueManageable`, `Searchable`) instead of one big interface?

**A:** Separation of concerns and the **Interface Segregation Principle**. Each interface represents one *capability*:
- `Displayable` — anything that can print itself
- `Loadable` — anything that loads data
- `Playable` — playback controls only
- `QueueManageable` — queue operations only
- `Searchable` — search/filter operations only

`MusicPlayer` implements four of them because a player *is* playable, queue-manageable, searchable, and displayable. But `DatabaseManager` only implements `Loadable`. If I had one big interface, `DatabaseManager` would be forced to stub out playback methods it doesn't need.

---

### Q4: Why does `DatabaseManager.loadSongs()` return `ArrayList<Song>` when the `Loadable` interface declares `List<Song>`?

**A:** This is **covariant return types** — a Java feature where an overriding method can return a *more specific* type than the parent declares. `ArrayList<Song>` **is-a** `List<Song>`, so it satisfies the contract. I chose `ArrayList` specifically because I need random access and fast iteration in `MusicPlayer`'s search methods. The interface stays flexible (`List`) while the implementation is concrete (`ArrayList`).

---

### Q5: What is the purpose of `static` fields like `songCount` and `artistCount`?

**A:** They track how many instances of each class have been created **across the entire program**. Because they are `static`, they belong to the *class itself*, not to any one object. Every time a `new Song(...)` constructor runs, `songCount++` increments the *same* counter. This lets me call `Song.getSongCount()` anywhere to know "how many songs have ever been created." If `songCount` were an instance field, each `Song` would have its own counter starting at 0.

---

### Q6: Why does `Playlist.addSong()` come in two versions — one with and one without a position?

**A:** **Overloading**. If the user just says "add this song," it goes to the end. If they specify a position, it inserts there. Both are named `addSong` — the compiler tells them apart by the **parameter list** (one parameter vs two). This is a design choice for usability: the caller picks the version that matches their need without having to remember two different method names like `addSongToEnd()` and `addSongAtPosition()`.

---

### Q7: Why does `MusicPlayer` implement `Displayable`?

**A:** Because the player itself has state worth displaying — its name, volume, current song, queue size, history size. By implementing `Displayable`, it can be passed to any method that expects a `Displayable` and printed polymorphically. It also fits the pattern: everything in this project that can describe itself implements `Displayable`.

---

## 2. MEMORY QUESTIONS — Heap vs Stack

### Q8: When you write `Song song1 = new Song(1, "Sunrise", artist1, album1, "Pop", 210);`, what goes on the stack and what goes on the heap?

**A:** The reference variable `song1` lives on the **stack**. The actual `Song` object — with all its fields (`id=1`, `title="Sunrise"`, `artist` reference, `album` reference, `genre="Pop"`, `durationInSeconds=210`) — is allocated on the **heap**. The `artist1` and `album1` references *inside* the Song object point to other heap objects (the `Artist` and `Album` instances). The stack frame for `loadSongs()` also holds local variables like `songs`, `artistMap`, `albumMap`.

---

### Q9: Where is `static int songCount` stored? How is it different from `int durationInSeconds`?

**A:** `static int songCount` is stored in the **method area** (or "class area") of the heap — a special region that holds class-level data. There is exactly *one* copy shared by all `Song` instances. `int durationInSeconds` is an **instance field** — every `Song` object on the heap has its *own* `durationInSeconds`. If you create 1000 `Song` objects, you have 1000 `durationInSeconds` slots but only 1 `songCount`.

---

### Q10: What happens in memory when you call `playbackHistory.add(currentSong)` inside `skipToNext()`?

**A:** The **reference** to the `Song` object (not a copy of the object) is added to the `ArrayList`. The `Song` object itself stays on the heap. Both `currentSong` (a field in `MusicPlayer`) and the last slot of `playbackHistory` now point to the *same* heap object. No new `Song` is created — just another reference to the same one. The `ArrayList`'s internal `Object[]` array on the heap stores this reference.

---

### Q11: When `Main.java` creates `ArrayList<MediaItem> library`, are new Song objects created? What is in memory?

**A:** No new `Song` objects are created. The loop `library.add(s)` copies **references** from `songLibrary` into `library`. The heap has the same 4 `Song` objects — now two `ArrayList` instances on the heap each hold references to them. The reference variable `library` is on the stack. This is **polymorphism** at the reference level: the *static type* is `MediaItem` but the *actual object* on the heap is `Song`.

---

## 3. RUNTIME vs COMPILE TIME

### Q12: When does Java decide which `displayInfo()` to call: at compile time or runtime?

**A:** **Runtime** — this is **dynamic method dispatch** (late binding). At compile time, Java only checks that `displayInfo()` exists in `MediaItem` (it does — declared as `abstract`). But at runtime, the JVM looks at the *actual object's class* on the heap and calls *its* version. This is why the polymorphism demo in `Main.java` works: even though `library` is typed as `ArrayList<MediaItem>`, calling `item.displayInfo()` invokes `Song.displayInfo()` because the heap object is a `Song`.

---

### Q13: When does Java decide which `searchSong` to call — `searchSong(String)` vs `searchSong(String, String)`?

**A:** **Compile time** — this is **static binding** (early binding) or **overload resolution**. The compiler inspects the number and types of arguments at the call site. `player.searchSong("Sunrise")` — one `String` argument — matches the version returning `ArrayList<Song>`. `player.searchSong("Sunrise", "Dara Music")` — two `String` arguments — matches the version returning `Song`. The compiler chooses at compile time, and the bytecode calls a specific method signature.

---

### Q14: What about `addSong(Song)` vs `addSong(Song, int)` — compile time or runtime?

**A:** **Compile time** — same principle. The compiler sees one argument vs two arguments and generates a call to the appropriate method signature. This is resolved before the program ever runs, based on the declared parameter types.

---

### Q15: At compile time, will `MediaItem item = new Song(...); item.displayInfo();` compile? Why?

**A:** Yes, it compiles because `MediaItem` declares `abstract void displayInfo()` (inherited from `Displayable`). The compiler only checks that the method exists in the **declared type**. It doesn't care that the actual runtime type is `Song` — that's the JVM's job at runtime. If `MediaItem` did not have `displayInfo()`, this code would not compile.

---

## 4. HOW IT WORKS — Step by Step

### Q16: Walk through what happens step by step when the user selects option 8 (Play song from playlist) and enters index 1.

**A:**
1. `scanner.nextLine()` reads "1" as a `String`
2. `Integer.parseInt("1")` converts it to `int 1`
3. `songIdx = 1 - 1` → `songIdx = 0`
4. `currentPlaylist.getSongAt(0)` checks bounds: `0 >= 0 && 0 < size` → valid. Returns the `Song` reference at index 0 of the internal `ArrayList`
5. `player.setCurrentSong(selectedSong)` — if a previous `currentSong` existed, it's pushed to `playbackHistory`. Then `currentSong = selectedSong`
6. `player.play()` — `currentSong` is not null, so `playing = true`, prints "Now playing: Sunrise"
7. `break` exits the switch, loops back to print the menu again

---

### Q17: Step by step, what does `super(validateId(songId), cleanText(title, "Unknown Song"))` in `Song`'s constructor actually do?

**A:**
1. `validateId(songId)` is called — this is `MediaItem.validateId()` (a static method inherited but callable directly). If `songId > 0`, it returns the id; else returns 0.
2. `cleanText(title, "Unknown Song")` is called — this is `MediaItem.cleanText()`. If `title` is null or blank, returns `"Unknown Song"`; else returns the trimmed title.
3. `super(validatedId, cleanedTitle)` calls `MediaItem(int, String)` constructor:
   - `this.id = validatedId`
   - `setTitle(cleanedTitle)` — sets `this.title = cleanedTitle`
4. Control returns to `Song` constructor, which then continues with `this.artist = artist`, `this.album = album`, `this.genre = cleanText(genre, "Unknown Genre")`, `setDurationInSeconds(durationInSeconds)`, `songCount++`.

---

### Q18: Walk through what the compiler and JVM do when they encounter `@Override public void displayInfo()` in `Song.java`.

**A:**
- **Compile time**: The `@Override` annotation tells the compiler "I intend to override a method from a superclass or interface." The compiler checks that `displayInfo()` exists in `MediaItem` (or `Displayable`). If it doesn't (e.g., typo like `displayInf()`), the compiler **errors out**. This is a safety check.
- After confirming, the compiler generates a `virtual` method entry in the class file's method table, associating `displayInfo()` with `Song`'s implementation.
- **Runtime**: When `Song.displayInfo()` is called, the JVM looks up the method in `Song`'s class metadata (the vtable), finds the override, and executes it. The parent's `displayInfo()` (if it existed) is never called unless `Song` explicitly uses `super.displayInfo()` — which it doesn't in this code because `MediaItem`'s version is `abstract`.

---

## 5. SCENARIO QUESTIONS — What If

### Q19: What would happen if I removed the `abstract` keyword from `MediaItem` but kept `abstract void displayInfo()`?

**A:** **Compilation error.** In Java, any class that contains an `abstract` method *must* be declared `abstract`. The compiler will reject it: "The type MediaItem must be an abstract class to define abstract methods." You must either add `abstract` to the class or remove `abstract` from the method and provide a concrete body.

---

### Q20: What if I changed `Playlist.addSong(Song song, int position)` to not validate `position < 0 || position > songs.size()`?

**A:** If `position < 0`, `songs.add(position, song)` would throw `IndexOutOfBoundsException` at runtime. If `position > songs.size()`, same exception. The validation prevents a crash and gracefully falls back to appending at the end — this is **defensive programming**.

---

### Q21: What if I called `player.skipToNext()` when the queue is empty AND there is no current song?

**A:** The method checks `playbackQueue.isEmpty()` first (line 188 of MusicPlayer). Since it is empty, it prints "No next song in the queue." and returns `false`. It never reaches the `currentSong` logic. The method is safe because of this **guard clause** at the top.

---

### Q22: What if I changed `Album` to extend `Song` instead of `MediaItem`?

**A:** That would be semantically wrong and architecturally broken. An album is **not a kind of song** — an album *contains* songs. The relationship is "has-a" (composition), not "is-a" (inheritance). If `Album extends Song`, an `Album` would have `durationInSeconds`, `genre`, `artist`, `album` fields from `Song` — all meaningless for an album. It would also violate the **Liskov Substitution Principle**: you could not use an `Album` wherever a `Song` is expected without behavior breaking.

---

### Q23: What happens if I pass `null` to `setCurrentSong(null)`?

**A:** The guard clause at line 136 catches it: prints "Cannot set current song to null." and returns `false`. The previous song stays as `currentSong`. Without this guard, the method would push the old song to history and set `currentSong = null`, potentially causing a `NullPointerException` when `play()` tries to call `currentSong.getTitle()`.

---

### Q24: What if I removed the `private` from `cleanText()` in `Artist` and made it `public`? How would that change things?

**A:** `Artist` has its own `private cleanText()` (line 381), but `MediaItem` already has a `public static cleanText()` (line 160). If `Artist`'s version is made `public`, any code could call `artist.cleanText(...)` — but more importantly, since `Artist` and `MediaItem` are unrelated by inheritance, one does not shadow the other. Making it `public` just widens access. The *design* question is: should text cleaning be a shared utility in `MediaItem`? Both `Artist` and `Playlist` duplicate `cleanText()` and `validateId()` — this is code duplication that could be refactored into a utility class.

---

### Q25: What if I called `new MediaItem(1, "test")` directly — would it compile?

**A:** **No.** `MediaItem` is declared `abstract` (line 143: `public abstract class MediaItem`). The compiler will reject `new MediaItem(...)` with "Cannot instantiate the type MediaItem." This is exactly the purpose of making it abstract — to force creation of only concrete subclasses like `Song` and `Album`.

---

### Q26: What if I added a new class `Podcast extends MediaItem` but forgot to implement `displayInfo()`?

**A:** The compiler will error: "The type Podcast must implement the inherited abstract method Displayable.displayInfo()." Since `MediaItem` inherits `abstract displayInfo()` from `Displayable` and does not implement it, any concrete subclass *must* provide an implementation. If `Podcast` is also `abstract`, it's allowed to skip it. Otherwise, forced by the compiler.

---

## 6. PROJECT-SPECIFIC QUESTIONS — Based on Your Actual Code

### Q27: In `Main.java` lines 1009-1015, you have a polymorphism demo. Explain exactly why `item.displayInfo()` prints Song details even though `item` is declared as `MediaItem`.

**A:** Because Java uses **dynamic method dispatch**. The `ArrayList<MediaItem>` holds `Song` objects. When `item.displayInfo()` is called, the JVM checks the *actual runtime class* of the object (`Song`), finds `Song`'s overridden `displayInfo()`, and executes it — not `MediaItem`'s abstract stub. This would work even if `library` held a mix of `Song` and `Album` objects: each would print its own version.

---

### Q28: In `DatabaseManager.loadSongs()`, why is `connectSong()` called separately instead of putting that logic inside `Song`'s constructor?

**A:** If `connectSong()` (which calls `artist.addSong(song)` and `album.addSong(song)`) were inside `Song`'s constructor, the `this` reference would escape the constructor before the object is fully initialized. This is a anti-pattern called **"leaking `this` from a constructor"** — the `Artist.addSong()` method might try to use the `Song` object before its fields are set. By separating construction from registration, we ensure the `Song` is fully built before any other object references it.

---

### Q29: In `MusicPlayer`, how does `skipToPrevious()` differ from `skipToNext()` in terms of how the queue and history are manipulated?

**A:**
- `skipToNext()`: pushes `currentSong` to the **end** of `playbackHistory`, then pulls the **first** song from `playbackQueue` as the new `currentSong`. The queue shrinks, history grows.
- `skipToPrevious()`: pushes `currentSong` to the **front** of `playbackQueue`, then pulls the **last** song from `playbackHistory` as the new `currentSong`. The queue grows (at index 0), history shrinks.

They are **inverse operations**: next dequeues from front, previous un-dequeues to front and pops from history.

---

### Q30: Why do `getSongsCopy()` in `Album`, `Artist`, `Playlist`, and `MusicPlayer` all return `new ArrayList<>(songs)` instead of the original list?

**A:** **Defensive copying** — encapsulation. If I returned the original `songs` reference, an external caller could do `album.getSongs().clear()` and wipe the album's internal song list. By returning a copy, the caller can modify the returned list (sort, filter, remove) without affecting the object's internal state. The caller gets the data but cannot corrupt the source.

---

### Q31: In `MusicPlayer.setVolume()`, what would happen if you called `setVolume(200)` without the clamping logic?

**A:** `this.volume` would be set to 200 — an invalid value outside the 0-100 range. The `displayInfo()` method and any volume-dependent UI would show "Volume: 200" which makes no sense for a typical audio system. The clamping logic (lines 714-717) ensures the volume is always **within valid bounds** regardless of what value is passed.

---

### Q32: The `searchSong` method is overloaded three times. Why does the one-parameter version return `ArrayList<Song>` while the two- and three-parameter versions return a single `Song`?

**A:** **Cardinality logic**: searching by title alone is ambiguous — many songs might share a title keyword (e.g., "Love"). So it returns a list. Searching by title + artist is much more specific — likely one match — so it returns a single `Song` (or null). Title + artist + genre is the most specific — also returns a single `Song`. The return type reflects the expected *uniqueness* of the result. This is a deliberate API design choice.

---

### Q33: In `MediaItem.cleanText()`, why return a `defaultValue` instead of throwing an exception when the input is null or empty?

**A:** **Fail-soft** design. In a music player, having a song with `title="Unknown Song"` is acceptable — the program can still run and display something meaningful. Throwing a `NullPointerException` or `IllegalArgumentException` would crash the program or require every caller to handle it. The default value keeps the application running gracefully. This is common in data-loading code where some data might be incomplete.

---

### Q34: Look at `Album.addSong()` — it checks `!songs.contains(song)`. How does Java determine if two `Song` objects are "the same"?

**A:** By default, `ArrayList.contains()` uses `equals()` which, unless overridden, uses **reference equality** (`==`). Since `Song` does **not** override `equals()`, two `Song` objects with the same data but created separately (e.g., `new Song(1,...)` twice) would be considered **different** objects. This could be a bug: the same song could be added twice. If deduplication by ID were needed, `Song` would need to override `equals()` and `hashCode()` based on `id`.

---

### Q35: In `Main.java`, why is `playlists.get(currentPlaylistIndex)` called at the top of every loop iteration instead of using a cached reference?

**A:** Because `currentPlaylistIndex` can change during the loop (option 4: "Switch playlist"). If we cached `currentPlaylist = playlists.get(0)` before the loop, switching playlists would update `currentPlaylistIndex` but the cached variable would still point to the old playlist. By re-fetching each iteration, we always have the *current* active playlist. This is a common pattern in stateful menu loops.

---

### Q36: If you wanted to add a `MusicVideo` class that has both song-like properties (title, artist) AND video-specific properties (resolution, duration), should it extend `Song` or `MediaItem`?

**A:** It should extend `MediaItem` directly (not `Song`). A music video is **not** a song with extra stuff — it's a different kind of media. Extending `Song` would inherit `genre`, `album`, `durationInSeconds` — but a music video's duration might be different from the audio track, and it might not belong to an "album" in the same sense. Extending `MediaItem` keeps the hierarchy clean: `MediaItem` → `Song`, `MediaItem` → `MusicVideo`. Both share `id`, `title`, and the contract to implement `displayInfo()`.
