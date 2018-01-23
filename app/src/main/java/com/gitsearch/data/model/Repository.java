package com.gitsearch.data.model;

import com.google.gson.annotations.SerializedName;

public class Repository {

  public long id;

  @SerializedName("full_name")
  public String fullName;

  public User owner;

  public double score;


}
