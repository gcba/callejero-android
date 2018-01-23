package com.gcba.callejero;

/**
 * Created by julian on 12/09/17.
 */

public class CallejeroOptions {

    private Boolean showOnlyFromCaba = true;
    private Boolean showPin = false;
    private Boolean showLabel = false;
    private Boolean showPlaces = false;




    public Boolean getShowOnlyFromCaba() {
        return showOnlyFromCaba;
    }

    public CallejeroOptions setShowOnlyFromCaba(Boolean showOnlyFromCaba) {
        this.showOnlyFromCaba = showOnlyFromCaba;
        return this;
    }

    public Boolean getShowPin() {
        return showPin;
    }

    public CallejeroOptions setShowPin(Boolean showPin) {
        this.showPin = showPin;
        return this;
    }

    public Boolean getShowLabel() {
        return showLabel;
    }

    public CallejeroOptions setShowLabel(Boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    public Boolean getShowPlaces() {
        return showPlaces;
    }

    public CallejeroOptions setShowPlaces(Boolean showPlaces) {
        this.showPlaces = showPlaces;
            return this;}



}
