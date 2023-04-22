package org.nnf.ii.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Container {
    private int size;
    private List<Image> images;

    public void add(Image image) {
        if (!this.isFull()) {
            images.add(image);
        }
    }

    public boolean isFull() {
        return images.size() == size;
    }

    public boolean isPresent(Image image) {
        return images.contains(image);
    }
}
