import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { ModalComponent } from "../modal/modal.component";
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-sidebar',
    standalone: true,
    templateUrl: './sidebar.component.html',
    styleUrl: './sidebar.component.css',
    imports: [RouterLinkActive, RouterLink, ModalComponent, FormsModule]
})
export class SidebarComponent {
  router = inject(Router);
  modalVisible=false;
  onLogout() {
    localStorage.removeItem("token");
    this.router.navigateByUrl("/signin")
  }
}
