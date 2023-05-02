package org.nnf.ii.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.nnf.ii.model.enums.Size;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.getRandomNumber;

@Getter
@Setter
@Builder
public class Container {
    private int size;
    private final List<Image> images = new ArrayList<>();

    public void add(Image image) {
        if (this.hasCapacity()) {
            images.add(image);
        }
    }

    public void delete(Image image){
        images.remove(image);
    }

    public Image getRandom() {
        return images.get(getRandomNumber(0, images.size()));
    }

    public int getAmountPresent() {
        return images.size();
    }

    public List<Image> getImagesOfSize(Size size) {
        return images.stream().filter(i -> i.getSize() == size).collect(toList());
    }

    public boolean isPresent(Image image) {
        return images.contains(image);
    }

    public boolean hasCapacity() {
        return images.size() < size;
    }

    public boolean hasReadyImages() {
        return images.stream().anyMatch(i -> i.getStatus() == READY);
    }

}
