#!/usr/bin/env bash

set -euo pipefail

echo "This would need sudo to run, probably better to run it manually"
exit 1

# install the necessary dependencies
sudo apt -q update
sudo apt -yq install gnupg curl unzip

# add Azul's public key
sudo apt-key adv \
  --keyserver hkp://keyserver.ubuntu.com:80 \
  --recv-keys 0xB1998361219BD9C9

# download and install the package that adds
# the Azul APT repository to the list of sources
curl -O https://cdn.azul.com/zulu/bin/zulu-repo_1.0.0-3_all.deb

# install the package
sudo apt install ./zulu-repo_1.0.0-3_all.deb

# update the package sources
sudo apt update

sudo apt install zulu17

gradle_ver=gradle-7.4.2

curl -O "https:///downloads.gradle-dn.com/distributions/${gradle_ver}-bin.zip"
unzip "${gradle_ver}-bin.zip"
sudo mv ${gradle_ver} /opt/gradle

sudo touch /etc/profile.d/gradle.sh
echo "export GRADLE_HOME=/opt/gradle/${gradle_ver}" | sudo tee /etc/profile.d/gradle.sh
echo "export PATH=\${GRADLE_HOME}/bin:\${PATH}" | sudo tee -a /etc/profile.d/gradle.sh
