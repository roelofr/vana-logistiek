# Vana LogistiekApp

De Vana LogistiekApp ("Penis Logistiekapp") is een ticketingsysteem voor logistiekers op Castlefest. Het primaire doel
van de applicatie is assisteren met de communicatie tussen de vrijwilligers in het veld, in de CP en om een naslagwerk
te zijn voor organisatie en coördinatie.

## Rollen

We erkennen 4 soorten gebruikers:

- Gebruikers
- Wijkhouders
- Centrale Post
- Coördinator

Het use-case diagram is als volgt:

```plantuml
@startuml

:Gebruiker: as User
:Wijkhouders: as Wijkhouder
:Centrale Post: as CP
:Coördinator: as Co

User <|-- Wijkhouder
Wijkhouder <|-- CP
CP <|-- Co

User -> (Bekijken Tickets)
User -> (Reageren op Tickets)
User -> (Bekijken standhouders)
User -> (Bekijken gebruikers)

Wijkhouder -> (Aanmaken Tickets)
Wijkhouder -> (Bekijken Wijkchat)
Wijkhouder -> (Reageren op Wijkchat)

CP -> (Bekijken alle Wijkchats)
CP -> (Toewijzen tickets)
CP -> (Sluiten Tickets)

Co -> (Bewerken gebruikers)
Co -> (Bewerken standhouders)
Co -> (Verwijderen tickets)
Co -> (Exporteren tickets)
```

## Domains

Om de applicatie beheersbaar te houden, is besloten dat eigenlijk alle communicatievormen
chats zijn. Alle chats worden beheerd in het Chat-domein, waarmee andere domeinen praten.

Verdere domeinen zijn vaak gekoppeld aan het Chat-domein. Deze andere domeinen zijn:

- **Tickets**, die informatie over de afhandeling van een ticket vasthouden (wie gaat het over, wie
heeft het gemeld, wie heeft er een actie, is het opgelost?)
- **Wijk** die informatie over wijken bevatten: welke standhouders staan er in, welke gebruikersgroepen werken in deze groepen en welke chat hoort er bij?
- **Standhouders**, met één service die de standhouders kan aangeven aan de hand van ID of nummer.

### Shared Domain

Het shared domein bevat models en services voor globaal-gekoppelde items. Hierbij gaat het om de
volgende modellen:

- Gebruikers als `User`
- Gebruikersgroepen als `Group`

### Chat-domain

Het _Chat_ domein bevat chats, berichten en acties in chat ("Attachments"), en deelnemers (User of Group). Een chat
heeft in beginsel een relatie, en chats zijn enkel op ID op te vragen.

```plantuml
@startuml

entity Chat {
    +id: int
    +title: string
    +label: string
    +type: ChatType
    +open: boolean
    +created_at: LocalDateTime
    +updated_at: LocalDateTime
}

entity ChatAttachment {
    +id: int
    +chat_id: int
    +type: string
    +user_id: int
    +group_id: int
}

metaclass GroupedChatAttachment {
    +batch_id: string
}

entity ChatMessage {
    +message: string
}

entity ChatMedia {
    path: string
    filename: string
    batch_id: string
}

Chat *- ChatAttachment

ChatAttachment <|-- GroupedChatAttachment
GroupedChatAttachment <|-- ChatMessage
GroupedChatAttachment <|-- ChatMedia

```
