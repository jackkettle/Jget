import {Injectable} from 'angular2/core';
import {Http, Headers} from 'angular2/http';
import {PROJECTS} from './mock-projects';
import {AppSettings} from './app-settings.component';
import 'rxjs/add/operator/map';

@Injectable()
export class ProjectService {

  private headers: Headers;

  constructor(private _http: Http) {
    this.headers = new Headers();
    this.headers.append('Accept', 'application/json');
    this.headers.append('Content-Type', 'application/json');
  }

  getProjectsBackUp() {
    return Promise.resolve(PROJECTS);
  }

  getAll() {
    return this._http.get(`${AppSettings.API_ENDPOINT}get`)
      .map(response => response.json());
  }

  get(id: string) {
    return this._http.get(
      `${AppSettings.API_ENDPOINT}get/` + id)
      .map(response => response.json());
  }

  create(formData: Object) {
    return this._http.post(
      `${AppSettings.API_ENDPOINT}create`, JSON.stringify(formData), { headers: this.headers })
      .map(res => res.json());
  }

  edit(formData: Object) {
    return this._http.post(
      `${AppSettings.API_ENDPOINT}edit`, JSON.stringify(formData), { headers: this.headers })
      .map(res => res.json());
  }

  delete(id: string) {
    return this._http.get(
      `${AppSettings.API_ENDPOINT}delete/` + id)
      .map(response => response.json());
  }

  setActiveManifest(id: string) {
    return this._http.get(
      `${AppSettings.API_ENDPOINT}setCurrentManifest/` + id)
      .map(response => response.json());
  }

  getActiveManifest() {
    return this._http.get(
      `${AppSettings.API_ENDPOINT}getCurrentManifest`)
      .map(response => response.json());
  }

  commenceDownload() {
    return this._http.get(
      `${AppSettings.API_ENDPOINT}commenceDownload`)
      .map(response => response.stringify());
  }

  cancelDownload() {
    return this._http.get(
      `${AppSettings.API_ENDPOINT}cancelDownload`)
      .map(response => response.stringify());
  }

  getDonwloadManagerStatus() {
    return this._http.get(
      `${AppSettings.API_ENDPOINT}downloadManagerStatus`)
      .map(response => response.json());
  }

  isCurrentlyRunning() {
    return this._http.get(
      `${AppSettings.API_ENDPOINT}isActive`)
      .map(response => response.json());
  }

}
