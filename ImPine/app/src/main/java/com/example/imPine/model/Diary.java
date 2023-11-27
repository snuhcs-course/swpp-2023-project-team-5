package com.example.imPine.model;

public class Diary {
    private String title;
    private String content;
    private boolean isPrivate;

    // Private constructor to be used by the Builder
    private Diary(Builder builder) {
        this.title = builder.title;
        this.content = builder.content;
        this.isPrivate = builder.isPrivate;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }


    // Static Builder class
    public static class Builder {
        private String title;
        private String content;
        private boolean isPrivate;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setIsPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Diary build() {
            return new Diary(this);
        }
    }
}
