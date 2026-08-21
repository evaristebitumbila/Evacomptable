# EVA Compta Consulting — Spring Boot + Thymeleaf + H2

Votre site d'origine (HTML/CSS/JS **inchangés**), converti en application Spring Boot,
avec un back office qui gère les **formations (cartes 3D)**, leurs **modules** et
**sessions**, plus un **bouton de connexion** dans la barre de navigation.

## Ce qui a été fait

- **Vos pages, votre design, vos textes** : repris tels quels (index, services,
  cabinet, faq, contact). Vos fichiers `style.css`, `main.js`, `translations.js`,
  `fonts.css`, `fontawesome.css`, `logo.png` sont **inchangés**.
- **Carrousel 3D** : votre `main.js` d'origine fonctionne sans modification. Les
  publications ne viennent plus d'un fichier `publications.js` figé, mais de la
  **base de données** (donc modifiables depuis le back office).
- **Bouton d'authentification dans la navbar** : « Se connecter » quand vous êtes
  déconnecté, « Back office » + « Se déconnecter » quand vous êtes connecté.
- **Back office** (`/admin`, protégé par connexion) :
  - CRUD des **formations** (les cartes 3D) : titre, badge, description, contenu
    enrichi, image (upload), position.
  - CRUD des **modules** et **sessions** de chaque formation.
  - Changement de mot de passe.
- **Base H2 en fichier** (`./data/evacompta.mv.db`) : données conservées.
- **Images** téléversées dans `./uploads`.

## Prérequis (vous les avez déjà )

- Java 17 ✔   ·   Maven 3.9 ✔   ·   Windows 11 ✔

## Lancer

Dans le dossier `eva-compta` (celui qui contient `pom.xml`) :

    mvn spring-boot:run

Puis ouvrez **http://localhost:8080**

## Connexion

- Bouton « Se connecter » dans la navbar
- E-mail : evabitumbila@gmail.com
- Mot de passe : evariste2026
- Changez ce mot de passe (Back office → Mot de passe).

## Gérer le contenu

- **Formations (cartes 3D)** : Back office → « Nouvelle formation », ou
  « Modifier » / « Supprimer » sur chaque ligne.
- **Modules & sessions** : bouton « Modules & sessions » sur une formation →
  ajouter / renommer / supprimer les modules, et dans chacun les sessions.
- Tout apparaît immédiatement sur le site (carrousel + page détail de la formation).

## Notes

- La page détail d'une formation (`/formations/{id}`) affiche dynamiquement les
  modules et sessions gérés dans le back office, avec votre design d'accordéon.
- Console H2 (optionnel) : http://localhost:8080/h2-console
  (JDBC URL : jdbc:h2:file:./data/evacompta · user : sa · pas de mot de passe)

---

## Mise à jour — associés & trait décoratif

### 1. Trait décoratif supprimé
Le petit trait orange qui précédait chaque intitulé de section (« NOS SERVICES »,
« NOTRE ÉQUIPE », etc.) a été retiré de `style.css` : la règle `.eyebrow::before`
et ses trois variantes (`.hero`, `.band`, `.page-head`) n'existent plus.
Le trait a donc disparu **sur toutes les pages** du site en une seule fois.

### 2. Associés pilotés depuis le back office
Nouvel onglet **« Associés »** dans le back office (à côté de « Formations »
et « Mot de passe »). Pour chaque associé : nom, fonction, présentation,
photo (facultative) et position d'affichage.

**Règle d'affichage :** la section « Les associés à vos côtés » de la page
À propos n'apparaît **que** s'il existe au moins un associé enregistré.
Tant que rien n'est saisi dans le back office, la section entière est
invisible côté public — plus de cases « Associé(e) à compléter ».

- Sans photo : les initiales du nom s'affichent (comme avant).
- Avec photo : la photo remplit la carte, avec un voile sombre pour garder
  le nom lisible.
- La table `associes` est créée automatiquement au démarrage
  (`ddl-auto=update`) : aucune manipulation de base à faire.
