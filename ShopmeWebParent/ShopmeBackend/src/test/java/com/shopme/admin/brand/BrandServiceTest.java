package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTest {
    @MockBean
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    public void testCheckUniqueNewModeReturnDuplicate() {
        Integer id = 2;
        String name = "Acer";

         Brand brand = new Brand(1, name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(id, name);

        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniqueNewModeReturnOK() {
        String name = "Yokohama";

        Mockito.when(brandRepository.findByName(name)).thenReturn(null);

        String result = brandService.checkUnique(2, name);

        assertThat(result).isEqualTo("OK");
    }
}
