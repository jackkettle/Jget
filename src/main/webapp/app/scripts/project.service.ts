import {Injectable} from 'angular2/core';
import {Http, Headers} from 'angular2/http';
import {PROJECTS} from './mock-projects';
import 'rxjs/add/operator/map';

@Injectable()
export class ProjectService {

  private headers: Headers;

  constructor (private _http: Http) {
    this.headers = new Headers();
    this.headers.append('Accept', 'application/json');
    this.headers.append('Content-Type', 'application/json');
  }

  getProjectsBackUp() {
    return Promise.resolve(PROJECTS);
  }

  getAll() {
    return this._http.get('http://localhost:8090/api/project/get')
      .map(response => response.json());
  }

  get(id: string) {
    return this._http.get('http://localhost:8090/api/project/get/' + id)
      .map(response => response.json());
  }

  create(formData: Object){
    return this._http.post('http://localhost:8090/api/project/create', JSON.stringify(formData), { headers: this.headers })
      .map(res => res.json());
  }

  delete(id: string) {
    return this._http.get('http://localhost:8090/api/project/delete/' + id)
      .map(response => response.json());
  }

}
