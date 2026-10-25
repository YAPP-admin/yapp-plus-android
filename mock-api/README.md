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
yapp.api.mock.example.current-week=available
yapp.api.mock.example.generation-sessions=sample
```

The session schedule APIs support named response examples. Set either property to a name below; the
debug app sends the selected Prism example only when its target is `mock`.

| Property | Available examples |
| --- | --- |
| `yapp.api.mock.example.current-week` | `available`, `not-eligible`, `not-open`, `no-session` |
| `yapp.api.mock.example.generation-sessions` | `sample`, `empty` |

Remove a property or leave it empty to use Prism's generated response for that API. Change the JSON
values in `mock-api/examples/session-schedule-examples.json` to customize them, then restart
`npm run mock`. The original downloaded `openapi.json` stays unchanged. Rebuild the Android debug app
after changing `local.properties` so Gradle includes the new selection.

The same examples can be requested directly from Prism:

```sh
curl -H 'Prefer: example=not-open' http://localhost:4010/api/sessions/current-week
```

The app's initial Google-to-YAPP session exchange is not connected yet. There is no
`local.properties` access-token injection. The mock serves unauthenticated examples;
it does not create a real authenticated app session. Real server authentication will
use the encrypted session store after the initial authentication contract is connected.

The `release` build always uses `yapp.api.prod.host`. If `yapp.api.debug.target` is omitted, `debug` uses `yapp.api.dev.host`.

## Update the backend contract

```sh
npm run contract:update
```

This downloads `/v3/api-docs` from the dev server and updates the ignored local `openapi.json` snapshot. The mock downloads it automatically if it is missing. To use another OpenAPI endpoint, set `YAPP_OPENAPI_URL` for the command. The generated `mock-openapi.json` is also ignored by Git and recreated whenever the mock starts.

## Mock behavior

Responses are generated from the schemas in the OpenAPI document. Mock requests do not require Bearer authentication, and state changes are not persisted. This is for local app development; it does not test OIDC login, real token authorization, backend validation, or persistent workflows. Switch `yapp.api.debug.target` back to `dev` when you have a development token and want to call the real server.
