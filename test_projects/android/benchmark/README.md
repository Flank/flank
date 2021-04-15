# Automated benchmark

Instrumented test based on ui-automator responsible for running benchmark on tested device

## Benchmarks

* `3dmarkandroid-v2-1-4726.apk`
* `Geekbench 5_v5.3.2.apk` - TODO

## Devices

* Pixel 5e (physical) - API 30
* NexusLowRes (virtual) - API 30
* NexusLowResEmulator (emulator) - API 30

## Table

```
┌─────────────────────┬────────────────────┬─────────────────────────────────────────┬──────────┬─────────────┬────────────────────────────────────────────┬─────────────────────┐
│      MODEL_ID       │        MAKE        │               MODEL_NAME                │   FORM   │ RESOLUTION  │               OS_VERSION_IDS               │        TAGS         │
├─────────────────────┼────────────────────┼─────────────────────────────────────────┼──────────┼─────────────┼────────────────────────────────────────────┼─────────────────────┤
│ NexusLowRes         │ Generic            │ Low-resolution MDPI phone               │ VIRTUAL  │ 640 x 360   │ 23, 24, 25, 26, 27, 28, 29, 30             │ beta=30             │
│ NexusLowResEmulator │ Generic            │ Low-resolution MDPI phone               │ EMULATOR │ 640 x 360   │ 19, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 │ private, alpha      │
│ redfin              │ Google             │ Pixel 5e                                │ PHYSICAL │ 2340 x 1080 │                     30                     │                     │
```
