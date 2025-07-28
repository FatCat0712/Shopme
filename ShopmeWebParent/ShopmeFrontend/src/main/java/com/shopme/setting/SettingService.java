package com.shopme.setting;

import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {
    private final SettingRepository settingRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public SettingService(SettingRepository settingRepository, CurrencyRepository currencyRepository) {
        this.settingRepository = settingRepository;
        this.currencyRepository = currencyRepository;
    }

    public List<Setting> getGeneralSettings() {
         return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    public void saveAll(Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }

    public EmailSettingBag getEmailSettings() {
        return new EmailSettingBag(settingRepository.findByTwoCategories(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES));
    }

    public CurrencySettingBag getCurrencySettings() {
        return new CurrencySettingBag(settingRepository.findByCategory(SettingCategory.CURRENCY));
    }

    public PaymentSettingBag getPaymentSettings() {
        return new PaymentSettingBag(settingRepository.findByCategory(SettingCategory.PAYMENT));
    }

    public String getCurrencyCode() {
        Setting setting =  settingRepository.findByKey("CURRENCY_ID");
        Integer currencyId = Integer.parseInt(setting.getValue());
        Currency currency = currencyRepository.findById(currencyId).get();
        return currency.getCode();
    }


}
