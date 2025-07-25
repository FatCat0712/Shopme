package com.shopme.admin.setting;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingRepositoryTest {
    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void testCreateGeneralSettings() {
//        Setting siteName = new Setting("SITE_NAME", "Shopme", SettingCategory.GENERAL);
        Setting siteLogo = new Setting("SITE_LOGO", "Shopme.png", SettingCategory.GENERAL);
        Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2025 Shopme Ltd.", SettingCategory.GENERAL);

        List<Setting> savedSettings = (List<Setting>)settingRepository.saveAll(List.of(siteLogo, copyright));
        assertThat(savedSettings.size()).isGreaterThan(0);
    }

    @Test
    public void testCreateCurrencySettings() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType  = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSAND_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        List<Setting> savedSettings = (List<Setting>)settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalPointType, decimalDigits, thousandsPointType));
        assertThat(savedSettings.size()).isGreaterThan(0);
    }

    @Test
    public void testListSettingsByCategory() {
         List<Setting> settings =  settingRepository.findByCategory(SettingCategory.GENERAL);

         settings.forEach(System.out::println);
    }


}
