package com.shopme.admin.setting;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingBag;

import java.util.List;

public class GeneralSettingBag extends SettingBag {

    public GeneralSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public void updateCurrencySymbol(String value) {
        if(value != null) {
            super.update("CURRENCY_SYMBOL", value);
        }
    }

    public void updateSiteLogo(String value) {
        if (value != null) {
            super.update("SITE_LOGO", value);
        }
    }

    public void updateSiteMascot(String value) {
        if(value != null) {
            super.update("SITE_MASCOT", value);
        }
    }
}
