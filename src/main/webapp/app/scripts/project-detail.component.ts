import {Component, OnInit} from 'angular2/core';
import {Router, RouteParams} from 'angular2/router';
import {Project} from './Project';
import {ProjectService} from './project.service';
import {HTTP_PROVIDERS} from 'angular2/http';

@Component({
  selector: 'my-project-detail',
  templateUrl: 'app/html/project-detail.component.html',
  styleUrls: ['app/styles/project-detail.component.css'],
  providers: [HTTP_PROVIDERS, ProjectService]
})
export class ProjectDetailComponent implements OnInit {

  public project: Project;

  constructor(private _router: Router, private _routeParams: RouteParams, private _projectService: ProjectService) { }

  ngOnInit() {
    let id = this._routeParams.get('id');
    this._projectService.get(id)
      .subscribe(
        response => this.project = response,
        error => { 
          console.error('Error: 123' + error); 
          this._router.navigate(['Dashboard', {}]) 
        },
        () => console.log(this.project)
      );
  }

  goBack() {
    this._router.navigate(['Dashboard', {}]);
  }

  deleteProject(id) {
    $('#modal1').closeModal();
    console.log("Deleteing project: " + id);
    this._projectService.delete(id)
      .subscribe(
        error => console.error('Error: ' + error),
        () => this._router.navigate(['Dashboard', {}])
      );
  }

  commenceDownload(project) {
    console.log(project);
  }

  openModal(){
    $('#modal1').openModal();
  }

  closeModal() {
    $('#modal1').closeModal();
  }

}
