package com.sokolua.manager.ui.screens.check_in;

import static java.lang.Math.min;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sokolua.manager.databinding.ScreenCheckInBinding;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.utils.App;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import flow.Flow;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CheckInView extends AbstractView<CheckInScreen.Presenter, ScreenCheckInBinding> {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private float latitude;
    private float longitude;
    private Geocoder gcd;

    private Disposable currentTimeObs;

    public CheckInView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScreenCheckInBinding bindView(View view) {
        return ScreenCheckInBinding.bind(view);
    }

    @Override
    protected void initDagger(Context context) {
        if (!isInEditMode()) {
            DaggerService.<CheckInScreen.Component>getDaggerComponent(context).inject(this);

            gcd = new Geocoder(App.getContext(), Locale.getDefault());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            Calendar cal = Calendar.getInstance();

            currentTimeObs = Observable.interval(1, TimeUnit.MINUTES)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .map(it -> new Date())
                    .filter(it -> {
                        cal.setTime(it);
                        return cal.get(Calendar.SECOND) == 0;
                    })
                    .map(sdf::format)
                    .doOnNext(it -> binding.datetimeText.setText(it))
                    .doOnSubscribe(it -> binding.datetimeText.setText(sdf.format(new Date())))
                    .doOnError(throwable -> Log.e("ERROR", "Check in", throwable))
                    .subscribe();

            binding.customerNameText.setText(mPresenter.getCustomerName());
            binding.customerAddress.setText(mPresenter.getCustomerAddress());

            binding.cameraImageButton.setOnClickListener(view -> {
                binding.cameraImageButton.animate()
                        .alpha(1)
                        .setDuration(2000)
                        .start();
                mPresenter.doCheckIn();
            });

            binding.mapLocationView.onCreate(new Bundle());
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

                fusedLocationClient.getLastLocation().addOnSuccessListener(this::updateMap);

                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            updateMap(location);
                        }
                    }
                };
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (currentTimeObs != null && !currentTimeObs.isDisposed()) {
            currentTimeObs.dispose();
        }
    }

    private void updateMap(Location location) {
        binding.mapLocationView.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.clear();
            if (location != null) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                LatLng curPos = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(curPos));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 17f));

                binding.mapLatitudeText.setText(String.format(Locale.getDefault(), "%1$.5f", latitude).trim());
                binding.mapLongitudeText.setText(String.format(Locale.getDefault(), "%1$.5f", longitude).trim());

                binding.mapAddressText.setText("");

                Observable.just(new Pair<Float, Float>(latitude, longitude))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(coords -> gcd.getFromLocation(coords.first, coords.second, 1))
                        .onErrorReturn(throwable -> new ArrayList<Address>())
                        .flatMap(Observable::fromIterable)
                        .take(1)
                        .doOnNext(adr -> {
                            StringBuilder res = new StringBuilder();
                            int ind = 0;
                            while (adr.getAddressLine(ind) != null) {
                                res.append(adr.getAddressLine(ind));
                                ind++;
                            }
                            binding.mapAddressText.setText(res);
                        })
                        .doOnError(thrw -> {
                            Log.d("UPDATE LOCATION", "updateMap: ", thrw);
                        })
                        .subscribe();
            }
        });
    }

    @Override
    public boolean viewOnBackPressed() {
        return Flow.get(this.getContext()).goBack();
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void makeScreenshot() {
        binding.cameraImageButton.setVisibility(GONE);
        binding.cameraPreview.setTop(-binding.cameraPreview.getHeight());
        binding.placeholderPhoto.setVisibility(VISIBLE);

        Camera.PictureCallback takePictureCallBack = (bytes, camera) -> {
            Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            camera.stopPreview();
            App.releaseCamera();

            float sx = (photo.getHeight() == 0) ? 1f : 1f * binding.checkInContainer.getMeasuredWidth() / photo.getHeight();
            float sy = (photo.getWidth() == 0) ? 1f : 1f * binding.checkInContainer.getMeasuredHeight() / photo.getWidth();

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postScale(min(sx, sy), min(sx, sy));
            Bitmap picture = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
            binding.placeholderPhoto.setImageBitmap(picture);

            Bitmap bitmap = Bitmap.createBitmap(binding.checkInContainer.getMeasuredWidth(), binding.checkInContainer.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            binding.checkInContainer.layout(binding.checkInContainer.getLeft(), binding.checkInContainer.getTop(), binding.checkInContainer.getRight(), binding.checkInContainer.getBottom());
            binding.checkInContainer.draw(canvas);

            mPresenter.setScreenshot(bitmap);
        };

        if (!binding.cameraPreview.takePicture(takePictureCallBack)) {
            binding.cameraPreview.setTop(binding.placeholderPhoto.getTop());

            Bitmap bitmap = Bitmap.createBitmap(binding.checkInContainer.getMeasuredWidth(), binding.checkInContainer.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            binding.checkInContainer.layout(binding.checkInContainer.getLeft(), binding.checkInContainer.getTop(), binding.checkInContainer.getRight(), binding.checkInContainer.getBottom());
            binding.checkInContainer.draw(canvas);

            mPresenter.setScreenshot(bitmap);

            binding.cameraPreview.stopPreview();
            App.releaseCamera();
        }
    }

}
