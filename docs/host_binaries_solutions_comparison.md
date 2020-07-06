# Host binaries solutions comparison

## git-lfs

Docs can be found [here](https://git-lfs.github.com)

### Is git lfs versioning files?

Yes. Git LFS versioning files and show changed md5 sum and size.

### How to configure it?

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
