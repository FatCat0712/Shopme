package com.shopme.admin.brand.export;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.brand.Brand;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class BrandCsvExporter extends AbstractExporter {
    public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException {

        super.setResponseHeader("brands", "text/csv", ".csv", response);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Brand ID", "Brand Name", "Category"};
        String[] fieldMapping = {"id", "name", "categories"};

        csvWriter.writeHeader(csvHeader);

        for (Brand brand: listBrands) {
            csvWriter.write(brand, fieldMapping);
        }

        csvWriter.close();
    }
}
