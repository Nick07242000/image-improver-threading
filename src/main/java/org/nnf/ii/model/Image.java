package org.nnf.ii.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.nnf.ii.model.enums.Resolution;
import org.nnf.ii.model.enums.Size;
import org.nnf.ii.model.enums.Status;

@Getter(onMethod_={@Synchronized})
@Setter(onMethod_={@Synchronized})
@AllArgsConstructor
public class Image {
    private String url;
    private Size size;
    private Resolution resolution;
    private Integer improvements;
    private Status status;
}
