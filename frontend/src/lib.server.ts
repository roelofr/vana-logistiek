import {resolve} from 'node:dns/promises';

const resolveOrEmpty = async (hostname: string, record: string) => {
  try {
    const result = await resolve(hostname, record);
    return result as string[];
  } catch (e) {
    return [];
  }
}

export const resolveHostname = async (hostname: string): Promise<string[]> => {
  return Array<string>()
    .concat(await resolveOrEmpty(hostname, 'A'))
    .concat(await resolveOrEmpty(hostname, 'AAAA'));
}
