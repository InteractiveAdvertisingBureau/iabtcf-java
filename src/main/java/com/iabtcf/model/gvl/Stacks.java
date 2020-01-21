package com.iabtcf.model.gvl;

import lombok.Data;

import java.util.List;

@Data
public class Stacks extends GVLMapItem {
    String description;
    List<Integer> purposes;
    List<Integer> specialFeatures;
}
