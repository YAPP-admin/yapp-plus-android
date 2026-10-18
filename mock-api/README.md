# YAPP Plus Mock API

This starts a local mock server from the backend's OpenAPI contract. It lets the Android app make HTTP requests before a development Bearer token is available.

## Start the server

From this directory:

```sh
nvm use
npm run mock
```

The first run downloads the OpenAPI document and the pinned Prism CLI version. Prism listens on port `4010` and binds to all network interfaces. Android Emulator can reach the development machine at `http://10.0.2.2:4010`. For a physical device, set `yapp.api.mock.host` in the repository's ignored `local.properties` to the development machine's LAN address, for example `http://192.168.0.12:4010`.

The repository's `local.properties` can select the target for the debug build:

```properties
yapp.api.debug.target=mock
yapp.api.mock.host=http://10.0.2.2:4010
```

When the backend provides a development access token, use the real dev API and
add the token to the ignored `local.properties` file:

```properties
yapp.api.debug.target=dev
yapp.api.debug.token=<development access token>
```

The token is used only by debug builds and is kept out of version control. Do not
share debug APKs containing a personal development token. Leaving the property
empty sends requests without an `Authorization` header, so the Mock API remains
usable without a token.

The `release` build always uses `yapp.api.prod.host`. If `yapp.api.debug.target` is omitted, `debug` uses `yapp.api.dev.host`.

## Update the backend contract

```sh
npm run contract:update
```

This downloads `/v3/api-docs` from the dev server and updates the ignored local `openapi.json` snapshot. The mock downloads it automatically if it is missing. To use another OpenAPI endpoint, set `YAPP_OPENAPI_URL` for the command. The generated `mock-openapi.json` is also ignored by Git and recreated whenever the mock starts.

## Mock behavior

Responses are generated from the schemas in the OpenAPI document. Mock requests do not require Bearer authentication, and state changes are not persisted. This is for local app development; it does not test OIDC login, real token authorization, backend validation, or persistent workflows. Switch `yapp.api.debug.target` back to `dev` when you have a development token and want to call the real server.
