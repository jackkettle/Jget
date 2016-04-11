import {Component, OnInit} from 'angular2/core';
import {Router, RouteParams} from 'angular2/router';
import {Project} from './Project';
import {DownloadProjectService} from './download-project.service';
import {HTTP_PROVIDERS} from 'angular2/http';
// import {List} from 'immutable';

@Component({
  selector: 'my-project-detail',
  templateUrl: 'app/html/project-detail.component.html',
  styleUrls: ['app/styles/project-detail.component.css'],
  providers: [HTTP_PROVIDERS, DownloadProjectService]
})
export class ProjectDetailComponent implements OnInit {

  public project: Project;

  constructor(private _router: Router, private _routeParams: RouteParams, private _downloadManagerService: DownloadProjectService) { }

  ngOnInit() {
    let id = this._routeParams.get('id');
    this._downloadManagerService.getProject(id)
      .subscribe(
        response => this.project = response,
        error => console.error('Error: ' + error),
        () => console.log(this.project)
      );
  }

  goBack() {
    this._router.navigate(['Dashboard', {}]);
  }

}
