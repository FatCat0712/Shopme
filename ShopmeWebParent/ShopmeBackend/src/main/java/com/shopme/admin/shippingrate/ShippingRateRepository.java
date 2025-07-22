package com.shopme.admin.shippingrate;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer>, SearchRepository<ShippingRate, Integer> {
    @Query("SELECT s FROM ShippingRate s WHERE s.country.id = ?1 AND s.state = ?2")
    ShippingRate findByCountryAndState(Integer countryId, String state);

    @Query("UPDATE ShippingRate s SET  s.codSupported = TRUE WHERE s.id = ?1")
    @Modifying
    void updateCODSupported(Integer id, Boolean enabled);

    @Query("SELECT s FROM ShippingRate  s WHERE s.country.name LIKE ?1 OR s.state LIKE ?1")
    Page<ShippingRate> findAll(String keyword, Pageable pageable);

    Long  countById(Integer id);

}
