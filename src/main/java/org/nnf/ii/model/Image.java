package org.nnf.ii.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.nnf.ii.model.enums.Resolution;
import org.nnf.ii.model.enums.Size;
import org.nnf.ii.model.enums.Status;

@Getter
@AllArgsConstructor
@Setter
public class Image {
    private String url;
    private Size size;
    private Resolution resolution;
    private Integer improvements; //synchronized
    private Status status;


}
