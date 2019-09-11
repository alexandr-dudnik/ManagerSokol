package com.sokolua.manager.ui.screens.check_in;

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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sokolua.manager.R;
import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.mvp.views.AbstractView;
import com.sokolua.manager.ui.custom_views.CameraPreview;
import com.sokolua.manager.utils.App;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import flow.Flow;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static java.lang.Math.min;

public class CheckInView extends AbstractView<CheckInScreen.Presenter> {
    @BindView(R.id.map_location_view)   MapView mapView;
    @BindView(R.id.customer_name_text)  TextView customerName;
    @BindView(R.id.datetime_text)  TextView dateTime;
    @BindView(R.id.customer_address)  TextView customerAddress;
    @BindView(R.id.map_latitude_text)  TextView latitudeText;
    @BindView(R.id.map_longitude_text)  TextView longitudeText;
    @BindView(R.id.map_address_text)  TextView mapAddress;
    @BindView(R.id.camera_image_button)    AppCompatImageView mCameraButton;
    @BindView(R.id.placeholder_photo)    AppCompatImageView mPhotoPlaceholder;
    @BindView(R.id.check_in_container)    RelativeLayout mContainer;
    @BindView(R.id.camera_preview)        CameraPreview mCameraPreview;



    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
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

            currentTimeObs = Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .map(it -> new Date())
                    .filter(it -> {cal.setTime(it); return cal.get(Calendar.SECOND)==0;})
                    .map(sdf::format)
                    .doOnNext(it -> dateTime.setText(it))
                    .doOnSubscribe(it -> dateTime.setText(sdf.format(new Date())))
                    .subscribe();

            customerName.setText(mPresenter.getCustomerName());
            customerAddress.setText(mPresenter.getCustomerAddress());

            mapView.onCreate(new Bundle());
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                //locationManager = (LocationManager) App.getContext().getSystemService(LOCATION_SERVICE);
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
        if (locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
        fusedLocationClient.removeLocationUpdates(locationCallback);
        currentTimeObs.dispose();
    }

    private void updateMap(Location location) {
        mapView.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.clear();
            if (location!= null) {
                latitude = (float)location.getLatitude();
                longitude = (float)location.getLongitude();
                LatLng curPos = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(curPos));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 17f));


                latitudeText.setText(String.format(Locale.getDefault(), "%1$.5f", latitude).trim());
                longitudeText.setText(String.format(Locale.getDefault(), "%1$.5f", longitude).trim());

                mapAddress.setText("");
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0){
                        Address adr = addresses.get(0);
                        StringBuilder res = new StringBuilder();
                        int ind = 0;
                        while(adr.getAddressLine(ind) != null){
                            res.append(adr.getAddressLine(ind));
                            ind++;
                        }
                        mapAddress.setText(res);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @OnClick(R.id.camera_image_button)
    void doCheckIn(View view){
        mCameraButton.animate()
                .alpha(255)
                .setDuration(2000)
                .start();
        mPresenter.doCheckIn();
    }

    @Override
    public boolean viewOnBackPressed() {
        return Flow.get(this.getContext()).goBack();
    }

    public float getLatitude(){
        return latitude;
    }

    public float getLongitude(){
        return longitude;
    }

    public void makeScreenshot(){
        mCameraButton.setVisibility(GONE);
        mCameraPreview.setTop(-mCameraPreview.getHeight());
        mPhotoPlaceholder.setVisibility(VISIBLE);

        Camera.PictureCallback takePictureCallBack = (bytes, camera) -> {

            Log.d("SOKOL", "picture taken");

            Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            camera.stopPreview();
            App.releaseCamera();


            float sx = (photo.getHeight()==0)?1f:1f*mContainer.getMeasuredWidth()/photo.getHeight();
            float sy = (photo.getWidth()==0)?1f:1f*mContainer.getMeasuredHeight()/photo.getWidth();

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postScale(min(sx,sy), min(sx,sy));
            Bitmap picture = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
//            Canvas previewCanvas = mCameraPreview.getHolder().lockCanvas();
//            previewCanvas.drawBitmap(picture,0,0, null);
//            mCameraPreview.getHolder().unlockCanvasAndPost(previewCanvas);
            mPhotoPlaceholder.setImageBitmap(picture);


            Bitmap bitmap = Bitmap.createBitmap(mContainer.getMeasuredWidth(), mContainer.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mContainer.layout(mContainer.getLeft(), mContainer.getTop(), mContainer.getRight(), mContainer.getBottom());
            mContainer.draw(canvas);

            mPresenter.setScreenshot(bitmap);

            //mCameraPreview.releaseCamera();
        };


        if (!mCameraPreview.takePicture(takePictureCallBack)){
            mCameraPreview.setTop(mPhotoPlaceholder.getTop());

            Log.d("SOKOL", "picture bot taken");

            Bitmap bitmap = Bitmap.createBitmap(mContainer.getMeasuredWidth(), mContainer.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mContainer.layout(mContainer.getLeft(), mContainer.getTop(), mContainer.getRight(), mContainer.getBottom());
            mContainer.draw(canvas);

            mPresenter.setScreenshot(bitmap);

            mCameraPreview.stopPreview();
            App.releaseCamera();
        }
    }

}
