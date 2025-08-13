package com.shopme.admin.question.export;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Question;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class QuestionCsvExporter extends AbstractExporter {
    public void export(List<Question> listQuestions, HttpServletResponse response) throws IOException {

        super.setResponseHeader("questions", "text/csv", ".csv", response);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Question ID", "Product", "Question", "Asker", "Ask Time", "Approved", "Answered By"};
        String[] fieldMapping = {"id", "product", "questionContent", "asker", "formattedAskTime" ,"approvalStatus", "answerer"};

        csvWriter.writeHeader(csvHeader);

        for (Question question: listQuestions) {
            csvWriter.write(question, fieldMapping);
        }

        csvWriter.close();
    }
}
