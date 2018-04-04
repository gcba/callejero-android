package com.gcba.callejero;

import com.gcba.callejero.model.StandardizedAddress;

/**
 * Created by julian on 18/08/17.
 */

public interface SelectionCallback {

    void onAddressSelection(StandardizedAddress address);
    void onCancel();
    void onSelectedPin();
    void onSelectionLabel(String direction);

}
