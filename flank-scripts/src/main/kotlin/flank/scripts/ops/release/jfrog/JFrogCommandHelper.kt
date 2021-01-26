package flank.scripts.ops.release.jfrog

val flankMaven: (String) -> String = { version -> "flank/maven/flank/$version" }
