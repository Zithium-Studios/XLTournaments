package net.zithium.tournaments.utility.universal;

public enum PlantAge {

    BAMBOO(0, "BAMBOO"),
    BEETROOT(3, "BEETROOT", "BEETROOTS", "BEETROOT_SEEDS"),
    CACTUS(0, "CACTUS"),
    CARROT(7, "CARROT", "CARROTS"),
    CHORUS_PLANT(0, "CHORUS_PLANT", "CHORUS_FLOWER"),
    COCOA(2, "COCOA", "COCOA_BEANS"),
    MELON(-1, "MELON", "MELON_BLOCK"),
    NETHER_WART(3, "NETHER_WART", "NETHER_STALK"),
    POTATO(7, "POTATO", "POTATOES"),
    PUMPKIN(-1, "PUMPKIN"),
    SUGAR_CANE(0, "SUGAR_CANE", "SUGAR_CANE_BLOCK"),
    SWEET_BERRY_BUSH(3, "SWEET_BERRY_BUSH"),
    WHEAT(7, "WHEAT", "CROPS", "WHEAT_SEEDS"),
    UNKNOWN(-1); // No plant types associated with UNKNOWN

    private final int maxAge;
    private final String[] plantTypes;

    PlantAge(int maxAge, String... plantTypes) {
        this.maxAge = maxAge;
        this.plantTypes = plantTypes;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public String[] getPlantTypes() {
        return plantTypes;
    }
}