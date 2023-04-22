package org.nnf.ii;

import org.apache.log4j.Logger;
import org.nnf.ii.model.Image;

import java.util.List;
import java.util.Stack;

import static org.nnf.ii.repository.ImageRepository.findAll;

public class Main {

    public static void main(String[] args) {

        Logger logger = Logger.getLogger(Main.class);

        List<Image> images = findAll(500);

    }
}
