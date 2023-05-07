package org.nnf.ii.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.nnf.ii.model.enums.Size.MEDIUM;
import static org.nnf.ii.model.enums.Status.IN_PROGRESS;
import static org.nnf.ii.model.enums.Status.READY;

@Getter(onMethod_=@Synchronized)
@Setter
@Builder
public class Container {
    private int size;
    private final List<Image> images = new ArrayList<>();

    @Synchronized
    public boolean add(Image image) {
        if (this.hasCapacity() && !this.isPresent(image)) {
            images.add(image);
            return true;
        }
        return false;
    }

    @Synchronized
    public void delete(Image image){
        images.remove(image);
    }

    @Synchronized
    public Optional<Image> getRandom() {
        Optional<Image> image = images.stream()
                .filter(i -> i.getStatus() == READY)
                .findAny();

        image.ifPresent(i -> i.setStatus(IN_PROGRESS));

        return image;
    }

    @Synchronized
    public int getAmountPresent() {
        return images.size();
    }

    @Synchronized
    public boolean isPresent(Image image) {
        return images.contains(image);
    }

    @Synchronized
    public boolean hasCapacity() {
        return images.size() < size;
    }

    @Synchronized
    public long countImprovedImages() {
        return images.stream().filter(i -> i.getImprovements() == 3).count();
    }

    @Synchronized
    public long countResizedImages() {
        return images.stream().filter(i -> i.getSize() == MEDIUM).count();
    }
}
