package org.example;

public enum VideoFormats {
    MP4(6 * 1024 * 1024), // 6 MB
    AVI(6 * 1024 * 1024),
    MKV(50 * 1024 * 1024);

    private final long maxSize;

    VideoFormats(long maxSize) {
        this.maxSize = maxSize;
    }
    public long getMaxSize() {
        return maxSize;
    }
}