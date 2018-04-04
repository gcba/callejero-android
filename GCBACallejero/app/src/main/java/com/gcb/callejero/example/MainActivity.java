package com.gcb.callejero.example;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gcba.callejero.CallejeroOptions;
import com.gcba.callejero.SelectionCallback;
import com.gcba.callejero.model.AddressLocation;
import com.gcba.callejero.model.StandardizedAddress;
import com.gcba.callejero.ui.CallejeroView;

public class MainActivity extends AppCompatActivity {

    private CallejeroView callejeroView;
    private AddressLocation location;
    private TextView queFuncionEstaUsando;
    private TextView locationX;
    private TextView locationY;
    Switch switchButton;

    CallejeroOptions options = new CallejeroOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        location = new AddressLocation();

        location.setY(-34.544028);
        location.setX(-58.482504);

        callejeroView = (CallejeroView) findViewById(R.id.callejero);
        switchButton = (Switch) findViewById(R.id.switchButton);

        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                options.setShowPin(isChecked);
            }
        });

        queFuncionEstaUsando = (TextView) findViewById(R.id.Direccion);
        switchButton = (Switch) findViewById(R.id.switchButton2);

        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                options.setShowLabel(!isChecked);
                callejeroView.setOptions(options);

            }
        });

        Switch switchButton2 = (Switch) findViewById(R.id.switchButtonShowPlaces);

        switchButton2.setChecked(false);
        switchButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                options.setShowPlaces(isChecked);
                callejeroView.setOptions(options);

            }
        });

        switchButton = (Switch) findViewById(R.id.switchButton3);

        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                options.setShowOnlyFromCaba(isChecked);
                callejeroView.setOptions(options);

            }
        });

        allowCallejeroLocation();

        callejeroView.setSelectionCallback(new SelectionCallback() {
            @Override
            public void onAddressSelection(StandardizedAddress address) {
                AddressLocation location = address.getLocation();

                locationX.setText(String.valueOf(location.getX()));
                locationY.setText(String.valueOf(location.getY()));
                locationY.setText(address.toString());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelectedPin() {
                queFuncionEstaUsando.setText("PIN");
                // Toast.makeText(MainActivity.this, "onSelectedPin", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelectionLabel(String direction) {
                // callejeroView.setSelectedAddress(direction);
                queFuncionEstaUsando.setText(direction);
                Toast.makeText(MainActivity.this, direction, Toast.LENGTH_SHORT).show();
            }
        });

        locationX = (TextView) findViewById(R.id.location_x);
        locationY = (TextView) findViewById(R.id.location_y);
    }

    private void allowCallejeroLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }

        callejeroView.enabledLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            allowCallejeroLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callejeroView.onActivityResult(requestCode, resultCode, data);
    }

}
