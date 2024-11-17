# Environment setup
This document may be incomplete now or in the future, so if you faced any problems ask the team for help.

### Mac
1. Install a brew, it's not mandatory but may be convenient for installing other software.
1. Currently, the zsh is the default shell on a mac. If you prefer bash use `chsh -s /bin/bash`.

### Env config
Bunch of useful exports. You can paste them to your `.bashrc`
```bash
FLANK_REPO="type path to your local flank repository"
export PATH=$PATH:$HOME/$FLANK_REPO/flank/test_runner/bash
export PATH=$PATH:$HOME/$FLANK_REPO/flank/test_projects/android/bash
export PATH=$PATH:$HOME/Library/Android/sdk/platform-tools
export PATH=$PATH:$HOME/Library/Python/2.7/bin
#export PATH=$PATH:$HOME/"path to your local gcloud repository"/gcloud_cli/google-cloud-sdk/bin
export FLANK_PROJECT_ID=ftl-flank-open-source
export GOOGLE_CLOUD_PROJECT=ftl-flank-open-source
export GITHUB_TOKEN="type your github token here"
```

### Common
1. Ask for access to internal slack channels
1. Ask for an invitation to firebase slack
1. Ask for access to GitHub repo
1. Ask for access to test bucket on a google cloud platform
1. Install [Oracle JDK 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
    * Unfortunately there is no official way to download installer without a login account.
    * Unfortunately unofficial instruction from [wavezhang](https://gist.github.com/wavezhang/ba8425f24a968ec9b2a8619d7c2d86a6) sometimes isn't working.
1. Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/) to install IDE.
    1. Install JetBrains Toolbox
        * download from [website](https://www.jetbrains.com/toolbox-app/)
        * or `brew cask install jetbrains-toolbox`
    1. Install IntelliJ idea (community may be enough)
    1. Install the Android studio.
1. Setup local flank repository
    1. Clone the repo `git clone --recursive https://github.com/Flank/flank.git`
    1. Init submodule `git submodule update --init --recursive updates the submodules`
1. Build flank running [./update_flank.sh](../../test_runner/bash/update_flank.sh)
1. Auth google account
    1. Run `flank auth login` [[./flank](../../test_runner/bash/flank)].
    1. Click on the link and authenticate the account in a browser.
    1. Flank will save the credential to ~/.flank.
1. Install gcloud. Be aware gcloud requires a python environment.
    1. You can clone https://github.com/Flank/gcloud_cli
    1. Or follow official instruction https://cloud.google.com/sdk/docs/quickstarts
    1. Don't forget about exports for Python and gcloud
1. Configure pre-commit hook for ktlint code autoformatting
    1. Make sure you can execute `flank-scripts` from the command line if not navigate to in the command line to `./flank-scripts/bash`
    1. Run `flankScripts linter apply_to_git_hooks`
1. Apply Ktlint style to Idea project.
    1. Make sure you can execute `flank-scripts` from the command line if not navigate to in the command line to `./flank-scripts/bash`
    1. Run `flankScripts linter apply_to_ide`
