package com.iabtcf.model.gvl;


import lombok.*;

import java.util.Map;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class VendorList {
   private Map<String,Vendor> vendors;

}
