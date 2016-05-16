package com.gaborbiro.foodie.ui.presenter;

public interface PlacesPresenter {
    void loadPlaces();
    void onScreenStarted();
    void onScreenStopped();
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}
