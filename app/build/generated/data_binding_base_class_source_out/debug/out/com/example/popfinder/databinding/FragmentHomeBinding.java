// Generated by view binder compiler. Do not edit!
package com.example.popfinder.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.popfinder.R;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentHomeBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final FloatingActionButton btnMapType;

  @NonNull
  public final CoordinatorLayout coordinate;

  @NonNull
  public final FloatingActionButton currentLocation;

  @NonNull
  public final TextInputEditText edtPlaceName;

  @NonNull
  public final FloatingActionButton enableTraffic;

  @NonNull
  public final ChipGroup placesGroup;

  @NonNull
  public final HorizontalScrollView placesList;

  @NonNull
  public final RecyclerView placesRecyclerView;

  @NonNull
  public final RecyclerView placesRecyclerView2;

  @NonNull
  public final View view;

  private FragmentHomeBinding(@NonNull RelativeLayout rootView,
      @NonNull FloatingActionButton btnMapType, @NonNull CoordinatorLayout coordinate,
      @NonNull FloatingActionButton currentLocation, @NonNull TextInputEditText edtPlaceName,
      @NonNull FloatingActionButton enableTraffic, @NonNull ChipGroup placesGroup,
      @NonNull HorizontalScrollView placesList, @NonNull RecyclerView placesRecyclerView,
      @NonNull RecyclerView placesRecyclerView2, @NonNull View view) {
    this.rootView = rootView;
    this.btnMapType = btnMapType;
    this.coordinate = coordinate;
    this.currentLocation = currentLocation;
    this.edtPlaceName = edtPlaceName;
    this.enableTraffic = enableTraffic;
    this.placesGroup = placesGroup;
    this.placesList = placesList;
    this.placesRecyclerView = placesRecyclerView;
    this.placesRecyclerView2 = placesRecyclerView2;
    this.view = view;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentHomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnMapType;
      FloatingActionButton btnMapType = ViewBindings.findChildViewById(rootView, id);
      if (btnMapType == null) {
        break missingId;
      }

      id = R.id.coordinate;
      CoordinatorLayout coordinate = ViewBindings.findChildViewById(rootView, id);
      if (coordinate == null) {
        break missingId;
      }

      id = R.id.currentLocation;
      FloatingActionButton currentLocation = ViewBindings.findChildViewById(rootView, id);
      if (currentLocation == null) {
        break missingId;
      }

      id = R.id.edtPlaceName;
      TextInputEditText edtPlaceName = ViewBindings.findChildViewById(rootView, id);
      if (edtPlaceName == null) {
        break missingId;
      }

      id = R.id.enableTraffic;
      FloatingActionButton enableTraffic = ViewBindings.findChildViewById(rootView, id);
      if (enableTraffic == null) {
        break missingId;
      }

      id = R.id.placesGroup;
      ChipGroup placesGroup = ViewBindings.findChildViewById(rootView, id);
      if (placesGroup == null) {
        break missingId;
      }

      id = R.id.placesList;
      HorizontalScrollView placesList = ViewBindings.findChildViewById(rootView, id);
      if (placesList == null) {
        break missingId;
      }

      id = R.id.placesRecyclerView;
      RecyclerView placesRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (placesRecyclerView == null) {
        break missingId;
      }

      id = R.id.placesRecyclerView2;
      RecyclerView placesRecyclerView2 = ViewBindings.findChildViewById(rootView, id);
      if (placesRecyclerView2 == null) {
        break missingId;
      }

      id = R.id.view;
      View view = ViewBindings.findChildViewById(rootView, id);
      if (view == null) {
        break missingId;
      }

      return new FragmentHomeBinding((RelativeLayout) rootView, btnMapType, coordinate,
          currentLocation, edtPlaceName, enableTraffic, placesGroup, placesList, placesRecyclerView,
          placesRecyclerView2, view);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
