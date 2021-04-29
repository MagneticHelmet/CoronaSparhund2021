package io.javabrains.coronavirussparhund.services;
import io.javabrains.coronavirussparhund.models.RegionCase;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CoronaDataService {

    @Autowired
    ResourceLoader resourceLoader;
    public List<List<RegionCase>> fetchAndReturnExcelData() {
        List<RegionCase> posDiffCases = new ArrayList<RegionCase>();
        List<RegionCase> negDiffCases = new ArrayList<RegionCase>();
        try {
            InputStream inputStream = resourceLoader.getResource("classpath:Folkhalsomyndigheten_Covid19.xlsx").getInputStream();
            //inputStream = new FileInputStream(excelFilePath);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet worksheet = workbook.getSheetAt(6); // getting the  7nth worksheet from the the Excel file.
            String compareString = ""; // will be used to compare Lan (column 2) in the Excel page, When a change is happening we are at the row of the most recent reported cases.

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = worksheet.getRow(i);
                String tempRegionString = row.getCell(2).getStringCellValue();

                if (compareString.isEmpty()){
                    /* at first row*/
                    compareString = tempRegionString;
                }

                else if (i == worksheet.getPhysicalNumberOfRows() -1){
                    /*last row of the excel file, here the cases are found in current and last row*/
                    addRegionCase(i +1, worksheet, posDiffCases, negDiffCases, compareString);
                }

                else if (!(compareString.equals(tempRegionString))) {
                    /* if tempRegionString has gain a new value, then we are at the row of the oldest reported cases.
                    * We now need to create a new object with data from last 2 rows which are the most recent reported cases */
                    addRegionCase(i,worksheet, posDiffCases,negDiffCases,compareString);
                    compareString = tempRegionString;

                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        /* sort both lists in with biggest change on top*/
        Collections.sort(posDiffCases, RegionCase.descending);
        Collections.sort(negDiffCases, RegionCase.ascending);
        List<List<RegionCase>> sortedLists = sortListsBySize(posDiffCases, negDiffCases);
        return sortedLists;
    }


    public void addRegionCase(int index, XSSFSheet worksheet, List<RegionCase> posDiffCases, List<RegionCase> negDiffCases, String compareString){
        XSSFRow row = worksheet.getRow(index - 2);
        int lastWeek = (int) row.getCell(3).getNumericCellValue();
        row = worksheet.getRow(index - 1);
        int thisWeek = (int) row.getCell(3).getNumericCellValue();
        RegionCase regionCase = new RegionCase(compareString, thisWeek, lastWeek);

        if (regionCase.getDiffFactor() >= 0) {
            /*update lists depending on diff*/
            posDiffCases.add(regionCase);
        }
        else{negDiffCases.add(regionCase);}

    }
    public List<List<RegionCase>> sortListsBySize(List<RegionCase> posDiffCases, List<RegionCase> negDiffCases){
        /* Create a List which we put both lists in order by list size*/
        List<List<RegionCase>> sortedLists = new ArrayList<>();
        if (posDiffCases.size() >= negDiffCases.size()){
            sortedLists.add(posDiffCases);
            sortedLists.add(negDiffCases);
            }
        else {
            sortedLists.add(negDiffCases);
            sortedLists.add(posDiffCases);
        }
            return sortedLists;


    }

}
