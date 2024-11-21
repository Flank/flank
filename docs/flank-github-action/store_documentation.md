## Action

This GitHub action allows for running Flank as a GitHub workflow.

Documentation for Flank is at [flank.github.io/flank](https://flank.github.io/flank/)


## Usage

### Inputs

| Input name                 | Description                                                                                                                                                                                                                                               | Required | Default          |
|----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|------------------|
| `version`                  | Flank version to run. Minimal supported version is `v21.03.1`. Leaving it blank will fallback to latest version.                                                                                                                                          | `false`  | latest available |
| `service_account`          | Service account to authenticate with. Could be path to file, link to file or file content itself. More information about creating a service account could be found at [documentation](https://flank.github.io/flank/#authenticate-with-a-service-account) | `true`   |                  |
| `platform`                 | Platform to run. Could be `ios` or `android`                                                                                                                                                                                                              | `true`   |                  |
| `flank_configuration_file` | Flank configuration file. More information on how it should look like is in [documentation](https://flank.github.io/flank/#flank-configuration)                                                                                                                     | `true`   |                  |

### Outputs

| Output name                | Description                                                                                                              |
|----------------------------|--------------------------------------------------------------------------------------------------------------------------|
| `gcloud_results_directory` | Link to Gcloud store where results are stored.                                                                           |
| `local_results_directory`  | Path to local results directory. All output files from this run are stored inside. You could use it as output artifacts. |


### Adding to workflows

```yaml
- name: <your name>
  id: <id of action>
  uses: Flank/flank@master
  with:
    version: <Flank version to run, minimum supported is v21.03.1, default latest>
    service_account: <file content, file link or path to file with service account> 
    platform: [android|ios]
    flank_configuration_file: <Path to configuration file>
```


### Example workflows

#### Service account file content from secrets
```yaml
- name: flank run
  id: flank_run
  uses: Flank/flank@master
  with:
    # Flank version to run
    version: v21.03.1
    # Service account file content from secrets
    service_account: ${{ secrets.SERVICE_ACCOUNT }} 
    # Run Android tests
    platform: android
    # Path to configuration file from local repo
    flank_configuration_file: './testing/android/flank-simple-success.yml'

- name: output
  run: |
    # Use local directory output
    echo "Local directory: ${{ steps.flank_run.outputs.local_results_directory }}"
    # Use Gcloud storage output 
    echo "Gcloud: ${{ steps.flank_run.outputs.gcloud_results_directory }}"
```

#### Service account file from repository
```yaml
- name: flank run
  id: flank_run
  uses: Flank/flank@master
  with:
    # Service account file from repository
    service_account: './service_account.json'
    # Run Android tests
    platform: android
    # Path to configuration file from local repo
    flank_configuration_file: './testing/android/flank-simple-success.yml'

- name: output
  run: |
    # Use local directory output
    echo "Local directory: ${{ steps.flank_run.outputs.local_results_directory }}"
    # Use Gcloud storage output 
    echo "Gcloud: ${{ steps.flank_run.outputs.gcloud_results_directory }}"
```

#### Create service account during workflow
```yaml

- name: Create service account
  run: echo '${{ secrets.SERVICE_ACCOUNT }}' > service_account_created.json

- name: flank run
  id: flank_run
  uses: Flank/flank@master
  with:
    # Service account created in previous step
    service_account: './service_account_created.json'
    # Run Android tests
    platform: android
    # Path to configuration file from local repo
    flank_configuration_file: './testing/android/flank-simple-success.yml'

- name: output
  run: |
    # Use local directory output
    echo "Local directory: ${{ steps.flank_run.outputs.local_results_directory }}"
    # Use Gcloud storage output 
    echo "Gcloud: ${{ steps.flank_run.outputs.gcloud_results_directory }}"
```

## Runner OS support
All 3 runner operating systems are supported.
