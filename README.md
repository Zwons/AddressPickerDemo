# AddressPicker
Android仿京东地址选择器，继承自Dialog。
## 运行截图
![运行截图](https://github.com/ymht/AddressPickerDemo/blob/master/art/screenshot.gif)
## 使用
就像Dialog使用那样，很方便使用

- 初始化AddressPicker
 ```
 AddressPicker addressPicker = new AddressPicker(this);
 ```
 - 设置监听以及显示(show())
 ```
 addressPicker.setOnAddressPickerListener(new OnAddressPickerListener() {
                    @Override
                    public void onProvinceStart() {
                    }

                    @Override
                    public void onCityStart(int provincePosition) {
                    }

                    @Override
                    public void onDistrictStart(int cityPosition) {
                    }

                    @Override
                    public void onStreetStart(int districtPosition) {
                    }

                    @Override
                    public void onEnsure(int streetPosition, String province, String city, String district, String street) {
                    }
                });
                addressPicker.show();
 ```
 - 在正确获取到省/市/区/街道数据的时候，调用相应方法
 ```
 /**获取省数据成功，参数为ArrayList<String>类型数据**/
 addressPicker.provinceSuccess(list);
 
 /**获取市数据成功，参数为ArrayList<String>类型数据**/
 addressPicker.citySuccess(list);
 
 /**获取区数据成功，参数为ArrayList<String>类型数据**/
 addressPicker.districtSuccess(list);
 
 /**获取街道数据成功，参数为ArrayList<String>类型数据**/
 addressPicker.streetSuccess(list);
 ```
 
 ```
 /*****新增设置是否开启街道选择（默认开启），在不开启的情况下，onEnsure(int streetPosition, String province, String city, String district, String street)中参数streetPosition为固定值-1、参数street为固定值空字符串*****/
 addressPicker.setOpenStreet(false);//不开启街道显示
 ```
