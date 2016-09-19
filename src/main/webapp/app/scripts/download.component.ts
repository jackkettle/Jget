import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {Project} from './Project';
import {DownloadManagerStatus} from './download-manager-status';
import {ProjectService} from './project.service';
import {HTTP_PROVIDERS} from 'angular2/http';
import {List} from 'immutable';
import {Observable} from 'rxjs/Rx';

@Component({
  selector: 'my-dashboard',
  templateUrl: 'app/html/download.component.html',
  styleUrls: ['app/styles/download.component.css'],
  providers: [HTTP_PROVIDERS, ProjectService]
})
export class DownloadComponent implements OnInit {

  constructor(private _downloadManagerService: ProjectService) { }

  public currentProject: Project;
  public downloadStatus: Object;
  public isActive: Boolean;

  ngOnInit() {

    this._downloadManagerService.getActiveManifest().subscribe(
      response => this.currentProject = response
    );

    this._downloadManagerService.isCurrentlyRunning().subscribe(
      data => { this.isActive = data, console.log(data) }
    );

    let tasksSubscription = this.pollStatus()
      .subscribe(
      data => { this.downloadStatus = data, console.log(data) }
      );

  }

  cancelDownload() {
    if(!this.isActive)
      return;

    this._downloadManagerService.cancelDownload()
      .subscribe(
      data => { console.log(data) }
      );
  }

  commenceDownload() {
    this._downloadManagerService.commenceDownload()
      .subscribe(
      data => { this.isActive = true }
      );
  }

  pollStatus() {
    return Observable
      .interval(2000)
      .flatMap(() => {
        return this._downloadManagerService.getDonwloadManagerStatus()
      });

  }

}
