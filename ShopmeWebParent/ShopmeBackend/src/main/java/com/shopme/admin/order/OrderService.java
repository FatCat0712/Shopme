package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    public final static int ORDER_PER_PAGE = 10;

    private final OrderRepository orderRepository;
    private final CountryRepository countryRepository;


   @Autowired
    public OrderService(OrderRepository orderRepository, CountryRepository countryRepository) {
        this.orderRepository = orderRepository;
        this.countryRepository = countryRepository;
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

    public List<Country> listAllCountries() {
       return countryRepository.findAllByOrderByNameAsc();
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


    public void save(Order orderInForm) {
        Order order = orderRepository.findById(orderInForm.getId()).get();
        orderInForm.setOrderTime(order.getOrderTime());
        orderInForm.setCustomer(order.getCustomer());
        orderRepository.save(orderInForm);

    }

    public void updateStatus(Integer orderId, String status) {
       Order orderIdDB = orderRepository.findById(orderId).get();
        OrderStatus statusToUpdate = OrderStatus.valueOf(status);
        if(!orderIdDB.hasStatus(statusToUpdate)) {
            List<OrderTrack> orderTracks = orderIdDB.getOrderTracks();
            OrderTrack track = new OrderTrack();

            track.setStatus(statusToUpdate);
            track.setUpdatedTime(new Date());
            track.setNotes(statusToUpdate.defaultDescription());
            track.setOrder(orderIdDB);


            orderTracks.add(track);
            orderIdDB.setOrderStatus(statusToUpdate);

            orderRepository.save(orderIdDB);

        }
    }
}
