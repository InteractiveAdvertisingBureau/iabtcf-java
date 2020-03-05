package com.iabtcf.model;

public interface PurposeRestriction {

  /**
   * @return Unique id for the puroseId/RestrictionType pair
   * @override
   */
  int hashCode();

  void setPurposeId(int id);

  int getPurposeId();
}
