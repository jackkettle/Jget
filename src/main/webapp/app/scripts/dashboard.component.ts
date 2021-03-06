import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {Project} from './Project';
import {ProjectService} from './project.service';
import {HTTP_PROVIDERS} from 'angular2/http';
import {List} from 'immutable';

@Component({
  selector: 'my-dashboard',
  templateUrl: 'app/html/dashboard.component.html',
  styleUrls: ['app/styles/dashboard.component.css'],
  providers: [HTTP_PROVIDERS, ProjectService]
})
export class DashboardComponent implements OnInit {

  public downloadProjects = List<Project>();
  public currentProject: Project;

  constructor(private _downloadManagerService: ProjectService, private _router: Router) { }

  ngOnInit() {

    this._downloadManagerService.getActiveManifest().subscribe(
      response => this.currentProject = response,
      error => this._router.navigate(['Dashboard', {}])
    )

    this._downloadManagerService.getAll().subscribe(
      response => {this.downloadProjects = response, console.log(this.downloadProjects)},
      error => this._router.navigate(['Dashboard', {}])
    )

  }

  gotoDetail(project: Project) {
    this._router.navigate(['ProjectDetail', { id: project.id }]);
  }

  gotoEdit(project: Project) {
    this._router.navigate(['ProjectEdit', { id: project.id }]);
  }
}
