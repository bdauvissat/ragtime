# Installation recommandée sous Windows

Si votre PC est sous OS principal Windows, une possiblité est d'utiliser WSL (Sous-système Linux pour Windows). D'une manière générale, WSL est tres utile pour tout développeur ayant (par choix ou par obligation) un PC sous Windows. 

L'installation décrite ici permet d'utiliser le GPU de votre carte graphique (si vous en avez une qui est compatible), elle concerne les cartes graphiques nvidia, mais l'équivalent doit etre possible avec AMD. Elle a été testée avec une carte graphique nvidia GeForce 1050 Ti (on ne rigole pas svp !).

## Principe d'installation

1. installer les derniers pilotes nvidia compatibles CUDA (la bibliotheque qui permet aux programmes de s'appuyer sur des GPU pour des calculs paralleles)
1. installer WSL. Bien prendre la version 2 (si vous avez déja un Linux qui tourne sous WSL1, vous pouvez le garder, les 2 versions cohabitent sans problème)
1. installer une distribution Linux (par défaut Ubuntu 22.04). Attention toutes les distributions ne sont pas compatibles avec les pilotes Nvidia. 
1. installer docker dans la distribution Linux. Attention aux interférences avec Docker Desktop (pour ma part je l'ai désinstallé, je n'utilise que le démon docker sous Linux/WSL)
1. installer nvidia-container-toolkit dans Linux
1. si vous voulez voir votre GPU s'agiter, installer `nvtop` 