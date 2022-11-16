package com.baypackets.ase.rules;

public class MatchObject {

  
  /** The index is the index of the nested opertor that matched the 
   *  rule from within the recursion */
  private int index;

  /** The value is the actual value that matched at this index. 
   *  the combination of the index, value and the Rule object will
   *  uniquely identify this condition that matched */
  private String value;

 
  public int getIndex() {
    return index;
  }

  public String getValue() {
    return value;
  }

  /* the package mutation */
  void setIndex (int index) {
    this.index = index;
  }

  void setValue (String value) {
    this.value = value;
  }

  void setData (int index, String value) {
    this.index = index;
    this.value = value;
  }
  
}
