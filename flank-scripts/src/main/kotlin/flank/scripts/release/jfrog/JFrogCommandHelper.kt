package flank.scripts.release.jfrog

val flankMaven: (String) -> String = { version -> "flank/maven/flank/$version" }
