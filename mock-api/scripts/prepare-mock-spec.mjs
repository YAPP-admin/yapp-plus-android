import { readFile, writeFile } from 'node:fs/promises';

const contractUrl = new URL('../openapi.json', import.meta.url);
const outputUrl = new URL('../mock-openapi.json', import.meta.url);
const examplesUrl = new URL('../examples/session-schedule-examples.json', import.meta.url);
const contract = JSON.parse(await readFile(contractUrl, 'utf8'));
const mockExamples = JSON.parse(await readFile(examplesUrl, 'utf8'));
const httpMethods = new Set(['get', 'post', 'put', 'patch', 'delete', 'options', 'head', 'trace']);

// Keep the checked-in contract faithful to the backend. The local mock must be
// usable before the app has a development token, so remove auth requirements
// only from the generated Prism input.
delete contract.security;
for (const pathItem of Object.values(contract.paths ?? {})) {
  for (const [method, operation] of Object.entries(pathItem)) {
    if (httpMethods.has(method.toLowerCase())) {
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

for (const [route, examples] of Object.entries(mockExamples)) {
  const [method, path] = route.split(/\s+/, 2);
  const response = contract.paths?.[path]?.[method.toLowerCase()]?.responses?.['200'];
  const responseContent = response?.content?.['application/json'];

  if (!responseContent) {
    throw new Error(`Cannot add mock examples: ${route} has no JSON 200 response in openapi.json`);
  }

  responseContent.examples = examples;
}

contract.servers = [{ url: 'http://localhost:4010', description: 'Local Prism mock server' }];
await writeFile(outputUrl, `${JSON.stringify(contract, null, 2)}\n`);
const exampleCount = Object.values(mockExamples).reduce(
  (count, examples) => count + Object.keys(examples).length,
  0,
);
const pathCount = Object.keys(contract.paths ?? {}).length;
console.log(
  `Prepared ${pathCount} paths and ${exampleCount} named examples for ` +
    'unauthenticated local mocking',
);
