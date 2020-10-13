@file:Import("updateAtomic.main.kts")
@file:Import("updateLlvm.main.kts")
@file:Import("updateSwift.main.kts")

updateAtomic()
updateLlvm()
updateSwift()

println("Binaries updated!")
