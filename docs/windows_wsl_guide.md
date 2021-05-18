# Building and running Flank on Windows WSL

It is possible to build and run Flank on Windows through WSL.
You could configure it using your own Windows machine or you could use GitHub actions to do this.

## Building

### Own Windows machine

1. Install WSL on Windows using [Microsoft Guide](https://docs.microsoft.com/en-us/windows/wsl/install-win10)
1. Launch WSL console
1. Install JDK if you do not have any installed
   ```
   sudo apt-get install openjdk-15-jre
   sudo apt-get install openjdk-15-jdk
   export JAVA_HOME=/usr/lib/jvm/openjdk-15-jdk
   export PATH=$PATH:$JAVA_HOME/bin
   ```
1. Install `dos2unix` using the command `sudo apt-get install dos2unix`
1. Convert files to UNIX in your Flank repository directory
   ```
   find . -type f -print0 | xargs -0 -n 1 -P 4 dos2unix
   ```
1. Make Gradlew wrapper executable
   ```
   chmod +x gradlew
   ```
1. Build Flank using command `./gradlew clean build`


### GitHub actions

1. Setup WSL on `windows-2019` runner 
    ```
    runs-on: windows-2019
    ```
1. Setup default shell to `wsl-bash {0}` this will allow avoiding typing `wsl-bash` on start each command
    ```
    defaults:
      run:
        shell: wsl-bash {0}
    ```
1. Use [GitHub action for setup WSL](https://github.com/marketplace/actions/setup-wsl)
    ```
    - uses: Vampire/setup-wsl@v1
      with:
        distribution: Ubuntu-20.04
        additional-packages:
          dos2unix
    ```
1. In order to build flank you should install java, add permission to Gradle wrapper file and prepare each file using `dos2unix`
    ```
    - name: Configure WSL for flank
      run: |
          find . -type f -print0 | xargs -0 -n 1 -P 4 dos2unix
          chmod +x gradlew
          sudo apt-get -y install openjdk-8-jdk
    ```
1. After this step you could build flank like in every UNIX machine
    ```
    ./gradlew clean build
    ```
1. For reference please check [Flank team implementation of WSL workflow](https://github.com/Flank/flank/tree/master/.github/workflows/wsl-workflow.yml)

## Running

After building using the above steps or downloading using the command   
`wget --quiet https://github.com/Flank/flank/releases/download/XXX/flank.jar -O ./flank.jar`  
where `XXX` is the latest version of flank from [Flank releases on GitHub](https://github.com/Flank/flank/releases)
You could run Flank both on your own machine or GitHub actions typing the command:
`java -jar <PATH TO FLANK> <COMMANDS> <OPTIONS>`
You could also add Flank's bash helper folder to your `$PATH` environment variable. This will allow you to call the shell scripts in that helper folder from anywhere.
