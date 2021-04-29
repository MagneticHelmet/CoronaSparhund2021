package io.javabrains.coronavirussparhund.controllers;

import io.javabrains.coronavirussparhund.models.RegionCase;
import io.javabrains.coronavirussparhund.services.CoronaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaDataService coronaDataService;

    @GetMapping("/")
    public String home(Model model){
        coronaDataService.fetchAndReturnExcelData();

            /* Put the biggest list first, this if the PosRegionCase is bigger, the size of the smallest list is necessary
            for the loop in thymeleaf, due to index out of bound exception*/
            List<List<RegionCase>> allSortedCases = coronaDataService.fetchAndReturnExcelData();
            model.addAttribute( "firstList", allSortedCases.get(0));
            model.addAttribute("secondList", allSortedCases.get(1));

            int secondListLength = allSortedCases.get(1).size();
            model.addAttribute("secondListLength", secondListLength);


        /* sum up the cases, both this and last weeks cases, then parse it to the model*/
        List<RegionCase> allCases = new ArrayList<RegionCase>(allSortedCases.get(0));
        allCases.addAll(allSortedCases.get(1));
        int totalThisWeek = allCases.stream().mapToInt(RegionCase -> RegionCase.getCasesThisWeek()).sum();
        int totalLastWeek = allCases.stream().mapToInt(RegionCase -> RegionCase.getCasesLastWeek()).sum();
        float totalDiffTemp = (((float)totalThisWeek-(float)totalLastWeek)/totalLastWeek)*100;
        int totalDiff = (int)totalDiffTemp;
        model.addAttribute( "totalThisWeek", totalThisWeek);
        model.addAttribute("totalLastWeek", totalLastWeek);
        model.addAttribute("totalDiff", totalDiff);
        return "home";
    }
}
