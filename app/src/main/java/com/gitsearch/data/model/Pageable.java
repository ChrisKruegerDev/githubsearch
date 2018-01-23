package com.gitsearch.data.model;

public class Pageable<T> {
  public int last;
  public T value;

  public Pageable(T value, int last) {
    this.last = last;
    this.value = value;
  }
}
