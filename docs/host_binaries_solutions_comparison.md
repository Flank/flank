# Host binaries solutions comparison

## git-lfs

Docs can be found at [git-lfs.github.com](https://git-lfs.github.com)

### Costs

[about-storage-and-bandwidth-usage](https://docs.github.com/en/github/managing-large-files/about-storage-and-bandwidth-usage)

Each data pack cost is $5 per month. It contains:

1. 50gb bandwidth
2. 50gb storage

### Is git lfs versioning files?

Yes. Git LFS versioning files and show changed md5 sum and size.

On main repository git diff looks like:

```git

diff --git a/app/build/outputs/apk/release/app-release-unsigned.apk b/app/build/outputs/apk/release/app-release-unsigned.apk
index bba31d0..d165ca1 100644
--- a/app/build/outputs/apk/release/app-release-unsigned.apk
+++ b/app/build/outputs/apk/release/app-release-unsigned.apk
@@ -1,3 +1,3 @@
 version https://git-lfs.github.com/spec/v1
-oid sha256:bf48d577836f07a3d625ee30f04eb356c0b1158770f613df0d82b6ef40d300d3
-size 1938709
+oid sha256:3349370934f8a1695b6ace6db53e58c0f24a1d9c02ce703b4a78870175c8c066
+size 1938769

```

### How to configure git lfs?

To add a file extension to git lfs you should execute the following command:

```bash

git lfs track "*.apk"

```

Now all files with ``` .apk ``` extensions will be added to git lfs

### How to add file

The file should be added in `normal` way to git

```git

git add app-debug.apk
git commit -m "Add apk file"
git push origin master

```

### How to play with branches

There is nothing to configure. When you modify files on branch changes not shown on the master.

## git-submodules

Docs can be found at [gist.github.com/gitaarik/8735255](https://gist.github.com/gitaarik/8735255)

### Is git submodules versioning files?

Git submodule is another git repository linked to the main repository. On main repository git diff looks like:

```txt

diff --git a/apks b/apks
--- a/apks
+++ b/apks
@@ -1 +1 @@
-Subproject commit 7036bcb56be19490b4445a7e31e821e80b9ff870
+Subproject commit 7036bcb56be19490b4445a7e31e821e80b9ff870-dirty

```

So from the main repository, we don't see what files changed. To check details we need to execute git diff from the submodule.

### Is git submodule versioning files?

Yes. Git Submodule versioning binary files like standard git repository.

### How to configure git submodule?

1. Create a submodule repository

2. On `main` execute repository git submodule add git@github.com:url_to/awesome_submodule.git path_to_awesome_submodule.

3. Execute `git submodule init`

### How to add file

Goto submodule directory

```git

git add app-debug.apk
git commit -m "Add apk file"
git push origin master

```

### How to play with branches

Actually there is an option to track specific branch on submodule.
https://stackoverflow.com/questions/9189575/git-submodule-tracking-latest

## git-annex

Docs can be found at [git-annex](https://git-annex.branchable.com/git-annex/)
Docs for [github at git-annex](https://git-annex.branchable.com/tips/centralized_git_repository_tutorial/on_GitHub/)

We can configure git-annex to store files in places like ftp, amazon s3, etc. Probably it can reduce costs on many large files. [link](https://git-annex.branchable.com/special_remotes/)

### Costs

Depends on the chosen storage provider

1. Google drive
   1. Limitation to 15gb (free quota)
   2. After 15gb need to switch to google one or use Gsuite (10$/month and quota 100gb per user)
2. OneDrive
   1. Pricing table: https://www.opendrive.com/pricing
   2. Free quota 5gb
   3. From table: Custom Plan: 500gb storage and 25gb daily bandwidth : 5$/month 50$/year
3. Droplet/VPS - depend on the provider
4. FTP/SFTP - depend on provider

### Is git annex versioning files?

Git-annex like git lfs versioning control sum

Git diff looks like:

```git

@ -1 +1 @@
../../../../../.git/annex/objects/QJ/ZX/SHA256E-s1938733--a5bd978c2a6a9ff32bdf0ad5bd94c1362d6904c80c8f6d7890a40303a5d1d703.apk/SHA256E-s1938733--a5bd978c2a6a9ff32bdf0ad5bd94c1362d6904c80c8f6d7890a40303a5d1d703.apk
../../../../../.git/annex/objects/xG/5q/SHA256E-s1938761--038902c65338873f5936f7c5d764fc98839746036a9fffb46223bb742fd1556f.apk/SHA256E-s1938761--038902c65338873f5936f7c5d764fc98839746036a9fffb46223bb742fd1556f.apk

```

### How to configure git annex?

1. First, you need to install git-annex

    ```bash

    brew install git-annex

    ```

    If you don't have brew installed. Install it from [brew.sh](https://brew.sh)

    Installing on other systems can be found at [git-annex install](https://git-annex.branchable.com/install/)

2. Initialize

    On root repository

    ```bash

    git annex init

    ```

### How to add file

```git

git annex add app-debug.apk
git commit -m "Add apk file"
git push origin master

```

### How to override file

1. Unlock file

    ```git

    git annex unlock app-debug.apk

    ```

2. Change file

3. Add file to annex

    ```git

    git annex add app-debug.apk

    ```

### Where to host

Git-annex allows to host binary files in different locations, after a little discussion with @jan-gogo we chose top 3:

1. Google Drive
2. OneDrive
3. Droplet/VPS
4. FTP/SFTP

This list is only our idea feel free to suggest another one.

All of the supported storage types can be seen at [git-annex special_remotes](https://git-annex.branchable.com/special_remotes/)

### How to play with branches

When you change branch you can override file and commit changes (remember to unlock file first). File will be uploaded on configured storage.

### How to configure new git-annex repository with google drive

1. Install rclone from [downloads.rclone.org](https://downloads.rclone.org/v1.37/)
2. Install git-annex-rclone from [git-annex-remote-rclone](https://github.com/DanielDent/git-annex-remote-rclone)
3. Go to repository directory and in console enter `rclone config`
4. Init remote by `git annex initremote CONFIG_NAME type=external externaltype=rclone target=CONFIG_NAME prefix=git-annex chunk=50MiB encryption=shared mac=HMACSHA512 rclone_layout=lower`
5. You can test remote by `git annex testremote`

### How to push changes to remote

1. Unlock file `git-annex unlock file_name`

2. Modify file

3. Add file `git annex add file_name --force`

4. Sync changes `git annex sync file_name --content`

### How to sync changes from remote

1. If you don't have configured git clone and git-annex with rclone check 1 to 3 points from `How to configure new git-annex repository with google drive` section

2. execute command `git annex enableremote CONFIG_NAME`

3. After clone repo sync with git-annex `git annex sync --content`

## conclusion

|                   |           GIT LFS             | GIT SUBMODULES | GIT ANNEX                    |
|:-----------------:|:-----------------------------:|:--------------:|:----------------------------:|
|       Costs       |   5$/month per pack (50gb)    |   Cannot find clear answer            | Depend on storage provider   |
| Versioning Files  |           Yes                 |   Yes          |  Yes                         |
| Branch support    |           Yes                                             |   Now there is an option to track specific branch on submodule. https://stackoverflow.com/questions/9189575/git-submodule-tracking-latest |  Yes |
| Flexibility       |  Yes, you can set your lfs server. [Check here](https://github.com/git-lfs/git-lfs/wiki/Implementations)   |   No | Yes, can use different storage providers. |

Our requirement is to have remote storage for test artifacts that should cover the following points:
* Allow having dedicated artifacts for specific branches.
* Free to use
* Public read access and restricted write 
Git annex was the most promising but we didn't find remote storage that will meet all our requirements.
By comparing these three solutions, we decide to stay with GitHub releases and automate the release process using custom Gradle tasks.
