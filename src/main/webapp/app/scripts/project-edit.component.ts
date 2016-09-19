import {Component, OnInit} from 'angular2/core';
import {Router, RouteParams} from 'angular2/router';
import {Project} from './Project';
import {ProjectService} from './project.service';
import {HTTP_PROVIDERS} from 'angular2/http';
import {List} from 'immutable';
import { FORM_DIRECTIVES, FormBuilder, Validators, Control, ControlGroup, ControlArray } from 'angular2/common';
import {FormBuilder, Validators} from 'angular2/forms';

@Component({
  selector: 'my-project-create',
  templateUrl: 'app/html/project-edit.component.html',
  styleUrls: ['app/styles/project-edit.component.css'],
  directives: [FORM_DIRECTIVES],
  providers: [HTTP_PROVIDERS, ProjectService]
})
export class ProjectEditComponent implements OnInit {

  public project: Project;

  group: ControlGroup;
  name: Control;
  id: Control;
  rootURLs: Control[];
  rootURLArray: ControlArray;

  seedURLs: Control[];
  seedURLArray: ControlArray;

  constructor(private _formBuilder: FormBuilder, private _routeParams: RouteParams, private _router: Router, private _projectService: ProjectService) {

    this.id = new Control('');
    this.name = new Control('');
    this.rootURLs = [new Control('')];
    this.rootURLArray = new ControlArray(this.rootURLs);

    this.seedURLs = [new Control('')];
    this.seedURLArray = new ControlArray(this.seedURLs);

    this.group = _formBuilder.group({
      id: this.id,
      name: this.name,
      rootURLInputs: this.rootURLArray,
      seedURLInputs: this.seedURLArray
    });
  }

  ngOnInit() {
    let id = this._routeParams.get('id');

    this._projectService.get(id)
      .subscribe(
      response => this.project = response,
      error => this._router.navigate(['Dashboard', {}]),
      () => this.handleFormPopulate());

  }

  handleFormPopulate() {
    this.id = new Control(this.project.id);
    this.name = new Control(this.project.name);

    this.rootURLs = [];
    for (let rootUrlEntry of this.project.rootUrls) {
      this.rootURLs.push(new Control(rootUrlEntry));
    }
    this.rootURLArray = new ControlArray(this.rootURLs);

    this.seedURLs = [];
    for (let seedURLEntry of this.project.seeds) {
      this.seedURLs.push(new Control(seedURLEntry));
    }
    this.seedURLArray = new ControlArray(this.seedURLs);

    this.group = this._formBuilder.group({
      id: this.id,
      name: this.name,
      rootURLInputs: this.rootURLArray,
      seedURLInputs: this.seedURLArray
    });
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

  goBack() {
    this._router.navigate(['Dashboard', {}]);
  }


  onSubmit(event) {
    this._projectService.edit(this.group.value).subscribe(
      response => console.log(response),
      error => console.error('Error: ' + error),
      () => this._router.navigate(['Dashboard', {}])
    );
  }
}
