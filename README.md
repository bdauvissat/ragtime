# RAGtime : Discuter avec vos propres données

## Installation des prérequis (obligatoire)

Ce workshop, présenté à [Devoxx FR 2024](https://www.devoxx.fr/schedule/talk/?id=29366) et lors du [Volcamp 2024](https://www.volcamp.io/talks/24d1t4s7) nécessite un certain nombre de prérequis que vous ne pourrez pas installer en début de séance à cause de leur taille. **Merci donc de suivre la procédure suivante avant d’arriver au workshop.**

En cas d’affluence, les animateurs du workshop se réservent le droit de donner la priorité aux participants qui auront installé les prérequis.

### Clonez ce projet

```
git clone https://github.com/bdauvissat/ragtime.git
cd ragtime
```

### Projet Java

Assurez-vous d'avoir Java 17 ou plus installé et soit :

* chargez le projet dans votre IDE favori,
* ou téléchargez les librairies nécessaires en exécutant :

  ```
  ./mvnw dependency:resolve
  ```

### Ollama

[Ollama](https://ollama.com/) est un serveur permettant de faire fonctionner des LLM localement sur votre machine. Deux options sont possibles en fonction de votre configuration: installation locale (recommandé) ou avec Docker.

En plus de l'installation de Ollama, il est nécessaire de télécharger [Gemma 2B](https://ollama.com/library/gemma), le petit LLM utilisé pour le workshop qui peut fonctionner sur des configurations modestes.

<details>
  <summary><b>Installation locale (recommandé)</b></summary>

Cette installation permettra à Ollama de [tirer partie du GPU](https://github.com/ollama/ollama/blob/main/docs/gpu.md) présent sur votre machine. Suivez les instructions sur [https://ollama.com/download](https://ollama.com/download).

Une fois installé et lancé, téléchargez le modèle avec `ollama pull gemma:2b`.

Pour discuter avec le modèle, lancez `ollama run gemma:2b` et dites quelque chose, par exemple "Bonjour, comment vas-tu ?"

</details>

<details>
  <summary><b>Installation avec Docker</b></summary>

Si l'installation locale n'est pas possible, lancez Ollama en exécutant la commande suivante :

```
docker compose up ollama
```

Une fois lancé, chargez le modèle avec

```
docker exec -it ollama-adelean ollama pull gemma:2b
```

Pour discuter avec le modèle, lancez `docker exec -it ollama-adelean ollama run gemma:2b` et dites quelque chose, par exemple "Bonjour, comment vas-tu ?"

</details>

## Lancer le projet

Pour lancer l'application en "dev mode" qui permet aussi le live-reload, executez :

```
mvn quarkus:dev
```

La console de développement de Quarkus est alors disponible sur [http://localhost:8080/q/dev/](http://localhost:8080/q/dev/)

