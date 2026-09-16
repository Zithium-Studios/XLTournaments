package net.zithium.tournaments.utility.universal;

public enum PlantAge {

    BAMBOO(0, "BAMBOO"), // Note: bamboo also has an internal 'stage' (0-1) and leaf-size 'age' (0-15)
    // that aren't captured by a single maxAge; flagged for review.
    BEETROOT(3, "BEETROOT", "BEETROOTS", "BEETROOT_SEEDS"),
    CACTUS(0, "CACTUS"), // Internal age (0-15) drives a hidden growth timer, not a visual stage.
    CARROT(7, "CARROT", "CARROTS"),
    CAVE_VINES(25, "CAVE_VINES"),
    CAVE_VINES_PLANT(25, "CAVE_VINES_PLANT"),
    CHORUS_FLOWER(5, "CHORUS_FLOWER"), // Split from CHORUS_PLANT: only the flower is Ageable (0-5).
    CHORUS_PLANT(0, "CHORUS_PLANT"),   // The plant/stem itself has no age property.
    COCOA(2, "COCOA", "COCOA_BEANS"),
    KELP(25, "KELP"),
    KELP_PLANT(25, "KELP_PLANT"),
    MANGROVE_PROPAGULE(4, "MANGROVE_PROPAGULE"),
    MELON(-1, "MELON", "MELON_BLOCK"),
    MELON_STEM(7, "MELON_STEM"),
    ATTACHED_MELON_STEM(0, "ATTACHED_MELON_STEM"),
    NETHER_WART(3, "NETHER_WART", "NETHER_STALK"),
    PITCHER_CROP(4, "PITCHER_CROP"),
    POTATO(7, "POTATO", "POTATOES"),
    PUMPKIN(-1, "PUMPKIN"),
    PUMPKIN_STEM(7, "PUMPKIN_STEM"),
    ATTACHED_PUMPKIN_STEM(0, "ATTACHED_PUMPKIN_STEM"),
    SUGAR_CANE(0, "SUGAR_CANE", "SUGAR_CANE_BLOCK"), // Internal age (0-15) drives a hidden growth timer.
    SWEET_BERRY_BUSH(3, "SWEET_BERRY_BUSH"),
    TORCHFLOWER_CROP(2, "TORCHFLOWER_CROP"),
    TWISTING_VINES(25, "TWISTING_VINES"),
    TWISTING_VINES_PLANT(25, "TWISTING_VINES_PLANT"),
    WEEPING_VINES(25, "WEEPING_VINES"),
    WEEPING_VINES_PLANT(25, "WEEPING_VINES_PLANT"),
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