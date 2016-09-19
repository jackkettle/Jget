import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {Project} from './Project';
import {ProjectService} from './project.service';
import {HTTP_PROVIDERS} from 'angular2/http';
import {List} from 'immutable';
import { FORM_DIRECTIVES, FormBuilder, Validators, Control, ControlGroup, ControlArray } from 'angular2/common';
import {FormBuilder, Validators} from 'angular2/forms';

@Component({
  selector: 'my-project-create',
  templateUrl: 'app/html/project-create.component.html',
  styleUrls: ['app/styles/project-create.component.css'],
  directives: [FORM_DIRECTIVES],
  providers: [HTTP_PROVIDERS, ProjectService]
})
export class ProjectCreateComponent implements OnInit {

  public downloadProjects = List<Project>();

  group: ControlGroup;
  name: Control;
  rootURLs: Control[];
  rootURLArray: ControlArray;

  seedURLs: Control[];
  seedURLArray: ControlArray;

  constructor(private _formBuilder: FormBuilder, private _router: Router, private _projectService: ProjectService) { 
    this.name = new Control('');
    this.rootURLs = [new Control('')];
    this.rootURLArray = new ControlArray(this.rootURLs);
    this.seedURLs = [new Control('')];
    this.seedURLArray = new ControlArray(this.seedURLs);
    this.group = _formBuilder.group({
      name: this.name,
      rootURLInputs: this.rootURLArray,
      seedURLInputs: this.seedURLArray
    });
  }

  ngOnInit() {
    this._projectService.getAll().subscribe(
      response => this.downloadProjects = response,
      error => console.error('Error: ' + error),
      () => console.log('Completed!')
    );
  }

  goBack(){
    this._router.navigate(['Dashboard', {}]);
  }

  addRootURLInput() {
    this.rootURLArray.push(new Control(''));
  }

  removeRootURLInput(index) {
    this.rootURLArray.removeAt(index);
  }

  addSeedURLInput() {
    this.seedURLArray.push(new Control(''));
  }

  removeSeedURLInput(index) {
    this.seedURLArray.removeAt(index);
  }


  onSubmit(event){
    this._projectService.create(this.group.value).subscribe(
      response => console.log(response),
      error => console.error('Error: ' + error),
      () => this._router.navigate(['Dashboard', {}])
    );
  }
}
