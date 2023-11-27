package com.example.imPine.model;

public class Diary {
    private int id;
    private String title;
    private String content;
    private boolean is_private;

    private String category;
    private String image_src;

    // Private constructor to be used by the Builder
    private Diary(Builder builder) {
        this.title = builder.title;
        this.content = builder.content;
        this.is_private = builder.is_private;
        this.category = builder.category;
    }

    public String getImage_src() {
        return image_src;
    }

    public String getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public boolean getIsPrivate() {
        return is_private;
    }


    // Static Builder class
    public static class Builder {
        private String title;
        private String content;
        private boolean is_private;
        private String image_src;

        private String category;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setIsPrivate(boolean isPrivate) {
            this.is_private = isPrivate;
            return this;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Builder setImageSrc(String imageSrc) {
            this.image_src = imageSrc;
            return this;
        }
        public Diary build() {
            return new Diary(this);
        }
    }
}
