# E-shop

Pro spuštění aplikace budete potřebovat:
* Libovolné IDE (IntelliJ IDEA, VS Code, ...)
* [Docker](https://www.docker.com/)

## Stažení a spuštění aplikace
1. Na stránce GitHub se nachází tlačítko `<> Code`, z kterého si lze zdrojový kód stáhnout.
2. Stáhněte si zdrojový kód do libovolné složky.
3. Otevřete složku přes IDE a napište do konzole `docker compose up -d`.
    * Prvotní sestavení aplikace trvá v řádech minut. (Některé mikroslužby se spouští až po 1 minutě)
    * V případě, že změníte soubory je nutný nový build, proto `docker compose up -d --build`.
    * Aplikaci lze zastavit příkazem `docker compose down`.

## Nastavení
Nastavení mikroslužeb se nachází v `config-server`, kde jsou všechny **.yml** soubory s názvy mikroslužeb. Cesta k souborům:
```
/
├── .idea/
├──  initdb/
├──  services/
│    └── config-server/
│        └── src/
│            └── main/
│                └── resources/
│                    └── configurations/
│                        └── .yml soubory (Pro všechy services!)
│    └── ...ostatní/
├──  store-ui/
├──  uploads/
├──  docker-compose.yml
```

**1. Nastavení platební brány Stripe**
> Na stránce [Stripe](https://stripe.com/en-cz) je nutné si založit uživatelský účet.

* Dashboard obsahuje *Publishable* a *Secret keys*, které je nutné nahradit ve složce **payment-service.yml**, která je v configurations.
* Webhook je nutné vytvořit na stránce [Webhooks](https://dashboard.stripe.com/test/workbench/webhooks/create), kdy je nutné vybrat eventy `checkout.session.completed` a `checkout.session.expired`. Po kliknutí na tlačítko pokračování je nutné vybrat **Webhook endpoint**. Následně zvolte URL, na kterou bude Stripe posílat informace. Formát URL je `vaše-adresa:8060/api/v1/stripe-webhook`.
  * Pokud se jedná o development na lokálním hostovi, doporučuji použít **ngrok** pro sdílení portu. (Sdílejte pouze `ngrok http 8060`. Vygenerované adrese přidejte `/api/v1/stripe-webhook`.)

**3. Nastavení e-mailu**
> Pro využití e-mailových zpráv je nutné mít vlastní e-mailovou adresu.

* Složka **notification-service.yml** obsahuje data, nutná pro odesílání e-mailových zpráv.
```
mail:
  sender:
    host: replace-with-host # For example: smtp.seznam.cz
    port: replace-with-port # For example: 587
    username: replace-with-username # For example: user@seznam.cz
    password: replace-with-password
```
* Z uvedeného kódu výše lze vidět, že je nutné zadat host, port, username (e-mail) a jeho heslo.

**2. Nastavení secret-key pro uživatele**
* Aby nebylo možné zjistit hesla, je nutné ve složce **user-service.yml** změnit *secret-key*.
* Na stránce [ACTE](https://acte.ltd/utils/randomkeygen) si lze vygenerovat klíč. Pro zabezpečení je použit HMA-256, proto zkopírujte *Encryption key 256* a nahraďte jej za stávající klíč.

## Použité technologie
###### Front-end
 * Angular

###### Back-end
 * Spring Boot; Microservices; Kafka; PostgreSQL; MongoDB

###### Ostatní
 * Docker; Stripe API
