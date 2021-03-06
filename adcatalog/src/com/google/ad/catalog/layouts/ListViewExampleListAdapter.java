// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.ad.catalog.layouts;

import com.google.ad.catalog.AdCatalogUtils;
import com.google.ad.catalog.Constants;
import com.google.ad.catalog.LogAndToastAdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Custom list adapter to embed AdMob ads in a ListView at the top
 * and bottom of the screen.
 *
 * @author api.rajpara@gmail.com (Raj Parameswaran)
 */
public class ListViewExampleListAdapter extends BaseAdapter {
  private static final String LOGTAG = "ListViewExampleListAdapter";

  private final Activity activity;
  private final BaseAdapter delegate;

  public ListViewExampleListAdapter(Activity activity, BaseAdapter delegate) {
    this.activity = activity;
    this.delegate = delegate;
  }

  @Override
  public int getCount() {
    // Total count includes list items and ads.
    return delegate.getCount() + 2;
  }

  @Override
  public Object getItem(int position) {
    // Return null if an item is an ad.  Otherwise return the delegate item.
    if (isItemAnAd(position)) {
      return null;
    }
    return delegate.getItem(getOffsetPosition(position));
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (isItemAnAd(position)) {
      if (convertView instanceof AdView) {
        Log.d(LOGTAG, "I am reusing an ad");
        return convertView;
      } else {
        Log.d(LOGTAG, "I am creating a new ad");
        AdView adView = new AdView(activity);
        adView.setAdUnitId(Constants.getAdmobId(activity));
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.loadAd(AdCatalogUtils.createAdRequest());
        adView.setAdListener(new LogAndToastAdListener(activity));
        return adView;
      }
    } else {
      return delegate.getView(getOffsetPosition(position), convertView, parent);
    }
  }

  @Override
  public int getViewTypeCount() {
    return delegate.getViewTypeCount() + 1;
  }

  @Override
  public int getItemViewType(int position) {
    if (isItemAnAd(position)) {
      return delegate.getViewTypeCount();
    } else {
      return delegate.getItemViewType(getOffsetPosition(position));
    }
  }

  @Override
  public boolean areAllItemsEnabled() {
    return false;
  }

  @Override
  public boolean isEnabled(int position) {
    return (!isItemAnAd(position)) && delegate.isEnabled(getOffsetPosition(position));
  }

  private boolean isItemAnAd(int position) {
    // Place an ad at the first and last list view positions.
    return (position == 0 || position == (getCount() - 1));
  }

  private int getOffsetPosition(int position) {
    return position - 1;
  }
}
