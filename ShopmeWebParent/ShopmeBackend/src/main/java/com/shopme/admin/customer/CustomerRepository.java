package com.shopme.admin.customer;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer>, SearchRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer  c WHERE " +
            "c.firstName LIKE %?1% OR " +
            "c.lastName LIKE %?1% OR " +
            "c.email LIKE %?1% OR " +
            "c.addressLine1 LIKE %?1% OR " +
            "c.addressLine1 LIKE %?1% OR " +
            "c.city LIKE %?1% OR " +
            "c.state LIKE %?1% OR " +
            "c.country.name LIKE %?1% OR " +
            "c.postalCode LIKE %?1%")
    Page<Customer> findAll(String keyword, Pageable pageable);

    Customer findByEmail(String email);

    @Query("UPDATE Customer c SET c.isEnabled = TRUE WHERE c.id = ?1")
    @Modifying
    void updateEnableStatus(Integer id);

    Long countById(Integer id);


}
