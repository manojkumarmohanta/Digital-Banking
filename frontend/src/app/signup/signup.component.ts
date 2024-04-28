import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { AccountService } from '../services/account.service';
import { ToastComponent } from '../toast/toast.component';
import { Router, RouterLink } from '@angular/router';
import { ModalComponent } from "../modal/modal.component";

@Component({
    selector: 'app-signup',
    standalone: true,
    templateUrl: './signup.component.html',
    styleUrl: './signup.component.css',
    imports: [RouterLink, FormsModule, ToastComponent, ModalComponent]
})
export class SignupComponent {
  router = inject(Router);
  accountService = inject(AccountService);
  toastHeading = ""; 
  toastDescription = ""; 
  toastVisible = false;
  modalVisible = false;
    
  validateOtp(form: NgForm) {
    if (form.valid) {
      this.accountService.validateOTP(form.value)
        .subscribe({
          next: res => {
            console.log(res);
            this.generateToast("Success", "Account Created");
            
          },
          error: err => {
            console.log(err);

            const error = err.error;
            this.generateToast(error['title'], error['detail'])
          },
          complete: () => {
            form.reset();
            this.router.navigate(["signin"])
          }
        });
    }
  }
  
  onSubmit(form: NgForm) {
    if (form.valid) {
      this.accountService.createAccount(form.value)
        .subscribe({
          next: res => {
            console.log(res);

            this.generateToast("OTP Sent", "We have successfully sent you an OTP, on your registered email. Please Check your Email.");
            form.reset();
          },
          error: err => {
            console.log(err);

            const error = err.error;
            this.generateToast(error['title'], error['detail'])
          }
        });
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

  closeModal(): void {
    this.modalVisible = false;
  }
}
