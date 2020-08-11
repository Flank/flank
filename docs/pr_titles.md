# PR title using [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/)

## Introduction
The Conventional Commits specification is a lightweight convention on top of commit messages. It provides an easy set of rules for creating an explicit commit history; which makes it easier to write automated tools on top of. 
This convention dovetails with SemVer, by describing the features, fixes, and breaking changes made in a commit 
messages.

## Usage
Every PR which is not in draft mode should follow conventional commit convention for PR title. 
It allows us to generate release notes and avoid merge conflicts in [release_notes.md file](../../release_notes.md)

### PR title
Pull request title should be: `<type>([optional scope]): <description>`

where   
`<type>` - [one of following](###Type)
`[optional scope]` - additional information  
`<description>` - description of pr

### Type

- `build` - Changes that affect the build system or external dependencies (dependencies update)
- `ci` - Changes to our CI configuration files and scripts (basically directory `.github/workflows`)
- `docs` - Documentation only changes
- `feat` - A new feature
- `fix` - A bug fix
- `chore` - Changes which does not touch the code (ex. manual update of release notes). It will not generate release notes
changes
- `refactor` - A code change that contains refactor
- `style` - Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
- `test` - Adding missing tests or correcting existing tests and also changes for our test app
- `perf` - A code change that improves performance (I do not think we will use it)

### Examples

- `feat: Add locales description command for ios and android` -> https://github.com/Flank/flank/pull/969
- `fix: rate limit exceeded ` -> https://github.com/Flank/flank/pull/919
- `ci: Added leading V to version name ` -> https://github.com/Flank/flank/pull/980
- `refactor: config entities and arguments` -> https://github.com/Flank/flank/pull/831
- `docs: Add secrets and vision doc` -> https://github.com/Flank/flank/pull/922
- `build: Disable Auto Doc Generation` -> https://github.com/Flank/flank/pull/942
- `test: added multi modules to test app` -> https://github.com/Flank/flank/pull/857
- `chore: Release v20.08.1` -> https://github.com/Flank/flank/pull/982
