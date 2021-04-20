package io.javabrains.coronavirussparhund.models;

import java.text.DecimalFormat;
import java.util.Comparator;

public class RegionCase {

    private final String region;
    private final int casesThisWeek;
    private  final int casesLastWeek;
    private final double diffFactor;
    private final String diffPercentage;

    public RegionCase(String region, int casesThisWeek, int casesLastWeek){

        this.region = region;
        this.casesThisWeek = casesThisWeek;
        this.casesLastWeek = casesLastWeek;
        this.diffFactor = (double)(casesThisWeek - casesLastWeek)/casesLastWeek;
        DecimalFormat value = new DecimalFormat("#");
        this.diffPercentage = value.format(diffFactor * 100);
    }

    public String getRegion() {
        return region;
    }

    public int getCasesThisWeek() {
        return casesThisWeek;
    }

    public int getCasesLastWeek() {
        return casesLastWeek;
    }

    public double getDiffFactor() { return diffFactor; }

    public String getDiffPercentage() {
        return diffPercentage;
    }

    public static Comparator<RegionCase> descending = new Comparator<RegionCase>() {

        public int compare(RegionCase r1, RegionCase r2) {
            double diffOfCases = r1.getDiffFactor() - r2.getDiffFactor();
            if (diffOfCases > 0){return -1;}
            if (diffOfCases < 0){ return 1;}
            else{return 0;}
        }
    };

    public static Comparator<RegionCase> ascending = new Comparator<RegionCase>() {

        public int compare(RegionCase r1, RegionCase r2) {
            double diffOfCases = r1.getDiffFactor() - r2.getDiffFactor();
            if (diffOfCases > 0) {
                return 1;
            }
            if (diffOfCases < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    };
            @Override
    public String toString() {
        return "RegionCase{" +
                "lan='" + region + '\'' +
                ", casesThisWeek=" + casesThisWeek +
                ", casesLastWeek=" + casesLastWeek +
                ", diffFactor=" + diffFactor +
                '}';
    }


}
