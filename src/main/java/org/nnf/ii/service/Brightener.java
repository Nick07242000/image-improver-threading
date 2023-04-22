package org.nnf.ii.service;

import lombok.Builder;
import lombok.Getter;
import org.apache.log4j.Logger;

@Getter
@Builder
public class Brightener implements Runnable {
    private final Logger log = Logger.getLogger(Brightener.class);

    @Override
    public void run() {

    }
}
