# Environment setup (WIP)
1. Ask for access to internal slack channels
1. Ask for invitation to firebase slack
1. Ask for access to github repo
1. Ask for access to test bucket on google cloud platform
1. Install Oracle JDK 8 
1. Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/) to install IntelliJ IDEA Community
1. Setup local flank repository
    * Clone the repo `git clone --recursive https://github.com/Flank/flank.git`
    * Init submodule `git submodule update --init --recursive updates the submodules`
1. Build flank running [./update_flank.sh](../../test_runner/bash/update_flank.sh)
1. Run [./flank](../../test_runner/bash/flank) auth login. Flank will save the credential to ~/.flank.
