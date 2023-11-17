# Learnings

## Auth-code samples

- AppSSO sets `clientAuthenticationMethod: client_secret_basic` for oauth2 bindings, but that's
  unrecognized before Boot 2.5. Had to introduce my own ClientRegistrationRepository to transform
  that client authentication method. Opened a PR with Spring Cloud Bindings:
  https://github.com/spring-cloud/spring-cloud-bindings/pull/106
- The sample uses a `ssoServiceUrl` and that's completely missing. We don't support this, because it
  is used with features we do not wish to support at the moment:
  1. Single-sign-out
  2. Removing consents for clients / revoking tokens
- Removed java-cfenv ; the sample uses SpringCloudBindings that's added by the buildpack
- Certificate trust happens at the platform level (woohoo) with ca-certificates buildpack
