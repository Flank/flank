# Host binaries solutions comparison

## git-lfs

Docs can be found [here](https://git-lfs.github.com)

### Is git lfs versioning files?

Yes. Git LFS versioning files and show changed md5 sum and size.

On main repository git diff loks like:

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

To add file extension to git lfs you should execute following command:

```bash

git lfs track "*.apk"

```

Now all files with ``` .apk ``` extensions will be added to git lfs

### How to add file

File should be added in `normal` way to git

```git

git add app-debug.apk
git commit -m "Add apk file"
git push origin master

```

### How to play with branches

There is nothing to configure. When you modify files on branch changes not shown on master.

## git-submodules

Docs can be found [here](https://gist.github.com/gitaarik/8735255)

### Is git submodules versioning files?

Git submodule is other git repository linked to main repository. On main repository git diff looks like:

```

diff --git a/apks b/apks
--- a/apks
+++ b/apks
@@ -1 +1 @@
-Subproject commit 7036bcb56be19490b4445a7e31e821e80b9ff870
+Subproject commit 7036bcb56be19490b4445a7e31e821e80b9ff870-dirty

```

So from main repository we dont see what files changed. To check details we need to execute git diff from submodule.

### Is git submodule versioning files?

Yes. Git Submodule versioning binary files like standard git repository.

### How to configure git submodule?

1. Create submodule repository

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

Sub-modules are not on a branch. They are just a pointer to a particular commit of the sub-module's repository.

## git-annex

Docs can be found [here](https://git-annex.branchable.com/git-annex/)
Docs for github [here](https://git-annex.branchable.com/tips/centralized_git_repository_tutorial/on_GitHub/)

We can configure git-annex to store files in places like ftp, amazon s3 etc. Probably it can reduce cost on many large files. https://git-annex.branchable.com/special_remotes/ 