import {ReferencedURL} from './referenced-url';
import {KeyValuePair} from './key-value-pair';

export interface Manifest {
  id: string;
  name: string;
  linkMap: Array<KeyValuePair>;
  rootUrls: Array<string>;
  seeds: Array<string>;
  uniqueIDs: Array<string>;
  fileCount: number;
  fileMap: Array<KeyValuePair>;
  frontier: Array<ReferencedURL>;
}
