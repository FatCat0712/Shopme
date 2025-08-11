package com.shopme.order;

import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query("SELECT COUNT(od) " +
            "FROM OrderDetail od " +
            "JOIN OrderTrack ot " +
            "ON od.order.id = ot.order.id " +
            "WHERE od.product.id = ?1 " +
            "AND od.order.customer.id = ?2 " +
            "AND ot.status = ?3")
    Long countByProductAndCustomerAndOrderStatus(Integer productId, Integer customerId, OrderStatus status);
}
