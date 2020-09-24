package flank.scripts.utils


fun currentGitBranch(): String = "git branch --show-current".runForOutput().trim()
