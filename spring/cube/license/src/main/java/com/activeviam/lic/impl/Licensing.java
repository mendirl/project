package com.activeviam.lic.impl;

public class Licensing {

    public static final ActiveViamLicense getLicense() {
        return new ActiveViamLicense();
    }

    public static boolean checkLicence() {
        return true;
    }
}
