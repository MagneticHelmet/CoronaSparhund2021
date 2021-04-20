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

        if(coronaDataService.getPosRegionCases().size() > coronaDataService.getNegRegionCases().size()){
            /* Put the biggest first, this if the PosRegionCase is bigger, the size of the other list is nessecary
            for the loop in thymeleaf to avoid index out of bound exception
             */
            model.addAttribute( "firstList", coronaDataService.getPosRegionCases());
            model.addAttribute("secondList", coronaDataService.getNegRegionCases());

            int secondListLength = coronaDataService.getNegRegionCases().size();
            model.addAttribute("secondListLength", secondListLength);
        }
        else{
            model.addAttribute( "firstList", coronaDataService.getNegRegionCases());
            model.addAttribute("secondList", coronaDataService.getPosRegionCases());

            int secondListLength = coronaDataService.getPosRegionCases().size();
            model.addAttribute("secondListLength", secondListLength);
        }

        /* sum up the cases, both this and last week and parse it on the model*/
        List<RegionCase> totalRegionCases = new ArrayList<RegionCase>(coronaDataService.getPosRegionCases());
        totalRegionCases.addAll(coronaDataService.getNegRegionCases());
        int totalThisWeek = totalRegionCases.stream().mapToInt(RegionCase -> RegionCase.getCasesThisWeek()).sum();
        int totalLastWeek = totalRegionCases.stream().mapToInt(RegionCase -> RegionCase.getCasesLastWeek()).sum();
        float totalDiffTemp = (((float)totalThisWeek-(float)totalLastWeek)/totalLastWeek)*100;
        int totalDiff = (int)totalDiffTemp;
        model.addAttribute( "totalThisWeek", totalThisWeek);
        model.addAttribute("totalLastWeek", totalLastWeek);
        model.addAttribute("totalDiff", totalDiff);

        return "home";
    }
}
