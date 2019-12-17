package com.iabtcf.gdpr.phase2.model.gvl;


import lombok.*;

import java.util.Map;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class VendorList {
   private Map<String,Vendor> vendors;

}
