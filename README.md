# SAE Final

## Description du projet

Ce projet est une application Java utilisant JavaFX permettant de résoudre et visualiser des scénarios de parcours entre villes françaises, en respectant des contraintes de livraisons Pokémon (vendeur → acheteur). L’objectif est de générer plusieurs solutions optimales ou heuristiques pour ces parcours, en s’appuyant sur des algorithmes adaptés, dont un tri topologique modifié pour respecter l’ordre vendeur → acheteur.

---

## Fonctionnalités principales

- Chargement des scénarios à partir de fichiers d’extraction (`Extraction`).
- Calcul de parcours heuristique glouton avec résumé du parcours et distance totale.
- Tri topologique modifié pour respecter les contraintes de l’ordre vendeur → acheteur dans les parcours.
- Génération des K meilleures solutions respectant ces contraintes d’ordre.
- Affichage dynamique des résultats en interface graphique avec JavaFX :
  - Visualisation du parcours.
  - Distance totale.
  - Sélection et mise à jour des scénarios.
- Gestion d’erreurs et retours utilisateurs en cas d’absence de solution ou problème dans les données.
- Modélisation avancée du problème en séparant les villes en sommets "ville+" (vendeur) et "ville-" (acheteur).

---

## Technologies utilisées

- Java 22 avec fonctionnalités preview activées.
- JavaFX pour l’interface graphique.
- Maven/Gradle (selon la configuration) pour gestion des dépendances et compilation.
- IntelliJ IDEA comme environnement de développement.

---

## Organisation du code

- `modele` : classes métier (Extraction, ResumeScenario, AlgoKSolution, etc.).
- `vue` : composants JavaFX (AffichageHeuristiqueGlouton, AffichageKSolution, etc.).
- `controleur` (si présent) : gestion des interactions utilisateur et liaison modèle/vue.

---

## Structure des données

- Fichiers d’extraction : données des villes, distances, membres Pokémon.
- `ResumeScenario` : numéro du scénario, ordre de visite (respectant vendeur → acheteur), distance totale.

---

## Exemples d’utilisation

### Calcul heuristique glouton

```java
HeuristiqueGlouton hg = new HeuristiqueGlouton(extraction, scenarioIndex);
ResumeScenario resume = hg.genererResumeScenario(scenarioIndex);
System.out.println(resume);
```

## Génération des K meilleures solutions
```java
AlgoKSolution algo = new AlgoKSolution(scenarioChoisi);
algo.genererKSolutions(k);
```

---

## Interface graphique
L’interface JavaFX permet de sélectionner un scénario, afficher les parcours calculés ainsi que leurs distances, avec une présentation claire et adaptée. Les composants graphiques sont modulaires et dynamiques.

---

## Installation et configuration

- Cloner le dépôt :

```
git clone https://github.com/ivybix/SAE-Final.git
````

- Ouvrir le projet dans IntelliJ IDEA.
- Configurer le SDK Java 22 avec options preview activées.
- Compiler et lancer la classe principale (MainApp ou équivalent).

---

## Remarques

- Le projet utilise les fonctionnalités preview de Java 22, assurez-vous que votre environnement est compatible.
- Les fichiers scénarios doivent être valides et conformes au format attendu.
