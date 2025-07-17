package com.shopme.admin.customer.export;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Customer;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CustomerCsvExporter extends AbstractExporter {
    public void export(List<Customer> listCustomers, HttpServletResponse response) throws IOException {

        super.setResponseHeader("customers", "text/csv", ".csv", response);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"No", "E-mail", "First Name", "Last Name", "Phone Number", "Address Line 1", "City", "State", "Country", "Postal Code"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName","phoneNumber", "addressLine1","city", "state","country", "postalCode"};

        csvWriter.writeHeader(csvHeader);

        for (Customer customer: listCustomers) {
            csvWriter.write(customer, fieldMapping);
        }


        csvWriter.close();
    }
}
