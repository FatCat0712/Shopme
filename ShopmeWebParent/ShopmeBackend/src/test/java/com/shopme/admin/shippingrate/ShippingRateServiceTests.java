package com.shopme.admin.shippingrate;

import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ShippingRateServiceTests {

    @Mock
    private ShippingRateRepository shippingRateRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ShippingRateService shippingRateService;

    @Test
    public void testCalculateShippingCost_NoRateFound() {
        Integer productId = 1;
        Integer countryId = 234;
        String state = "ABCDE";

        Mockito.when(shippingRateRepository.findByCountryAndState(countryId,state)).thenReturn(null);

        assertThrows(ShippingRateNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                shippingRateService.calculateShippingCost(productId, countryId, state);
            }
        });
    }

    @Test
    public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException {
        Integer productId = 1;
        Integer countryId = 234;
        String state = "New York";

        ShippingRate shippingRate = new ShippingRate();
        shippingRate.setRate(10f);

        Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(shippingRate);

        Product product = new Product();
        product.setWeight(5);
        product.setWidth(4);
        product.setHeight(3);
        product.setLength(8);

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);

        assertEquals(50, shippingCost);



    }




}
