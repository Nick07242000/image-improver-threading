package org.nnf.ii;

import org.nnf.ii.model.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import static org.nnf.ii.model.enums.Resolution.LOW;
import static org.nnf.ii.model.enums.Size.SMALL;
import static org.nnf.ii.model.enums.Status.READY;

public class Main {

    public static void main(String[] args) {

        List<Image> images = new ArrayList<>();

        while (images.size() != 100){
            images.add(new Image(
                    "https://s3.amazonaws.com/bucket/name-not-found/" + UUID.randomUUID(),
                    SMALL,
                    LOW,
                    0,
                    READY
            ));
        }

        Stack<Image> startContainer = new Stack<>();

    }
}
