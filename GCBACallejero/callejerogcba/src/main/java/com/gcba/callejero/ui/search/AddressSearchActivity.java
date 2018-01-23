package com.gcba.callejero.ui.search;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gcba.callejero.CallejeroManager;
import com.gcba.callejero.LocationCallBack;
import com.gcba.callejero.R;
import com.gcba.callejero.SearchCallback;
import com.gcba.callejero.CallejeroCTE;
import com.gcba.callejero.model.AddressLocation;
import com.gcba.callejero.model.NormalizeResponse;
import com.gcba.callejero.model.Places.PlacesLocations;
import com.gcba.callejero.model.Places.PlacesObjectContent;
import com.gcba.callejero.model.StandardizedAddress;
import com.gcba.callejero.ui.CallejeroUtils;
import com.gcba.callejero.ui.CallejeroView;
import com.gcba.callejero.ui.LocationCallbackPlacesObjectCntent;
import com.gcba.callejero.ui.search.adapter.CallejeroAdapter;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by ignacio on 08/06/17.
 */
public class AddressSearchActivity extends AppCompatActivity implements AddressSearchView{

    private AddressSearchPresenter presenter;

    private SearchView searchView;
    private ListView list;
    private ProgressBar progress;
    private TextView error;
    private TextView currentAddress;
    private boolean showPin;
    private boolean showLabel;
    private String direccion;
    List<StandardizedAddress> addressListNormalizada;
    private LinearLayout rootLayout;
    private String x;
    private String y;
    PlacesLocations placesLocations;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_slide_up, R.anim.activity_no_change);
        setContentView(R.layout.activity_search_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_address_toolbar);
        setSupportActionBar(toolbar);

        setTitle("");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        presenter = new AddressSearchPresenter(getApplicationContext());

        showPin = getIntent().getBooleanExtra(CallejeroCTE.SHOW_PIN,false);
        showLabel = getIntent().getBooleanExtra(CallejeroCTE.SHOW_LABEL,false);



        initControls();

        if (showLabel){
            currentAddress.setVisibility(View.VISIBLE);
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            runEnterAnimation();
        }
    }

    private void runEnterAnimation(){

        rootLayout.setVisibility(View.INVISIBLE);

        ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    circularRevealActivity();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void circularRevealActivity() {

        int cx = rootLayout.getWidth() / 2;
        int cy = rootLayout.getHeight() / 2;

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(700);

        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onAttachView(this);
        presenter.recents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onDetachView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls(){

        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        searchView = (SearchView) findViewById(R.id.search_address_view);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        RxSearchView.queryTextChanges(searchView)
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        currentAddress.setText(charSequence);
                        if(charSequence.length() >= 3) {
                            boolean onlyFromCABA = getIntent().getBooleanExtra( CallejeroCTE.SHOW_ADDRESS_FROM_CABA, false);
                            boolean showPlaces = getIntent().getBooleanExtra( CallejeroCTE.SHOW_PLACES, false);
                            presenter.search(charSequence.toString(), onlyFromCABA,showPlaces, charSequence);
                        }else{

                        }

                    }
                });

        list = (ListView) findViewById(R.id.search_address_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (list.getItemAtPosition(position) == null && showPin){
                    deliverResult(null);
                }else {
                    StandardizedAddress address = (StandardizedAddress) list.getItemAtPosition(position);

                    if (address.isPlace()){
                        //TODO : llamar a Object content y luego al normalizador

                         final String idPlaces = address.getIdPlaceInstance();

                         CallejeroManager.getInstance().loadPlacesObjectContent(idPlaces, new LocationCallbackPlacesObjectCntent() {
                            @Override
                            public void onSuccess(PlacesObjectContent placesTwo) {
                                direccion = placesTwo.getDireccionNormalizada();

                                String point =  placesTwo.getUbicacion().getControide();

                                final Double pointX = CallejeroUtils.parseX(point);
                                final Double pointY = CallejeroUtils.parseY(point);
                                AddressLocation addressLocation = new AddressLocation();
                                addressLocation.setX(pointX);
                                addressLocation.setY(pointY);


                                if (direccion != ""){

                                    CallejeroManager.getInstance().normalizeQuery(direccion, true, new SearchCallback() {
                                        @Override
                                        public void onSuccess(NormalizeResponse normalize) {
                                            addressListNormalizada = normalize.getAddressList();
                                            //TODO : si el size es mayor o igual a 1
                                            StandardizedAddress standardizedAddress = addressListNormalizada.get(0);
                                            StandardizedAddress address = (StandardizedAddress) list.getItemAtPosition(position);
                                            AddressLocation addressLocation = new AddressLocation();
                                            addressLocation.setX(pointX);
                                            addressLocation.setY(pointY);

                                            address.setIdPlaceInstance(idPlaces);
                                            address.setStreetCode(address.getStreetCode());
                                            address.setNumber(address.getNumber());
                                            address.setStreetName(standardizedAddress.getStreetName());
                                            address.setName(standardizedAddress.getName());
                                            address.setStreetCornerName(address.getStreetCornerName());
                                            address.setType("PLACE");
                                            address.setCityCode(address.getCityCode());
                                            address.setLocation(addressLocation);

                                            searchView.setQuery(address.getName(), false);
                                            hideKeyboard();
                                            deliverResult(address);

                                        }

                                        @Override
                                        public void onError(Throwable error) {
                                            Log.d("CALLEGERO",error.getMessage());
                                        }
                                    });

                                }else {

                                CallejeroManager.getInstance().loadAddressLatLong(addressLocation, new LocationCallBack() {
                                    @Override
                                    public void onSuccess(StandardizedAddress address) {
                                        address.setIdPlaceInstance(idPlaces);
                                        searchView.setQuery(address.getName(), false);
                                        hideKeyboard();
                                        deliverResult(address);
                                    }

                                    @Override
                                    public void onError(Throwable error) {
                                        System.out.println("ERRORRR");
                                    }
                                });

                                }


                            }
                            @Override
                            public void onError(Throwable error) {
                                Log.d("CALLEGERO",error.getMessage());
                            }
                        });
                    }else {
                        if(address.isStreet()){
                            searchView.setQuery(address.getStreetName() + " ", false);
                        }else {
                            hideKeyboard();
                            deliverResult(address);
                        }

                    }
                }
            }
        });

        progress = (ProgressBar) findViewById(R.id.search_address_progress);
        error = (TextView) findViewById(R.id.search_error);


        currentAddress = (TextView) findViewById(R.id.currentAddress);
        currentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliverResultString(currentAddress.getText().toString());
//              Toast.makeText(AddressSearchActivity.this,currentAddress.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_no_change, R.anim.activity_slide_down);

        Intent data = new Intent();
        data.putExtra(CallejeroView.REQUEST_ID_DATA, getIntent().getIntExtra(CallejeroView.REQUEST_ID_DATA, 0));

        setResult(RESULT_CANCELED, data);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_no_change, R.anim.activity_slide_down);
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void deliverResult(StandardizedAddress standardizedAddress){

        Intent data = new Intent();
        data.putExtra(CallejeroView.REQUEST_ID_DATA, getIntent().getIntExtra(CallejeroView.REQUEST_ID_DATA, 0));

        if (standardizedAddress == null){
            setResult(15001, data);
            finish();
            return;
        }

        presenter.saveSelection(standardizedAddress);

        data.putExtra( CallejeroCTE.STANDARDIZED_ADDRESS_DATA, standardizedAddress);

        setResult(RESULT_OK, data);
        finish();
    }

    private void deliverResultString(String current){

        Intent data = new Intent();
        data.putExtra(CallejeroView.REQUEST_ID_DATA, getIntent().getIntExtra(CallejeroView.REQUEST_ID_DATA, 0));
        data.putExtra("current", current);
        setResult(15002, data);
        finish();
    }



    @Override
    public void onStartSearch() {
        progress.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        reset();
    }

    @Override
    public void onEmptySearch() {
        progress.setVisibility(View.GONE);
        reset();
    }

    private void reset(){
        list.setAdapter(null);
        list.setVisibility(View.GONE);
    }

    @Override
    public void onResultSuccess(String query, List<StandardizedAddress> addressList) {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.GONE);

        if(list.getAdapter() == null)

            list.setAdapter(new CallejeroAdapter(addressList,query, showPin));
        else {
            CallejeroAdapter adapter = (CallejeroAdapter) list.getAdapter();
            adapter.addSearchs(addressList);
        }

        list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEmptyResults() {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        error.setText(R.string.search_empty_results_error);
    }

    @Override
    public void onResultError() {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        error.setText(R.string.search_network_error);
        list.setVisibility(View.GONE);
    }
}
