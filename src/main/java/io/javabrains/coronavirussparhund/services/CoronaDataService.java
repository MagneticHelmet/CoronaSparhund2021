package io.javabrains.coronavirussparhund.services;

import io.javabrains.coronavirussparhund.models.RegionCase;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CoronaDataService {

    private List<RegionCase> posRegionCases = new ArrayList<RegionCase>(); // List of RegionCase's where the diff from last week is positive
    private List<RegionCase> negRegionCases = new ArrayList<RegionCase>(); // -----------------.----------------------------------- negative
    public List<RegionCase> getPosRegionCases() {
        return posRegionCases;
    }
    public List<RegionCase> getNegRegionCases() {
        return negRegionCases;
    }
    @Autowired
    ResourceLoader resourceLoader;
    @PostConstruct
    public void fetchExcelData() {
        try {
            InputStream inputStream = resourceLoader.getResource("classpath:Folkhalsomyndigheten_Covid19.xlsx").getInputStream();
            //inputStream = new FileInputStream(excelFilePath);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet worksheet = workbook.getSheetAt(6); // getting worksheet from the the right Excel page.
            String compareString = ""; // will be used to compare Lan (column 2) in the Excel page, When a change is happening we are at the row of the most recent reported cases.

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = worksheet.getRow(i);
                String tempRegionString = row.getCell(2).getStringCellValue();

                if (compareString.isEmpty()){
                    /* at first row*/
                    compareString = tempRegionString;
                }

                if (!(compareString.equals(tempRegionString))) {
                    /* if tempRegionString has gain a new value, then we are at the row of the oldest reported cases.
                    * We now need to create a new object with data from last 2 rows which are the most recent reported cases */

                    row = worksheet.getRow(i - 2);
                    int lastWeek = (int) row.getCell(3).getNumericCellValue();
                    row = worksheet.getRow(i - 1);
                    int thisWeek = (int) row.getCell(3).getNumericCellValue();
                    RegionCase regionCase = new RegionCase(compareString, thisWeek, lastWeek);
                    compareString = tempRegionString;

                    if (regionCase.getDiffFactor() >= 0) {
                        /* update lists depending on diff*/
                        posRegionCases.add(regionCase);
                    }
                    else{negRegionCases.add(regionCase);}

                }
                if (i == worksheet.getPhysicalNumberOfRows() -1){
                    /*last row of excel file, here the cases are found in current and last row*/
                    row = worksheet.getRow(i-1);
                    int lastWeek = (int) row.getCell(3).getNumericCellValue();
                    row = worksheet.getRow(i);
                    int thisWeek = (int) row.getCell(3).getNumericCellValue();
                    RegionCase regionCase = new RegionCase(compareString, thisWeek, lastWeek);

                    if (regionCase.getDiffFactor() >= 0) {
                        /* update list depending on what kind of diff*/
                        posRegionCases.add(regionCase);
                    }
                    else{ negRegionCases.add(regionCase);}

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Collections.sort(posRegionCases, RegionCase.descending);
        Collections.sort(negRegionCases, RegionCase.ascending);
    }
}
