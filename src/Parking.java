import java.util.ArrayList;
import java.util.Random;

public class Parking extends Simulation {
    private static Random randomK = new Random();
    private static Random randomPlace = new Random();

    private static ArrayList<Boolean> parkingPlaces;

    private static int n;
    private static int startPlace;
    private static int actualPlace;

    private static int result;
    private static int countPlaces = 0;

    private static ArrayList<Integer> placesFrequency;

    public Parking(int _n, int _startPlace) {
        n = _n;
        startPlace = _startPlace;
        parkingPlaces = new ArrayList<Boolean>(n);
        placesFrequency = new ArrayList<>(_n + 1);
    }

    @Override
    public void beforeReplications() {
        //vytvorenie prazdnych miest na parkovisku
        countPlaces = 0;
        for (int i = 0; i < n; i++) {
            parkingPlaces.add(false);
        }
        for (int i = 0; i < n + 1; i++) {
            placesFrequency.add(0);
        }
    }

    @Override
    public void beforeReplication() {
        //vyprazdnenie parkoviska
        for (int i = 0; i < n; i++) {
            parkingPlaces.set(i, false);
        }
        //generovanie poctu obsadenosti
        int k = (int)(randomK.nextDouble() * (n)) + 1;
        //obsadzovanie miest na parkovisku
        ArrayList<Integer> freePlaces = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            freePlaces.add(i);
        }
        for (int i = 0; i < k; i++) {
            int place = (int)(randomPlace.nextDouble() * freePlaces.size());//_randomPlace.nextInt(freePlaces.size());
            parkingPlaces.set(freePlaces.get(place), true);
            freePlaces.remove(place);
        }
        //nastavenie zaciatku prehladavania
        actualPlace = n - startPlace - 1;
    }

    @Override
    public void replication() {
        while (true) {
            //preskakujem nedostupne miesta
            if (actualPlace == 2 || actualPlace == 5 || actualPlace == 8) {
                actualPlace--;
            }
            //ziadne miesto na parkovisku
            if (actualPlace == -1) {
                result = 3 * n;
                return;
            }
            //najdene parkovacie miesto
            if (!parkingPlaces.get(actualPlace)) {
                result = actualPlace + 1;
                return;
            }
            //posun na dalsie miesto
            actualPlace--;
        }
    }

    @Override
    public void afterReplication() {
        //aplikacia vysledku
        //neukladat data ale poslet ich do gui
        countPlaces += result;
        if (result > placesFrequency.size()) {
            placesFrequency.set(placesFrequency.size() - 1, placesFrequency.get(placesFrequency.size() - 1) + 1);
        } else {
            placesFrequency.set(result - 1, placesFrequency.get(result - 1) + 1);
        }

        if (getActualReplication() > getPause() && (getActualReplication() - getPause()) % getJumpRepl() == 0) {
            guiListener.sendGraphValue(getActualReplication(), countPlaces);
            guiListener.sendHistogramData(placesFrequency);
        }
    }

    @Override
    public void afterReplications() {
        //vypis vysledkov
        System.out.println(countPlaces);
        System.out.println((double)countPlaces / getCountReplications());
    }
}
