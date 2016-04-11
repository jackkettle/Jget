import {Component} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES} from 'angular2/router';
import {DashboardComponent} from './dashboard.component';
import {ProjectCreateComponent} from './project-create.component';
import {ProjectDetailComponent} from './project-detail.component';
import {DownloadProjectService} from './download-project.service';

@Component({
  selector: 'my-app',
  templateUrl: 'app/html/app.component.html',
  styleUrls: ['app/styles/app.component.css'],
  directives: [ROUTER_DIRECTIVES],
  providers: [DownloadProjectService]
})
@RouteConfig([
  {path: '/dashboard', name: 'Dashboard', component: DashboardComponent, useAsDefault: true},
  {path: '/create', name: 'Create', component: ProjectCreateComponent},
  { path: '/detail/:id', name: 'ProjectDetail', component: ProjectDetailComponent }
])
export class AppComponent {
  public title = 'Jget';
  public description = 'Website download tool';
}
