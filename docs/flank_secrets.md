# Flank Secrets

Flank securely communicates to Firebase Test Lab using Google's official Java SDKs for authorization.

## 2FA

Two-factor authentication is required for everyone in the Flank organization.

## Running Flank

Flank runs require authorization with either a Google user account or a service account. Service accounts are the recommended way to authenticate to Flank instead of using a personal account. The authorization credential is saved by default to: `$HOME/.config/gcloud/application_default_credentials.json`

## Developing Flank

The Flank release job requires secrets as part of continuous delivery. We use our Sonatype account for releasing a new version of Flank to the [Maven Central Repository](https://mvnrepository.com/artifact/com.github.flank/flank).
