import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
 // import {Project} from './Project';
import {DownloadProjectService} from './download-project.service';
import {HTTP_PROVIDERS} from 'angular2/http';
 // import {List} from 'immutable';

@Component({
  selector: 'my-project-create',
  templateUrl: 'app/html/project-create.component.html',
  styleUrls: ['app/styles/project-create.component.css'],
  providers: [HTTP_PROVIDERS, DownloadProjectService]
})
export class ProjectCreateComponent implements OnInit {

  constructor(private _router: Router) { }

  // create(){ }

}
