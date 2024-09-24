package cz.raadost.service.messanger;

import lombok.Getter;

@Getter
public enum StaticMessages {
  WELCOME(
      "Ahoj, děkujeme ti za zájem o můj amatérský obsah.\nVyber si o jaký druh obsahu máš zájem:\n /video - naše amatérská videa\n /photos - naše amatérské fotky\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí "),
    CONTENT_SELECTED("Vybral jsi následující obsah: "),
  CONTENT_OUT_OF_BOUNDS(
      "Obsah s takovým indexem není k dispozici.\n\nVyber si o jaký druh obsahu máš zájem:\n /video - naše amatérská videa\n /photos - naše amatérské fotky\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí."),
    THANKS_MESSAGE("Děkujeme za zaplacení. %s platbu ověří a spoji se s tebou na Telegramu."),
    CONTACT_USER("Ověř platbu a uživatele kontaktuj!"),
    USER_WILL_CONTACT_YOU("Uživatel nemá vyplněné username, dostal informaci, že tě má kontaktovat přímo na profilu administrátora a napsat ti jakou dal poznámku k platbě !"),
  NO_USERNAME_MESSAGE(
      "Děkujeme za zaplacení. Na tvém účtu nemáš vyplněné username, prosím napiš zprávu uživateli @%s a řekni ju co jsi vyplnil do poznámky k platbě, aby mohla platbu ověřit."),
    PAYMENT_GUIDE("Až budou peníze poslané napiš "),
    PICK_CONTENT("Níže si prosím klikem na číslo vyber požadovaný obsah."),
  INVALID_REQUEST(
      "Této odpovědí nerozumím :(.\n\nVyber si o jaký druh obsahu máš zájem:\n /video - naše amatérská videa\n /photos - naše amatérské fotky\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí ");

    private final String message;

    StaticMessages(String message) {
        this.message = message;
    }
}

