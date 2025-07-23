package com.shopme.common.entity;

import java.util.List;

public class SettingBag {
    private final List<Setting> listSettings;


    public SettingBag(List<Setting> listSettings) {
        this.listSettings = listSettings;
    }

    public Setting get(String key) {
       int index = listSettings.indexOf(new Setting(key));
       if(index >= 0 ){
           return listSettings.get(index);
       }
      return null;
    }

    public String getValue(String key) {
        int index = listSettings.indexOf(new Setting(key));
        if(index >= 0 ){
            return listSettings.get(index).getValue();
        }
        return null;
    }

    public void update(String key, String value) {
        Setting setting = this.get(key);
        if(setting != null && value != null) {
            setting.setValue(value);
        }
    }

    public List<Setting> list() {
        return listSettings;
    }
}
