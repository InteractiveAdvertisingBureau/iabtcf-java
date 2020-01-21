package com.iabtcf.model.gvl;

import lombok.*;

@Getter @Setter
public class Purpose extends GVLMapItem {
    String description;
    String descriptionLegal;

    Boolean consentable = true;
    Boolean rightToObject = true;

}
