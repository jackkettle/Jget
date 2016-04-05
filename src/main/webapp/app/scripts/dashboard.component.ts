import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {Project} from './Project';
import {DownloadProjectService} from './download-project.service';
import {HTTP_PROVIDERS} from 'angular2/http';
import {List} from 'immutable';

@Component({
  selector: 'my-dashboard',
  templateUrl: 'app/html/dashboard.component.html',
  styleUrls: ['app/styles/dashboard.component.css'],
  providers: [HTTP_PROVIDERS, DownloadProjectService]
})
export class DashboardComponent implements OnInit {
  public downloadProjects = List<Project>();

  constructor(private _downloadManagerService: DownloadProjectService, private _router: Router) { }

  ngOnInit() {
    this._downloadManagerService.getProjects().subscribe(
      response => this.downloadProjects = response,
      error => console.error('Error: ' + error),
      () => console.log('Completed!')
    );
  }
}
