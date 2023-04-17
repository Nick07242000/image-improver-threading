package org.nnf.ii;

import org.nnf.ii.model.Image;

import java.util.List;
import java.util.Stack;

import static org.nnf.ii.repository.ImageRepository.findAll;

public class Main {

    public static void main(String[] args) {

        List<Image> images = findAll();

        Stack<Image> startContainer = new Stack<>();

    }
}
