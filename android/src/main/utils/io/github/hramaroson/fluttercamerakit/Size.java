package io.github.hramaroson.fluttercamerakit;

import android.support.annotation.NonNull;

/**
 * A simple class representing a size, with width and height values.
 */
class Size implements Comparable<Size> {

    private final int width;
    private final int height;

    Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    Size flip() {
        return new Size(height, width);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof Size) {
            Size size = (Size) o;
            return width == size.width && height == size.height;
        }
        return false;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }

    @Override
    public int hashCode() {
        return height ^ ((width << (Integer.SIZE / 2)) | (width >>> (Integer.SIZE / 2)));
    }

    @Override
    public int compareTo(@NonNull Size another) {
        return width * height - another.width * another.height;
    }

}