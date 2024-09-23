package cz.raadost.model;

import lombok.Getter;

@Getter
public enum StaticMessages {
    WELCOME("Ahoj, děkujeme ti za zájem o náš amatérský obsah.\nVyber si o jaký druh obsahu máš zájem:\n /video - naše amatérská videa\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí "),
    CONTENT_SELECTED("Vybral jsi následující obsah: "),
    PAYMENT_DETAILS("PLATBA \n BÚ - 3064826016/3030\n Revolut - kocicka1\n Poznámka k platbě - "),
    CONTENT_OUT_OF_BOUNDS("Obsah s takovým indexem není k dispozici, pro dostupný obsah napiš /obsah"),
    THANKS_MESSAGE("Děkujeme za zaplacení. Blanka platbu ověří a spoji se s tebou na Telegramu."),
    CONTACT_USER("Ověř platbu a uživatele kontaktuj!"),
    USER_WILL_CONTACT_YOU("Uživatel nemá vyplněné usernema, dostal informaci, že tě má kontaktovat přímo na profilu @raadost a napsat ti jakou dal poznámku k platbě !"),
    NO_USERNAME_MESSAGE("Děkujeme za zaplacení. Na tvém účtu nemáš vyplněné username, prosím napiš zprávu přímo Blance na uživatelie @raadost a řekni jí co jsi vyplnil do poznámky k platbě, aby mohla platbu ověřit."),
    PAYMENT_GUIDE("Až budou peníze poslané napiš "),
    PICK_CONTENT("Níže si prosím klikem na číslo vyber požadovaný obsah."),
    INVALID_REQUEST("Této odpovědí nerozumím :(.");

    private final String message;

    StaticMessages(String message) {
        this.message = message;
    }
}

