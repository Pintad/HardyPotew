# Wizard Quiz (Android, Kotlin + Jetpack Compose)

Application de quiz hors-ligne sur un univers de magie fictif (non officiel, sans contenu protégé).

## Fonctionnalités
- Écran d'accueil:
  - Nombre de propositions: **2 / 4 / 8 / 12**.
  - Mode **Quiz** (10 questions aléatoires) ou **Entraînement** (filtre catégorie optionnel).
- Partie:
  - Score en direct et progression (`Question X/10`).
  - Bonus difficulté: `easy=+1`, `medium=+2`, `hard=+3`.
  - Feedback après validation + explication facultative.
  - Gestion du manque de mauvaises réponses: **rétrogradation automatique** + message utilisateur.
- Résultats:
  - Score final et pourcentage.
  - Rejouer ou retour accueil.

## Architecture
- **MVVM** avec `StateFlow`.
- `QuestionRepository` lit le CSV depuis `assets`.
- Parser CSV robuste (`CsvQuestionParser`) :
  - BOM UTF-8.
  - Délimiteur `;` ou `,`.
  - Champs avec guillemets (`"..."`) et guillemets échappés (`""`).
  - Lignes vides ignorées.

## Emplacement et format des questions
Fichier par défaut :

`app/src/main/assets/questions.csv`

Schéma attendu (ordre strict):

```csv
id;category;difficulty;question;correct;wrong1;wrong2;wrong3;wrong4;wrong5;wrong6;wrong7;wrong8;wrong9;wrong10;explanation
```

- `difficulty`: `easy | medium | hard`
- `wrong1..wrong10`: peuvent être vides.
- `explanation`: optionnelle.

### Exemple minimal d’une ligne
```csv
q13;Sorts;easy;Quel sort sèche rapidement une robe mouillée ?;Sicca;Lumis;Ferro;;;;;;;;;;Retire l'humidité sans chauffer.
```

## Build et exécution
1. Ouvrir le projet dans **Android Studio Hedgehog+**.
2. Laisser Gradle synchroniser.
3. Lancer l’app sur émulateur/device Android (API 24+).

### Via terminal (si SDK Android configuré)
```bash
./gradlew test
./gradlew assembleDebug
```

## Tests unitaires inclus
- Parsing CSV (`CsvQuestionParserTest`)
- Génération des choix 2/4/8/12 (`OptionGeneratorTest`)
- Rétrogradation si réponses insuffisantes (`OptionGeneratorTest`)
