package cz.raadost.model;

import lombok.Getter;


public enum ContentData {
    BLANKA24_PREVIEW(
            1,
            "Blanka24 Mikulášské PREVIEW.",
            "Video",
            "Umelecké 1:55",
            199,
            "https://www.google.com"
    ),
    MIKULASSKE_HONENI(
            2,
            "Mikulášské honění a kouření.",
            "Video",
            "Honění / Kouření - 3:49",
            249,
            "https://www.seznam.com"
    ),
    BALICEK_FOTKY(
            3,
            "Fotky z dovolene",
            "Fotky",
            "150Ks",
            500,
            "https://www.idnes.com"
    ),;
@Getter
    private final int index;
    @Getter
    private final String name;
    @Getter
    private final String type;
    @Getter
    private final String description;
    @Getter
    private final int price;
    @Getter
    private final String url;

    ContentData(int index, String name, String type, String description , int price, String url) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
        this.url = url;
    }
    public static ContentData findByStringNumber(String stringNumber) {
        // Parse the string to an integer
        int number;
        try {
            number = Integer.parseInt(stringNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + stringNumber);
        }

        // Search for the corresponding enum based on the number
        for (ContentData data : ContentData.values()) {
            if (data.getIndex() == number) { // Assuming 'getNumber()' returns 'cislo'
                return data;
            }
        }

        throw new IllegalArgumentException("No content data found for number: " + number);
    }


}
