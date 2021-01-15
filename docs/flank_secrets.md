# Flank Secrets

Flank securely communicates to Firebase Test Lab using Google's official Java SDKs for authorization. 

## 2FA

Two-factor authentication is required for everyone in the Flank organization.

## Running Flank

Flank runs require authorization with either a Google user account or a service account. Service accounts are the recommended way to authenticate to Flank instead of using a personal account. The authorization credential is saved by default to: `$HOME/.config/gcloud/application_default_credentials.json`

## Developing Flank

The Flank release job requires secrets as part of continuous delivery. A [flankbot](https://bintray.com/flankbot) account with [`Member`](https://www.jfrog.com/confluence/display/BT/Bintray+Organizations#:~:text=A%20member%20of%20an%20organization%20in%20Bintray%20can%20create%20repositories,details%2C%20and%20change%20member%20authorizations.) level permission is used to release artifacts on bintray.

- `GITHUB_TOKEN` - Provided by [GitHub Actions](https://docs.github.com/en/actions/configuring-and-managing-workflows/authenticating-with-the-github_token)
- `JFROG_USER` - Username for jfrog authentication
- `JFROG_API_KEY` - API key for jfrog used to publish releases. The API key is found in https://bintray.com/profile/edit
