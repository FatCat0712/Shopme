package com.shopme.admin.order;


import com.shopme.common.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends CrudRepository<OrderDetail, Integer> {
    @Query("SELECT NEW com.shopme.common.entity.order.OrderDetail(od.product.category.name, od.quantity, od.productCost, od.shippingCost, od.subTotal)" +
            "FROM OrderDetail od " +
            "WHERE od.order.orderTime " +
            "BETWEEN ?1 AND ?2"
    )
    List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);

    @Query("SELECT NEW com.shopme.common.entity.order.OrderDetail(od.quantity, od.product.name, od.productCost, od.shippingCost, od.subTotal)" +
            "FROM OrderDetail od " +
            "WHERE od.order.orderTime " +
            "BETWEEN ?1 AND ?2"
    )
    List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);



}
