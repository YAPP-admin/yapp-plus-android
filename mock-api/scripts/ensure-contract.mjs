import { access } from 'node:fs/promises';

const contractUrl = new URL('../openapi.json', import.meta.url);

try {
  await access(contractUrl);
} catch {
  await import('./update-contract.mjs');
}
