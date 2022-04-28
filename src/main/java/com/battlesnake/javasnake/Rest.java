package com.battlesnake.javasnake;

public class Rest {
    private final long id;

    private final String content;

    public Rest(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
