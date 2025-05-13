import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import helmet from 'helmet';
import {dirname, resolve} from 'node:path';
import {fileURLToPath} from 'node:url';
import {resolveHostname} from './lib.server';
import {STATUS_CODES} from 'node:http';

const serverDistFolder = dirname(fileURLToPath(import.meta.url));
const browserDistFolder = resolve(serverDistFolder, '../browser');

const app = express();
const angularApp = new AngularNodeAppEngine();

/**
 * Use Helmet to protect the webserver
 */
app.use(helmet());

/**
 * Disable the API endpoint, this is a misconfiguration from the server.
 */
app.use('/api/**', (req, res) => {
  res.sendStatus(501);
})

/**
 * Serve static files from /browser
 */
app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

/**
 * Handle all other requests by rendering the Angular application.
 */
app.use('/**', (req, res, next) => {
  angularApp
    .handle(req)
    .then((response) =>
      response ? writeResponseToNodeResponse(response, res) : next(),
    )
    .catch(next);
});

/**
 * Start the server if this module is the main entry point.
 * The server listens on the port defined by the `ANGULAR_PORT` environment variable, or defaults to 4000.
 */
if (isMainModule(import.meta.url)) {
  console.log('Resolving proxy...')

  const trustedProxies: string[] = ([] as string[])
    .concat(['loopback', 'linklocal'])
    .concat(await resolveHostname('host.docker.internal'))

  console.log('Will trust %d proxies: %s', trustedProxies.length, trustedProxies.join(', '));

  const port = (process.env['ANGULAR_PORT'] || 4000) as number;
  app.set('trust proxy', trustedProxies);
  app.listen(port, () => {
    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

/**
 * Request handler used by the Angular CLI (for dev-server and during build) or Firebase Cloud Functions.
 */
export const reqHandler = createNodeRequestHandler(app);
