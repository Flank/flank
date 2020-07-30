package main

import (
	"strings"
)

// Print hi 100,000 times. Used to stress test Kotlin's bash parsing.
func main() {
	println(strings.Repeat("hi", 1e5))
}
