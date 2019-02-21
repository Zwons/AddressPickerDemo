package com.ymht.library.picker.address;


public interface OnAddressPickerListener {
    void onProvinceStart();

    void onCityStart(int provincePosition);

    void onDistrictStart(int cityPosition);

    void onStreetStart(int districtPosition);

    void onEnsure(int streetPosition, String province, String city, String district, String street);
}
