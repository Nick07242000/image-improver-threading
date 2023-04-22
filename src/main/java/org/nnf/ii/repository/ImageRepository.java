package org.nnf.ii.repository;

import org.nnf.ii.model.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.nnf.ii.model.enums.Resolution.LOW;
import static org.nnf.ii.model.enums.Size.SMALL;
import static org.nnf.ii.model.enums.Status.READY;

public final class ImageRepository {
    private ImageRepository() {}

    public static List<Image> findAll(int amount) {
        List<Image> images = new ArrayList<>();

        while (images.size() != amount) {
            images.add(new Image(
                    "https://s3.amazonaws.com/bucket/name-not-found/" + UUID.randomUUID(),
                    SMALL,
                    LOW,
                    0,
                    READY
            ));
        }

        return images;
    }
}
