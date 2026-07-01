package model;

import interfaces.Displayable;

// Base class for all media items in the music system.
// Provides common fields (id, title) and basic accessors.

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

    // Returns the unique identifier of this media item.
    public int getId() {
        return id;
    }

    // Returns the title of this media item.

    public String getTitle() {
        return title;
    }

    // Sets the title of this media item. Silently ignores null or blank values.

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    // abstract method to be implemented by subclasses to display their own version of details

    public abstract void displayInfo();
}
    
