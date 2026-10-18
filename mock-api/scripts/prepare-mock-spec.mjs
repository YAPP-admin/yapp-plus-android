import { readFile, writeFile } from 'node:fs/promises';

const contractUrl = new URL('../openapi.json', import.meta.url);
const outputUrl = new URL('../mock-openapi.json', import.meta.url);
const contract = JSON.parse(await readFile(contractUrl, 'utf8'));

// Keep the checked-in contract faithful to the backend. The local mock must be
// usable before the app has a development token, so remove auth requirements
// only from the generated Prism input.
delete contract.security;
for (const pathItem of Object.values(contract.paths ?? {})) {
  for (const [method, operation] of Object.entries(pathItem)) {
    if (['get', 'post', 'put', 'patch', 'delete', 'options', 'head', 'trace'].includes(method.toLowerCase())) {
      delete operation.security;
      for (const response of Object.values(operation.responses ?? {})) {
        if (response.content?.['*/*']) {
          response.content['application/json'] ??= response.content['*/*'];
          delete response.content['*/*'];
        }
      }
    }
  }
}

contract.servers = [{ url: 'http://localhost:4010', description: 'Local Prism mock server' }];
await writeFile(outputUrl, `${JSON.stringify(contract, null, 2)}\n`);
console.log(`Prepared ${Object.keys(contract.paths ?? {}).length} paths for unauthenticated local mocking`);
