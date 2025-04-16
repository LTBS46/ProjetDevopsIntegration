# Java DataFrame Library - DevOps Project

![DevOps Logo](logo.png)






A Java implementation of a DataFrame library similar to Python's pandas, designed for a DevOps course project. The library provides tabular data manipulation capabilities with support for CSV/TSV file parsing and various data operations.

|||
|:-:|:-|
|Runtime|[![Java 17](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)|
|Package|[![Static Badge](https://img.shields.io/badge/JUnit-5-red)](https://junit.org/junit5/) [![Static Badge](https://img.shields.io/badge/Maven-3.6-green)](https://maven.apache.org/docs/3.6.3/release-notes.html) [![Static Badge](https://img.shields.io/badge/ANTLR-4.13-orange)](https://www.antlr.org/download.html)|
|`main` branch status|[![.github/workflows/actions.yml](https://github.com/LTBS46/ProjetDevopsIntegration/actions/workflows/actions.yml/badge.svg?branch=main)](https://github.com/LTBS46/ProjetDevopsIntegration/actions/workflows/actions.yml)|
|`dev` branch status|[![.github/workflows/actions.yml](https://github.com/LTBS46/ProjetDevopsIntegration/actions/workflows/actions.yml/badge.svg?branch=dev)](https://github.com/LTBS46/ProjetDevopsIntegration/actions/workflows/actions.yml)|

## Features
### Structure et manipulation de données
- **Structure tabulaire bidimensionnelle** avec étiquettes pour les colonnes et lignes
- **Création de DataFrames** à partir de fichiers CSV/TSV grâce au parseur ANTLR4
- **Typage dynamique** avec inférence automatique (String, Integer, Float)
- **Manipulation flexible des données** :
  - Accès aux cellules individuelles, lignes ou colonnes entières
  - Extraction de sous-ensembles (slicing) du DataFrame
  - Suppression de colonnes avec conservation des valeurs

### Opérations analytiques
- **Calculs statistiques** sur les colonnes numériques :
  - Moyenne (Mean)
  - Maximum (Max)
  - Minimum (Min)
- **Export des données** au format CSV

### Interface et accessibilité
- **API intuitive** inspirée de pandas (Python)

## Outils et technologies utilisés

Notre projet s'appuie sur les outils et technologies suivants :

### Développement
- **Java 17** : Langage de programmation principal
- **Maven 3.x** : Gestion des dépendances et du cycle de vie du projet
- **ANTLR 4.13** : Générateur d'analyseurs pour le parsing des fichiers CSV/TSV
- **JUnit 5** : Framework de tests unitaires

### DevOps 
- **GitHub Actions** : Orchestration des workflows d'intégration continue, test et compilation automatique pour chauqe push sur les branches main et dev.
- **Branche** : gestione des branches pour une intégration continue
- **JavaDoc** : Documentation standardisée du code source
- **Tests automatisés** : Vérification de la qualité et de la fiabilité du code

## Workflow Git et procédure de validation

Notre workflow Git s'organise autour de trois types de branches principales :

### Structure des branches
- **main** : Contient uniquement les versions stables et validées du projet
- **dev** : Branche de développement contenant les fonctionnalités en cours d'intégration
- **autres branches*** : Branches temporaires pour le développement de fonctionnalités spécifiques

### Procédure de développement et d'intégration
1. **Création de branches de fonctionnalités** : Pour chaque nouvelle fonctionnalité ou correction, une branche dédiée est créée à partir de `dev`
2. **Développement isolé** : Le travail s'effectue dans la branche de fonctionnalité sans perturber les autres développements
3. **Tests locaux** : Avant toute soumission, le développeur exécute les tests unitaires localement

### Procédure de validation des Pull/Merge Requests
1. **Soumission de PR** : La branche de fonctionnalité est soumise via une Pull Request vers `dev`
2. **Validation automatisée** : Les GitHub Actions exécutent automatiquement :
   - La compilation du projet
   - L'exécution des tests unitaires
   - La vérification du formatage du code
3. **Revue de code** : Au moins un autre membre de l'équipe doit approuver les changements la merge request a pu être approuvé par le même utilisateur github lors de tp où nous étions plusieurs réunit (donc validé à l'oral) 
4. **Fusion** : Une fois approuvée et tous les tests passés, la PR est fusionnée dans `dev`

### Déploiement et releases
1. **Intégration vers main** : Lorsque `dev` contient suffisamment de fonctionnalités stables, une PR est créée vers `main`
2. **Mise à jour de version** : Le numéro de version est automatiquement incrémenté lors de la fusion vers `main`
3. **Création de release** : Une nouvelle release GitHub est automatiquement créée avec les notes de version

## Feedback
Les outils qu'on a utilisé nous ont permis une meilleur gestion du projet/code. Attention au version des outils, dans notre cas on a eu plusieurs problèmes sur des machines ou sur le github de version des outils qui était différentes et donc le projet ne compile pas.

# Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/LTBS46/ProjetDevopsIntegration.git
2. Build with Maven:
    ```bash
    mvn clean install
```xml
<dependency>
			<groupId>org.antlr</groupId>
			<artifactId>antlr4-runtime</artifactId>
			<version>4.13.2</version>
</dependency>
```
# Docker
Présent sous docker HUB:
```bash
docker pull frubit/rakoun-dataframe
```
