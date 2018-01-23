package com.gitsearch.data.model;

import android.net.Uri;
import android.text.TextUtils;

import com.gitsearch.data.remote.GitHub;

/**
 * User: Christian Krueger
 * Date: 20.01.2018
 *
 * @since 0.5.2
 */
public class PaginationLink {

  private final String rel;
  private final Integer page;

  public PaginationLink(String link) {
    link = link.trim()
      .replace("<", "")
      .replace(">", "")
      .replace("\"", "");
    String url = link.split(";")[0];

    Uri uri = Uri.parse(url);
    String pageParameter = uri.getQueryParameter(GitHub.PARAMETER_PAGE);

    if (TextUtils.isEmpty(pageParameter)) {
      page = null;
    }
    else {
      page = Integer.valueOf(pageParameter);
    }

    rel = link.split("rel=")[1];
  }

  public String getRel() {
    return rel;
  }

  public int getPage() {
    return page;
  }
}
