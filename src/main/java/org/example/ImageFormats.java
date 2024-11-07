package org.example;

public enum ImageFormats {
    JPEG(5 * 1024 * 1024),// 5 MB
    JPG(5 * 1024 * 1024),
    PNG(5 * 1024 * 1024),
    SVG(5 * 1024 * 1024),
    GIF(5 * 1024 * 1024),
    WEBP( 5 * 1024 * 1024);

    private final long maxSize;

    ImageFormats(long maxSize) {
        this.maxSize = maxSize;
    }
    public long getMaxSize() {
        return maxSize;
    }
}


