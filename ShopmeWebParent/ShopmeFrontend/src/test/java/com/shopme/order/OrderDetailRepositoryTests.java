package com.shopme.order;

import com.shopme.common.entity.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderDetailRepositoryTests {
    private final OrderDetailRepository repo;

    @Autowired
    public OrderDetailRepositoryTests(OrderDetailRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testCountByProductAndCustomerAndOrderStatus() {
        Integer productId = 106;
        Integer customerId = 5;
        Long count = repo.countByProductAndCustomerAndOrderStatus(productId, customerId, OrderStatus.DELIVERED);
        assertThat(count).isGreaterThan(0);
    }



}
