package cz.raadost.model;

import lombok.Getter;

@Getter
public enum StaticMessages {
    WELCOME("Ahoj, děkujeme ti za zájem o náš amatérský obsah.\nPro seznam obsahu napiš /obsah"),
    CONTENT_SELECTED("Vybral jsi následující obsah: "),
    PAYMENT_DETAILS("PLATBA \n BÚ - 3064826016/3030\n Revolut - kocicka1\n Poznámka k platbě - "),
    CONTENT_OUT_OF_BOUNDS("Obsah s takovým indexem není k dispozici, pro dostupný obsah napiš /obsah"),
    THANKS_MESSAGE("Děkujeme za zaplacení. Blanka platbu ověří a spoji se s tebou na Telegramu."),
    PAYMENT_GUIDE("Až budou peníze poslané napiš /ZAPLACENO");

    private final String message;

    StaticMessages(String message) {
        this.message = message;
    }
}

