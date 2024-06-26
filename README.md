# RAGtime : Discuter avec vos propres données

## Slides

[Les slides sont ici](
https://docs.google.com/presentation/d/e/2PACX-1vQnCn16AyyVsNi8j8HRVEbN_NfApUTYrOQ8fffdxrS0IzjpreQ6wFoD8ZTby373Re9aM1NPGKGoa4s9/pub).

## Installation des prérequis (obligatoire)

Ce workshop, [présenté à Devoxx FR](https://www.devoxx.fr/schedule/talk/?id=29366), nécessite un certain nombre de prérequis que vous ne pourrez pas installer en début de séance à cause de leur taille. **Merci donc de suivre la procédure suivante avant d’arriver au workshop.**

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

### Docker et images
**ATTENTION: si votre PC est sous windows, vous pouvez utiliser WSL et docker, et eventuellement tirer profit des capacités de votre carte graphique. [Voir le doc spécifique](Windows-WSL-Docker.md) avant de continer.**

Assurez-vous que [Docker](https://www.docker.com/products/docker-desktop/) et sa ligne de commande sont installés.

Chargez les images Docker en exécutant :

```
docker compose pull
```

<details>
  <summary>Tester l'installation de Elasticsearch et Kibana</summary>
  <blockquote>
  Pour lancer Elasticsearch et Kibana, executez la commande :

  ```
  docker compose up devoxx-kibana
  ```

Connectez-vous ensuite à [http://localhost:5601](http://localhost:5601) avec le login `elastic` et le mot de passe `elasticpwd`.
  </blockquote>
</details>


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
docker exec -it ollama-devoxx ollama pull gemma:2b
```

Pour discuter avec le modèle, lancez `docker exec -it ollama-devoxx ollama run gemma:2b` et dites quelque chose, par exemple "Bonjour, comment vas-tu ?"

</details>

## Lancer le projet

Pour lancer l'application en "dev mode" qui permet aussi le live-reload, executez :

```
mvn quarkus:dev
```

La console de développement de Quarkus est alors disponible sur [http://localhost:8080/q/dev/](http://localhost:8080/q/dev/)

### Documents à indexer
le fichier json est à télécharger ici: https://drive.google.com/file/d/15eqcNCnb3igxGGt7d1qEXEsUM9nQwaXn/view?usp=sharing

(source: https://www.theregister.com/)

## Pour aller plus loin

2 modeles disponibles sur Ollama qui sont intéressants à tester. N'oubliez pas que le modele pour faire les embeddings (des documents indexés et des questions posées) est indépendant du modele de génération des réponses (à partir du prompt et du contexte injecté). Il peut donc etre interéssant d'utiliser un modele spécialisé sur chacune des taches. 
- `nomic-embed-text:latest` (0.25 GB) pour faire les embeddings (property `quarkus.langchain4j.ollama.embedding-model.model-id`)
- `wizardlm2:7b` (4.1 GB) pour le modele de génération / question answering (properties `ollama.model` et `quarkus.langchain4j.ollama.chat-model.model-id`)
Vous pouvez donc faire un `ollama pull` de ces modeles et les renseigner dans les properties pour les tester. 
