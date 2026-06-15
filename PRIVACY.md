# Politique de confidentialité — AsyncSupport (TicketBot)

**Dernière mise à jour : 15 juin 2026**

## 1. Introduction

AsyncSupport (le « Bot ») est un outil de gestion de tickets de modération pour le serveur Discord « Async - Community ». Cette politique de confidentialité décrit comment le Bot traite les données personnelles des utilisateurs.

## 2. Responsable du traitement

Le responsable du traitement est l'équipe de modération du serveur Discord « Async - Community ». Pour toute question relative à vos données, contactez la modération via le serveur : https://discord.gg/graven.

## 3. Données collectées

Le Bot collecte et traite les catégories de données suivantes :

| Catégorie | Données | Finalité |
|---|---|---|
| Identifiants Discord | ID utilisateur, nom d'utilisateur (username), tag (ex. `utilisateur#1234`), avatar URL | Création et gestion des tickets, identification dans les salons et rapports |
| Contenu des messages | Texte des messages, pièces jointes, embeds, horodatages | Transmission des messages entre l'utilisateur et la modération, archivage des tickets |
| Métadonnées de messages | Identifiants de messages, messages épinglés, réactions | Gestion des salons de ticket et accusés de réception |

## 4. Finalités du traitement

Les données sont traitées pour les seules finalités suivantes :

- **Ouverture et gestion des tickets** : permettre aux utilisateurs de contacter la modération et aux modérateurs de répondre.
- **Transmission des messages** : relayer les messages entre l'utilisateur (en message privé) et le salon de ticket (via webhook Discord).
- **Archivage des tickets** : à la fermeture d'un ticket, un rapport JSON contenant l'historique complet des échanges est envoyé dans un salon d'archives dédié.
- **Rétablissement des tickets après redémarrage** : le Bot recharge les tickets existants en lisant les sujets des salons textuels de la catégorie tickets.

## 5. Base légale du traitement (RGPD)

Le traitement est fondé sur :

- **L'intérêt légitime** (Article 6.1.f RGPD) : permettre la modération et le support utilisateur sur le serveur Discord.
- **Le consentement** (Article 6.1.a RGPD) : l'utilisateur initie volontairement l'ouverture d'un ticket via la commande `/ticket`.

## 6. Destinataires des données

Les données peuvent être transmises aux destinataires suivants :

- **Discord Inc.** : les messages transités par le Bot utilisent l'API Discord et les webhooks Discord. Consultez la [politique de confidentialité de Discord](https://discord.com/privacy).
- **Équipe de modération du serveur** : les modérateurs ayant accès aux salons de tickets et aux salons d'archives peuvent consulter l'ensemble des données échangées.
- **Service de visualisation de tickets (optionnel)** : si configuré, une URL vers le rapport JSON peut être générée via un service externe défini par `config.reader.base_url`.

## 7. Stockage et conservation

| Type de stockage | Durée de conservation |
|---|---|
| Mémoire vive (HashMap) | Durée de fonctionnement du Bot. Les tickets en cours sont perdus au redémarrage. |
| Salons Discord (tickets) | Durée d'ouverture du ticket. Les salons sont supprimés à la fermeture. |
| Rapports JSON (archives) | Durée indéterminée dans le salon d'archives Discord. La modération peut les supprimer manuellement. |
| Fichiers joints ré-hébergés | Durée indéterminée dans le salon d'attachements. La modération peut les supprimer manuellement. |

**Aucune base de données externe** n'est utilisée. Les données n'existent que dans l'infrastructure Discord et la mémoire du Bot.

## 8. Sécurité

- Le token du Bot est stocké dans un fichier de configuration local (`config.yml`) et n'est jamais exposé.
- Les messages sont transmis via l'API Discord chiffrée (TLS).
- Le Bot n'écrit pas de journalisation contenant les données personnelles des utilisateurs.

## 9. Vos droits (RGPD)

Conformément au RGPD, vous disposez des droits suivants :

- **Droit d'accès** : demandez à la modération une copie des données vous concernant.
- **Droit de rectification** : vos données Discord sont gérées via votre compte Discord.
- **Droit à l'effacement** : fermez votre ticket ou demandez à la modération de supprimer les rapports d'archives.
- **Droit à la limitation du traitement** : vous pouvez cesser d'utiliser le Bot en ne lançant pas la commande `/ticket`.
- **Droit à la portabilité** : demandez une copie de vos données archivées à la modération.
- **Droit d'opposition** : vous pouvez vous opposer au traitement en ne lançant pas la commande `/ticket`.

Pour exercer ces droits, contactez la modération du serveur Discord « Async - Community » : https://discord.gg/graven.

## 10. Transferts hors UE

Les données sont traitées via l'infrastructure Discord, qui peut impliquer des transferts de données hors de l'Union européenne. Discord s'appuie sur les clauses contractuelles types (CCT) de la Commission européenne pour ces transferts. Consultez la [politique de confidentialité de Discord](https://discord.com/privacy) pour plus d'informations.

## 11. Modifications de la politique

Cette politique peut être mise à jour à tout moment. La date de « Dernière mise à jour » en tête de document indique la version en vigueur.
