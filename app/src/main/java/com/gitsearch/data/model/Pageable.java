package com.gitsearch.data.model;

public class Pageable<T> {

  public final int last;
  public final T value;

  public Pageable(T value, int last) {
    this.last = last;
    this.value = value;
  }

}
