import java.util.*;

/**
 * Deterministic regression harness: source methods are injected directly from
 * production CitizenManager and AnimalManager, not rewritten in the tests.
 */
public class ColonyAllocationRegression {
    interface ICitizenData {
        void update(int rate);
    }
    interface IAnimalData {
        void update(int rate);
    }
    static final class CitizenManager {
        final Map<Integer, ICitizenData> citizens = new HashMap<>();
        final List<ICitizenData> tickCitizenSnapshot = new ArrayList<>();
        final Random random = new Random(789);
        // PROD_CITIZEN_TICK
        // PROD_CITIZEN_RANDOM
    }
    static final class AnimalManager {
        final Map<Integer, IAnimalData> animalMap = new HashMap<>();
        final List<IAnimalData> tickAnimalSnapshot = new ArrayList<>();
        // PROD_ANIMAL_TICK
    }
    static int checks;
    static void expect(final String label, final boolean condition) {
        if (!condition) throw new AssertionError(label);
        checks++;
    }
    static void checkCitizenPopulation(final int population) {
        final CitizenManager manager = new CitizenManager();
        final int[] calls = new int[population];
        for (int id = 0; id < population; id++) {
            final int currentId = id;
            manager.citizens.put(id, rate -> {
                calls[currentId]++;
                if (currentId == 0) manager.citizens.remove(currentId);
            });
        }
        final List<ICitizenData> originalBuffer = manager.tickCitizenSnapshot;
        manager.tickCitizenData(5);
        expect(population + " citizens updated despite map removal",
            Arrays.stream(calls).sum() == population);
        expect(population + " citizen registry drops one", manager.citizens.size() == population - 1);
        expect(population + " citizen tick buffer does not retain references",
            manager.tickCitizenSnapshot.isEmpty());
        manager.tickCitizenData(5);
        expect(population + " second tick only covers survivors",
            Arrays.stream(calls).sum() == 2 * population - 1);
        expect(population + " same backing buffer instance",
            originalBuffer == manager.tickCitizenSnapshot);
        for (int i = 0; i < population; i++) {
            expect(population + " random picks only living citizens",
                manager.citizens.containsValue(manager.getRandomCitizen()));
        }
        manager.citizens.clear();
        boolean threw = false;
        try { manager.getRandomCitizen(); }
        catch (IllegalArgumentException ex) { threw = true; }
        expect(population + " empty registry throws as before", threw);
    }
    static void checkAnimals(final int population) {
        final AnimalManager manager = new AnimalManager();
        final int[] calls = new int[population];
        for (int id = 0; id < population; id++) {
            final int currentId = id;
            manager.animalMap.put(id, rate -> {
                calls[currentId]++;
                if (currentId == 0) manager.animalMap.remove(currentId);
            });
        }
        manager.tickAnimalData(20);
        expect(population + " animal snapshot safe under mutation",
            Arrays.stream(calls).sum() == population);
        expect(population + " animal tick buffer cleared",
            manager.tickAnimalSnapshot.isEmpty());
        manager.tickAnimalData(20);
        expect(population + " animal tick survivor count",
            Arrays.stream(calls).sum() == 2 * population - 1);
    }
    public static void main(String[] args) {
        for (int size : new int[] {1, 25, 500, 1000, 2000}) {
            if (size > 1) checkCitizenPopulation(size);
            checkAnimals(size);
        }
        System.out.println("PASS: " + checks + " regression assertions at populations up to 2,000.");
        System.out.println("NOTE: tests verify selection and snapshot lifecycle, not actual game RAM.");
    }
}
