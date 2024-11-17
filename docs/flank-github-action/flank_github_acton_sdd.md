# Flank GitHub action

Add GitHub action which allows running Flank

# References

- [GitHub actions documentation](https://github.com/features/actions)

- [Creating a Docker container action](https://docs.github.com/en/actions/creating-actions/creating-a-docker-container-action)

- [Creating a JavaScript action](https://docs.github.com/en/actions/creating-actions/creating-a-javascript-action)

- [Creating a composite run steps action](https://docs.github.com/en/actions/creating-actions/creating-a-composite-run-steps-action)

- [GitHub actions marketplace](https://github.com/marketplace?type=actions)

  

# Motivation

Bitrise and Circle CI have steps in their CIs to run Flank. As GitHub is a widely used code repository and GitHub actions become more and more popular there should be also GitHub action that allows running Flank. 

After creating GitHub action, Flank could reach more users.

# Goals

- Flank actions should be as easy as possible to run
- Flank action should be maintainable together with Flank runner (always be up to date with options)
- Flank action should be available on the public repository

# Design

There should be a possibility to specify all flank options using GitHub action variables as input, as well as passing the configuration itself.

User must provide at least:

- Flank version (default latest)
- platform to run (iOS or Android)
- Flank service account file to authenticate
- Flank options or flank configuration file path

# API

Flank GitHub action will be developed using [composite run steps action](https://docs.github.com/en/actions/creating-actions/creating-a-composite-run-steps-action) which just packs other actions and runs it as a single action.

### Design proposal

```yaml
name: 'Flank'
description: 'Run Flank from GitHub actions!'
inputs:
    version: 
        description: 'Version of flank to run'
        required: true
        default: <latest>
    platform: 
        description: 'Platform to run iOS or Android'
        required: true
  flank_option1:
    description: 'Description of option 1'
    required: false # validation will done when running
    ...
  flank_optionN:
    description: 'Description of option N'
    required: false # validation will done when running
  flank_configuration_file:
    description: 'Path to configuration file'
    required: false # validation will done when running
  flank_service_account: 
      description: 'Path to service account file to authenticate'
    required: true
outputs:
  output_report:
    description: "Output report"
    value: ${{ steps.report.outputs.random-id }}
runs:
  using: "composite"
  steps:
    - id: download flank
      run: <download flank>
      shell: bash
    - id: validate configuration
      run: <check if configuration file or any options are specified>
      shell: bash
    - id: run flank
      run: <check if configuration file or any options are specified>
      shell: bash
    - id: report
      run: <show run report to user>
      shell: bash
```



### Usage

##### With config file

```yaml
on: [push]

jobs:
  hello_world_job:
    runs-on: ubuntu-latest
    name: A job to say hello
    steps:
    - uses: actions/checkout@v2
    - id: flank
      uses: actions/flank@v1
      with:
        version: '21.01.0'
        platform: 'android'
        flank_service_account: './service_account.json'
        flank_configuration_file: './flank.yml'
    - run: cat ${{ steps.flank.outputs.output_report }}
      shell: bash
```

##### With options

```yaml
on: [push]

jobs:
  hello_world_job:
    runs-on: ubuntu-latest
    name: A job to say hello
    steps:
    - uses: actions/checkout@v2
    - id: flank
      uses: actions/flank@v1
      with:
        version: '21.01.0'
        platform: 'android'
        flank_service_account: './service_account.json'
                app: "../test_projects/android/apks/app-debug.apk"
              test: "../test_projects/android/apks/app-debug-androidTest.apk"
              results-dir: test_dir
                legacy-junit-result: true
    - run: cat ${{ steps.flank.outputs.output_report }}
      shell: bash
```

# Results

After finishing Flank GitHub actions should be published to [GitHub actions marketplace](https://github.com/marketplace?type=actions). 

There should be also an announcement on Flank channel on Slack.

The thing to consider is also using it for our internal verification together with integration tests

# Dependencies

GitHub action needs to have `action.yml` file in the root of the repository, however, Flank repository has the file for posting Slack message after release. However it is only used by Flank team, so it will be best to move it as a side repository and keep the main Flank action in Flank's mono repository

# Testing

A new testing repository will be set up to test action as described in [GitHub actions documentation](https://docs.github.com/en/actions/creating-actions/creating-a-composite-run-steps-action#testing-out-your-action-in-a-workflow)

# Alternatives Considered

The investigation was done to choose the best way to develop custom GitHub action.

[Docker container action](https://docs.github.com/en/actions/creating-actions/creating-a-docker-container-action) is not the best choice, because users are [forced to run Flank action on Ubuntu workflow](https://docs.github.com/en/actions/creating-actions/about-actions#types-of-actions).

The second alternative considered was using [JavaScript as action language](https://docs.github.com/en/actions/creating-actions/creating-a-javascript-action). However, this is another language to maintain in our project and it will not be the best option to choose for such an important thing
