package com.gcba.callejero.ui.search;

import com.gcba.callejero.model.StandardizedAddress;

import java.util.List;

/**
 * Created by ignacio on 08/06/17.
 */

public interface AddressSearchView {
    void onStartSearch();
    void onEmptySearch();
    void onResultSuccess(String query, List<StandardizedAddress> addressList);
    void onEmptyResults();
    void onResultError();
}
