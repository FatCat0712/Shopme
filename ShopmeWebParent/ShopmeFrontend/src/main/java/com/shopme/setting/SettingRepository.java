package com.shopme.setting;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {
   List<Setting> findByCategory(SettingCategory settingCategory);

    @Query("SELECT s FROM Setting s WHERE s.category = ?1 OR s.category = ?2")
   List<Setting> findByTwoCategories(SettingCategory catOne, SettingCategory catTwo);

    Setting findByKey(String key);
}
