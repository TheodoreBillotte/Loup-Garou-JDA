# Loup-Garou-JDA
Ce projet a pour but de recréer un jeu de loup-garou sur discord en utilisant l'API JDA.

## Installation
Pour installer le bot, il faut d'abord créer un bot sur le site de discord. 
Pour cela, il faut se rendre sur 
https://discordapp.com/developers/applications/me et créer une nouvelle application. 
Une fois l'application créée, il faut créer un bot. Pour cela, il faut aller dans l'onglet 
"Bot" et cliquer sur "Add Bot". Une fois le bot créé, il faut copier le token et le coller 
dans le fichier `TOKEN` dans le dossier `src/main/resources`.

Une fois le token copié, il faut compiler le projet. Pour cela, il vous faut 
maven et java 8. Pour compiler le projet, il faut être à la racine du projet et
lancer la commande `mvn clean install`. Une fois le projet compilé, vous trouverez
un fichier jar dans le dossier `target` du projet. Pour lancer le bot, il faut
lancer la commande `java -jar LGbot-1.0.jar`.

## Utilisation
Pour utiliser le bot, il faut l'inviter sur un serveur discord. Pour cela, il faut
aller sur https://discordapp.com/oauth2/authorize?client_id=CLIENT_ID&scope=bot&permissions=0
et remplacer CLIENT_ID par l'id du bot. Une fois le bot invité, il faut lui donner
les permissions nécessaires.

## Commandes
La seule commande que possède le bot est `/create <name>` qui permet de créer une partie
de Loup-Garou. Par la suite, tout est configurable directement avec un système
d'interfaces avec des boutons et des listes déroulantes.

## Fonctionnalités
Le bot permet de créer des parties de Loup-Garou. Il est possible de configurer
les rôles, la durée des différentes phases du jeu, le nombre de joueurs, etc.
Il est possible de jouer à plusieurs parties en même temps.

Le bot gère automatiquement tout ce qui a besoin d'être géré pour une partie 
de loup-garou comme la gestion des channels vocaux et textuels, la gestion des
permissions, etc.

Mais le plus important, c'est que le bot gère automatiquement les phases du jeu
et les actions des joueurs. Il est donc possible à tous les rôles de la 
partie d'interagir très facilement avec le bot, pour faire les actions que 
son rôle est censé faire en utilisant juste des messages avec interactions 
dans le chat du village directement, ce qui rend le jeu d'autant plus immersif.

## Rôles
Le bot permet de créer des parties avec les rôles suivants :
- Loup-Garou
- Villageois
- Voyante
- Sorcière
- Chasseur
- Cupidon
- Petite Fille
- Corbeau

## Auteur
Ce projet a été réalisé par [Théodore Billotte](https://github.com/TheodoreBillotte)
en tant que projet hub pour Epitech Montpellier.
