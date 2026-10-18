import { writeFile } from 'node:fs/promises';

const defaultUrl = 'https://44nk4mnqi7.execute-api.ap-northeast-2.amazonaws.com/v3/api-docs';
const url = process.env.YAPP_OPENAPI_URL ?? defaultUrl;

const response = await fetch(url);
if (!response.ok) {
  throw new Error(`OpenAPI fetch failed (${response.status} ${response.statusText}) from ${url}`);
}

const contract = await response.json();
if (!contract.openapi || !contract.paths || Object.keys(contract.paths).length === 0) {
  throw new Error(`The response from ${url} is not a valid OpenAPI document`);
}

await writeFile(new URL('../openapi.json', import.meta.url), `${JSON.stringify(contract, null, 2)}\n`);
console.log(`Saved ${Object.keys(contract.paths).length} API paths from ${url}`);
