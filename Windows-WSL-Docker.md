# Installation recommandée sous Windows

Si votre PC est sous OS principal Windows, une possiblité est d'utiliser WSL (Sous-système Linux pour Windows). D'une manière générale, WSL est tres utile pour tout développeur ayant (par choix ou par obligation) un PC sous Windows. 

L'[installation décrite ici](https://developer.nvidia.com/cuda/wsl) permet d'utiliser le GPU de votre carte graphique (si vous en avez une qui est compatible), elle concerne les cartes graphiques nvidia, mais l'équivalent doit etre possible avec AMD. Elle a été testée avec une carte graphique nvidia GeForce 1050 Ti (on ne rigole pas svp !).

## Principe d'installation

1. [installer les derniers pilotes nvidia compatibles CUDA](https://www.nvidia.com/Download/index.aspx) (la bibliotheque qui permet aux programmes de s'appuyer sur des GPU pour des calculs paralleles)
1. installer WSL, [par exemple avec cette documentation](https://learn.microsoft.com/fr-fr/windows/wsl/install-manual). Bien prendre la version 2 (si vous avez déja un Linux qui tourne sous WSL1, vous pouvez le garder, les 2 versions cohabitent sans problème)
1. installer une distribution Linux (par défaut Ubuntu 22.04). Attention toutes les distributions ne sont pas compatibles avec les pilotes Nvidia, Ubuntu est recommandé. 
1. installer docker dans la distribution Linux. Attention aux interférences avec Docker Desktop (pour ma part je l'ai désinstallé, je n'utilise que le démon docker sous Linux/WSL) 
   ```
   # Install Docker, you can ignore the warning from Docker about using WSL
   # You can also add --dry-run in order to make a... dry run
   curl -fsSL https://get.docker.com -o get-docker.sh
   sudo sh get-docker.sh
   
   # Add your user to the Docker group
   sudo usermod -aG docker $USER
   
   # Sanity check that both tools were installed successfully
   docker --version
   docker compose version
   
   # Using Ubuntu 22.04 or Debian 10+? You need to do 1 extra step for    iptables
   # compatibility, you'll want to choose option (1) to use    iptables-legacy from
   # the prompt that'll come up when running the command below.
   #
   # You'll likely need to reboot Windows or at least restart WSL    after applying
   # this, otherwise networking inside of your containers won't work.
   sudo update-alternatives --config iptables
   ```
1. [installer nvidia-container-toolkit](https://docs.nvidia.com/datacenter/cloud-native/container-toolkit/latest/install-guide.html#installation) dans Linux, si vous souhaitez utiliser la version dockerisée d'Ollama.
1. si vous voulez voir votre GPU s'agiter, installer `nvtop` (un simple `sudo apt install nvtop` fera l'affaire)