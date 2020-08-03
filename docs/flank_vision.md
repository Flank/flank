# Flank Vision

Flank is a massively parallel Android and iOS test runner for Firebase Test Lab.

## GCloud Compatible

Flank is a Kotlin reimplementation of [gcloud firebase test](https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test) commands. Flank strives to implement all gcloud test commands using compatible YAML syntax. The CLI flags match when possible.

The same `flank.yml` file can be run with both gcloud and Flank.

- `gcloud firebase test android run flank.yml:gcloud`
- `flank firebase test android run -c flank.yml`

## Industry enabling features

Flank adds features on top of gcloud CLI, such as test sharding for iOS, to improve the developer experience. The goal of Flank's features is to be implemented in the server so that all test lab customers may benefit, not just those who use Flank. By upstreaming Flank features, a new set of even more awesome capabilities can be developed on top of the server API.

## Vision

Today the Flank team is focused on bug fixes and stabilization in both the test runner and the [Fladle](https://github.com/runningcode/fladle) plugin. In the future Flank may adopt a gRPC API to enable other software to easily be built on top of Flank. Examples include test analytics and test flakiness management.

