import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {PROJECTS} from './mock-projects';
import 'rxjs/add/operator/map';

@Injectable()
export class DownloadProjectService {

  constructor (private http: Http) {}

  getProjectsBackUp() {
    return Promise.resolve(PROJECTS);
  }

  getProjects() {
    return this.http.get('http://localhost:8090/api/download/getProjects')
      .map(response => response.json());
  }

  getProject(id: string) {
    return Promise.resolve(PROJECTS)
      .then(projects => projects.filter(h => h.id === id)[0]);
  }
}
