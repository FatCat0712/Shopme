package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {
    public final static int ORDER_PER_PAGE = 10;

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();
        Sort sort;
        if(sortField.equals("destination")) {
            sort = Sort.by("country").and(Sort.by("state").and(Sort.by("city")));
        }
        else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDER_PER_PAGE, sort);
        Page<Order> page;
        if(keyword != null) {
            page = orderRepository.findAll(keyword, pageable);
        }
        else {
            page = orderRepository.findAll(pageable);
        }

        helper.updateModelAttribute(pageNum, page);
    }

    public Order get(Integer orderId) throws OrderNotFoundException {
       Optional<Order> order =  orderRepository.findById(orderId);
       if(order.isEmpty()) {
           throw new OrderNotFoundException("Could not find any order with ID " + orderId);
       }
       else {
           return order.get();
       }
    }

    public void delete(Integer orderId) throws OrderNotFoundException {
        Long countById = orderRepository.countById(orderId);
        if(countById == 0) {
            throw new OrderNotFoundException("Could not find any order with ID " + orderId);
        }
        else {
            orderRepository.deleteById(orderId);
        }
    }

}
