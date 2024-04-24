import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ToastComponent } from "../toast/toast.component";
import { SidebarComponent } from '../sidebar/sidebar.component';
import { ModalComponent } from '../modal/modal.component';
import { HeaderComponent } from '../header/header.component';

@Component({
    selector: 'app-loan',
    standalone: true,
    templateUrl: './loan.component.html',
    styleUrl: './loan.component.css',
    imports: [FormsModule,
      SidebarComponent,
      HeaderComponent,
      ModalComponent,
      ToastComponent,]
})
export class LoanComponent {
  toastHeading = '';
  toastDescription = '';
  toastVisible = false;
  onLoanSubmit(form: NgForm) {
    if (form.valid) {
      form.reset();
      this.generateToast('Success', 'Your Loan Application has been submitted successfully.');
    }
  }

  generateToast(heading: string, description: string) {
    this.toastHeading = heading;
    this.toastDescription = description;
    this.toastVisible = true;

    setTimeout(() => {
      this.toastVisible = false;
    }, 5000);
  }
}


