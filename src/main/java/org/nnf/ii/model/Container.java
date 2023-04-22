package org.nnf.ii.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Container {
    private int size;
    private List<String> images;

    public boolean isFull() {
        return images.size() == size;
    }
}
