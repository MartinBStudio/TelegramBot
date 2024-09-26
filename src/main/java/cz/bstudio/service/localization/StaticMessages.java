package cz.bstudio.service.localization;

import lombok.Getter;

@Getter
public enum StaticMessages {
  WELCOME(
      "Ahoj, děkujeme ti za zájem o můj obsah.\n\n Celkový počet: %s",
      "Hello, thank you for interest in my content.\n\nTotal amount: %s"),
  CONTENT_TYPES(
      "Vyber si o jaký druh obsahu máš zájem.\n\n/all - všechen obsah\n /video - naše amatérská videa\n /photos - naše amatérské fotky\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí ",
      "Choose what type you want to buy.\n\n/all - all content\n /video - our amateur videos\n /photos - our amateur photos\n /special - videos and photos on request just for you\n /bundle - photo or video packages"),
  CONTENT_SELECTED("Vybral jsi následující obsah: ","You choose following content:"),
  NO_AVAILABLE_CONTENT("Prodejce bohužel zatím nenahrál žádný obsah. :(","Seller did not add any content yet :(."),
  CONTENT_OUT_OF_BOUNDS(
      "Obsah s takovým indexem není k dispozici.","Content with following index is not available."),
  THANKS_MESSAGE("Děkujeme za zaplacení. %s platbu ověří a spoji se s tebou na Telegramu.","Thank you for your payment. %s will verify payment and get in touch with you."),
  CONTACT_USER("Ověř platbu a uživatele kontaktuj!","You said he paid, so please contact him !"),
  USER_WILL_CONTACT_YOU(
      "Uživatel nemá vyplněné username, dostal informaci, že tě má kontaktovat přímo na profilu administrátora a napsat ti jakou dal poznámku k platbě !","User not using username in Telegram, I told him to contact you directly on your admin profile."),
  NO_USERNAME_MESSAGE(
      "Děkujeme za zaplacení. Na tvém účtu nemáš vyplněné username, prosím napiš zprávu uživateli @%s a řekni ju co jsi vyplnil do poznámky k platbě, aby mohla platbu ověřit.","Thank you for your payment, please contact @%s and tell her your payment note.She will then provide content for you."),
  PAYMENT_GUIDE("Až budou peníze poslané napiš ","After you send the money please use following command "),
  PICK_CONTENT("Níže si prosím klikem na číslo vyber požadovaný obsah.","Here, by clicking on index select requested content."),
  NOTIFICATION_DETAILS(
      "Username - %s\nObsah - [%s] %s\nČástka - %sCZK\nPoznámka k platbě - %s\n\n%s",
      "Username - %s\nContent - [%s] %s\nPrice -%s euro\nPayment note - %s\n\n%s"),
  CONTENT_DETAILS(
      " %s\n\n%s\n DRUH - %s\n POPIS - %s\n CENA - %sCZK\n\nPLATBA\n %s\n %s\n POZNÁMKA K PLATBĚ - %s\n\n%s%s",
      " %s\n\n%s\n TYPE - %s\n DESCRIPTION - %s\n PRICE - %sEURO\n\nPAYMENT\n %s\n %s\n PAYMENT NOTE - %s\n\n%s%s"),
  INVALID_REQUEST(
      "Této odpovědí nerozumím :(.\n\nVyber si o jaký druh obsahu máš zájem:\n /video - naše amatérská videa\n /photos - naše amatérské fotky\n /special - videa a fotky na přání jen pro tebe\n /bundle - balíčky fotek nebo videí ","I don't understand this message. :(");

  private final String czechMessage;
  private final String englishMessage;




  StaticMessages(String czechMessage, String englishMessage) {
    this.czechMessage = czechMessage;
    this.englishMessage = englishMessage;
  }
}
