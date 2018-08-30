package com.ymht.addresspickerdemo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ymht.library.picker.address.AddressPicker;
import com.ymht.library.picker.address.OnAddressPickerListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView mText;
    private Button mButton;
    private AddressPicker addressPicker;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = findViewById(R.id.text);
        mButton = findViewById(R.id.button);

        addressPicker = new AddressPicker(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressPicker.setOnAddressPickerListener(new OnAddressPickerListener() {
                    @Override
                    public void onProvinceStart() {
                        list.clear();
                        list.add("河南");
                        list.add("安徽");
                        list.add("山东");
                        list.add("湖北");
                        list.add("江苏");
                        list.add("浙江");

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addressPicker.provinceSuccess(list);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onCityStart(int provincePosition) {
                        list.clear();
                        list.add("郑州");
                        list.add("洛阳");
                        list.add("南阳");
                        list.add("开封");
                        list.add("商丘");

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addressPicker.citySuccess(list);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onDistrictStart(int cityPosition) {
                        list.clear();
                        list.add("金水区");
                        list.add("二七区");
                        list.add("惠济区");
                        list.add("中原区");
                        list.add("高新区");

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addressPicker.districtSuccess(list);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onStreetStart(int districtPosition) {
                        list.clear();
                        list.add("未来路街道");
                        list.add("东风路街道");
                        list.add("丰产路街道");
                        list.add("花园路街道");
                        list.add("人民路街道");

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addressPicker.streetSuccess(list);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onEnsure(int streetPosition, String address) {
                        mText.setText(address);
                    }
                });
                addressPicker.show();
            }
        });
    }
}
