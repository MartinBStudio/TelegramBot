package cz.raadost.service.localization;

import lombok.Getter;

@Getter
public enum StaticMessages {
  WELCOME(
      "Ahoj, děkujeme ti za zájem o můj amatérský obsah.\nVyber si o jaký druh obsahu máš zájem:\n /video - naše amatérská videa\n /photos - naše amatérské fotky\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí ","Hello"),
  CONTENT_SELECTED("Vybral jsi následující obsah: ","You choose following content:"),
  CONTENT_OUT_OF_BOUNDS(
      "Obsah s takovým indexem není k dispozici.\n\nVyber si o jaký druh obsahu máš zájem:\n /video - naše amatérská videa\n /photos - naše amatérské fotky\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí.","Content with following index %s is not found."),
  THANKS_MESSAGE("Děkujeme za zaplacení. %s platbu ověří a spoji se s tebou na Telegramu.","Thank you for your payment. %s will verify payment and get in touch with you."),
  CONTACT_USER("Ověř platbu a uživatele kontaktuj!","You said he paid, so please contact him !"),
  USER_WILL_CONTACT_YOU(
      "Uživatel nemá vyplněné username, dostal informaci, že tě má kontaktovat přímo na profilu administrátora a napsat ti jakou dal poznámku k platbě !","User not using username in Telegram, I told him to contact you directly on your admin profile."),
  NO_USERNAME_MESSAGE(
      "Děkujeme za zaplacení. Na tvém účtu nemáš vyplněné username, prosím napiš zprávu uživateli @%s a řekni ju co jsi vyplnil do poznámky k platbě, aby mohla platbu ověřit.","Thank you for your payment, please contact @%s and tell her your payment note.She will then provide content for you."),
  PAYMENT_GUIDE("Až budou peníze poslané napiš ","After you send the money please use following command "),
  PICK_CONTENT("Níže si prosím klikem na číslo vyber požadovaný obsah.","Here, by clicking on index select requested content."),
  INVALID_REQUEST(
      "Této odpovědí nerozumím :(.\n\nVyber si o jaký druh obsahu máš zájem:\n /video - naše amatérská videa\n /photos - naše amatérské fotky\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí ","I don't understand this message. :(");

  private final String czechMessage;
  private final String englishMessage;




  StaticMessages(String czechMessage, String englishMessage) {
    this.czechMessage = czechMessage;
    this.englishMessage = englishMessage;
  }
}
