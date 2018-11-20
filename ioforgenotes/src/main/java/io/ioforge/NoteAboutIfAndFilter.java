package io.ioforge;

import io.ioforge.elements.SteelBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class NoteAboutIfAndFilter {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        //let's init
        List<SteelBar> steelBars = generateNewBatchOfSteelBars(10);
        steelBars.forEach(System.out::println);

        // let's do what we want to actually do in a traditional way
        // filterWithIf(steelBars);

        // let's do what we want to actually do in a J8 way
        filterWithStream(steelBars);
    }

    private static void filterWithStream(List<SteelBar> steelBars) {
        List<SteelBar> theGoodBarsStream = steelBars.stream()
                .filter(steelBar -> steelBar.getCarbonPercent() >= 1)
                .filter(steelBar -> steelBar.getCarbonPercent() <= 3)
                .map(steelBar -> stressSteelBar(steelBar))
                .filter(steelBar -> steelBar.getStrength() >= 90)
                .collect(toList());

        System.out.println();
        theGoodBarsStream.forEach(System.out::println);
    }

    private static void filterWithIf(List<SteelBar> steelBars) {
        List<SteelBar> theGoodBarsIF = new ArrayList<>();

        for (SteelBar steelBar : steelBars) {
            if (steelBar.getCarbonPercent() >= 1 && steelBar.getCarbonPercent() <= 3) {
                stressSteelBar(steelBar);
                if (steelBar.getStrength() >= 90) {
                    theGoodBarsIF.add(steelBar);
                }
            }
        }

        System.out.println();
        theGoodBarsIF.forEach(System.out::println);
    }

    private static List<SteelBar> generateNewBatchOfSteelBars(int bars) {
        List<SteelBar> steelBars = new ArrayList<>();

        for (int i = 0; i < bars; i++) {
            Integer remainingPercentage = 100;
            SteelBar steelBar = new SteelBar();
            steelBar.setAlloyBatch("batch_" + i);
            steelBar.setOtherMetalsPercent(RANDOM.nextInt(3));
            steelBar.setOtherNonMetalsPercent(RANDOM.nextInt(3));
            steelBar.setCarbonPercent(RANDOM.nextInt(4));
            steelBar.setStrength(RANDOM.nextInt(11) + 90);  //this will set a random strength between 90 and 100
            remainingPercentage -= steelBar.getOtherMetalsPercent();
            remainingPercentage -= steelBar.getOtherNonMetalsPercent();
            remainingPercentage -= steelBar.getCarbonPercent();

            steelBar.setIronPercent(remainingPercentage);

            steelBars.add(steelBar);
        }
        return steelBars;
    }

    private static SteelBar stressSteelBar(SteelBar steelBar) {
        steelBar.setStrength(steelBar.getStrength() - RANDOM.nextInt(11));
        return steelBar;
    }
}
