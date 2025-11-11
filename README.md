# YogaAppFullstack 
Projet OpenClassrooms M2 P5

Application complète de gestion Yoga pour démontrer compétences en test Fullstack (front + back, unitaires + intégration).

---

## 1. Installation et configuration

### 1.1 Cloner le projet

```
git clone https://github.com/LUCIENMathieu03/YogaAppFullstack-OCRM2-P5.git
cd YogaAppFullstack-OCRM2-P5
```
### 1.2 Installation Frontend (Angular)

- Place-toi dans le répertoire du front (`/front` )  
- Installe les dépendances :

```
npm install
```

- Pour lancer l’application Angular (en développement) :

```
npm start
```

---

### 1.3 Installation Backend (Spring Boot)

#### Base de données MySQL

- Crée une base MySQL (ex. `yogadb`).
- Exécute le script SQL pour créer les tables et y insérer les données.

Via une GUI comme MySQL Workbench ou alors en ligne de commande :

```
mysql -u ton_utilisateur -p nom_de_ta_bdd < chemin/vers/script.sql
```

#### Configuration backend

- Java 11 
- Configure le fichier `.env` avec :
```
DB_USERNAME= ton_identifiant
DB_PASSWORD= ton_mot_de_passe
DB_URL= nom_de_ta_bdd
```

- Construis le projet et installe les dépendances :
```
mvn clean install
```

- Pour lancer le backend :
```
mvn spring-boot:run
```


---

## 2. Commandes pour lancer les tests

### 2.1 Tests Frontend

Scripts définis dans `package.json` :
```
"scripts": {
"start": "ng serve",
"test": "jest",
"test:watch": "jest --watch",
"lint": "ng lint",
"e2e": "ng e2e",
"e2e:ci": "ng run yoga:e2e-ci",
"e2e:coverage": "npx nyc report --reporter=lcov --reporter=text-summary",
"cypress:open": "cypress open",
"cypress:run": "cypress run"
}
```

- Pour lancer les tests unitaires front :
```
npm run test
``` 

- Pour lancer les tests E2E Cypress :
```
npm run cypress:open
``` 

---

### 2.2 Tests Backend

- Lancer tous les tests unitaires et d'intégration backend avec Maven :

```
mvn test
``` 


---

## 3. Notes complémentaires

- Assure-toi que MySQL tourne et que tes identifiants sont corrects.
- Java 11 est utilisé pour le backend.
- Ne commite jamais ton fichier `.env` contenant les secrets.
- Utilise `mvn clean`ou `mvn clean install`  avant de relancer les builds/tests pour éviter des conflits.

---
