package com.jtbank.backend.controller;

import com.jtbank.backend.constant.AccountStatus;
import com.jtbank.backend.dto.*;
import com.jtbank.backend.mapper.AccountMapper;
import com.jtbank.backend.model.Account;
import com.jtbank.backend.model.Credential;
import com.jtbank.backend.service.IAccountService;
import com.jtbank.backend.service.IJWTService;
import com.jtbank.backend.service.IMailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final IAccountService accountService;
    private final IJWTService jwtService;
    private final IMailService mailService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/OtpValidateAndRegister/{otp}")
    @ResponseStatus(HttpStatus.OK)
    public void validateAccount(@PathVariable String otp) throws UnsupportedEncodingException {
        var user = accountService.getAccountByOtp(otp);

        if (user.getOtp().equals(otp)){
            LocalDateTime otpCreationTime = user.getOtpCreationTime();
            LocalDateTime currentTime = LocalDateTime.now();
            if (otpCreationTime.plusMinutes(3).isAfter(currentTime)){
                var credential = user.getCredential();
                credential.setStatus(AccountStatus.ACTIVE);
                var email = credential.getAccountEmail();
                var name = user.getAccountHolderName();
                mailService.sendRegisterSuccessfulMessage(name, email);
            }else {
                accountService.deleteAccount(user.getAccountNumber());
            }

        }
        else {
            throw  new RuntimeException("OTP is not validated.");
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDTO createAccount(@Valid @RequestBody AccountRequestDTO dto) throws UnsupportedEncodingException, MessagingException {
        var account = AccountMapper.modelMapper(dto);
        var result = accountService.createAccount(account);
        var email = result.getCredential().getAccountEmail();
        var name = result.getAccountHolderName();
        var otp = result.getOtp();
        mailService.sendOtp(name, email, String.valueOf(otp));
        return AccountMapper.dtoMapper(result);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDTO createAccount(@Valid @RequestBody AccountAddressDTO dto) {
        var account = AccountMapper.modelMapper(dto);
        var result = accountService.saveAccount(account);
        return AccountMapper.dtoMapper(result);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TokenDTO accountByEmailAndPassword(@RequestBody Credential credential) throws UnsupportedEncodingException, IllegalAccessException {
        var email = credential.getAccountEmail();
        var result = accountService.getAccountByEmail(email);
        var status = result.getCredential().getStatus();
        if (status.equals(AccountStatus.ACTIVE)){
            var auth = new UsernamePasswordAuthenticationToken(credential.getAccountEmail(),
                    credential.getAccountPassword());
            authenticationManager.authenticate(auth);

            var account = accountService.getAccountByEmail(credential.getAccountEmail());
            var token = jwtService.generateToken(String.valueOf(account.getAccountNumber()));
            var name = account.getAccountHolderName();
            mailService.sendLoginSuccessfulMessage(name, email);
            return new TokenDTO(token);
        }else{
            throw new IllegalAccessException("User not Validated.");
        }
    }

    @PostMapping("/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountResponseDTO uploadPicture(@RequestAttribute long accountNumber,
                                          @RequestParam("file")MultipartFile file) throws Exception {
        if(file.isEmpty())
            throw new RuntimeException("Image not found");

        var result = accountService.uploadProfilePicture(accountNumber, file);
        return AccountMapper.dtoMapper(result);
    }


    @PutMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountResponseDTO updateAccount(@PathVariable long accountNumber, @RequestBody UpdateDTO dto) throws UnsupportedEncodingException {
        var account = AccountMapper.modelMapper(dto);
        var result = accountService.updateAccount(accountNumber, account);
        var name = account.getAccountHolderName();
        var email = result.getCredential().getAccountEmail();
        mailService.sendProfileUpdateSuccessfulMessage(name, email);
        return AccountMapper.dtoMapper(result);
    }

    @PatchMapping("/deposit/{balance}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@RequestAttribute long accountNumber, @PathVariable double balance) throws UnsupportedEncodingException {
        accountService.depositBalance(accountNumber, balance);
        Account account = accountService.getAccount(accountNumber);
        var name = account.getAccountHolderName();
        var email = account.getCredential().getAccountEmail();
        var updatedBalance = account.getAccountBalance() + balance;
        mailService.sendDepositSuccessfulMessage(name, email, balance, updatedBalance, accountNumber);
    }

    @PatchMapping("/withdrawal/{balance}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@RequestAttribute long accountNumber, @PathVariable double balance) throws UnsupportedEncodingException {
        accountService.withdrawalBalance(accountNumber, balance);
        Account account = accountService.getAccount(accountNumber);
        var name = account.getAccountHolderName();
        var email = account.getCredential().getAccountEmail();
        var updatedBalance = account.getAccountBalance() - balance;
        mailService.sendWithdrawalSuccessfulMessage(name, email, balance, updatedBalance, accountNumber);
    }

    @PatchMapping("/transfer/{receiver}/balance/{balance}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestAttribute("accountNumber") long sender, @PathVariable long receiver, @PathVariable double balance) throws UnsupportedEncodingException {
        accountService.transfer(sender, receiver, balance);
        Account account = accountService.getAccount(sender);
        var name = account.getAccountHolderName();
        var email = account.getCredential().getAccountEmail();
        var updatedBalance = account.getAccountBalance() - balance;
        mailService.sendTransferSuccessfulMessage(name, email, balance, updatedBalance, sender, receiver);
    }

    @DeleteMapping("/number/{accountNumber}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountResponseDTO deleteByAccountNumber(@PathVariable long accountNumber) {
        var result = accountService.deleteAccount(accountNumber);
        return AccountMapper.dtoMapper(result);
    }

    @GetMapping
    public List<AccountResponseDTO> accounts() {
        var results = accountService.getAccounts();
        return results.stream().map(AccountMapper::dtoMapper).toList();
    }

    @GetMapping(value = "/{accountNumber}/image", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getImage(@PathVariable long accountNumber) throws Exception {
        return accountService.getProfilePicture(accountNumber);
    }

    @GetMapping("/{slNo}")
    public AccountResponseDTO accountBySlNO(@PathVariable int slNo) {
        var result = accountService.getAccountBySlNo(slNo);
        return AccountMapper.dtoMapper(result);
    }

    @GetMapping("/number/{accountNumber}")
    public AccountResponseDTO accountByNumber(@PathVariable long accountNumber) {
        var result = accountService.getAccount(accountNumber);
        return AccountMapper.dtoMapper(result);
    }

    @GetMapping("/current")
    public AccountResponseDTO currentAccountByNumber(@RequestAttribute long accountNumber) {
        var result = accountService.getAccount(accountNumber);
        return AccountMapper.dtoMapper(result);
    }

    @GetMapping("/email/{email}")
    public AccountResponseDTO accountByEmail(@PathVariable String email) {
        var result = accountService.getAccountByEmail(email);
        return AccountMapper.dtoMapper(result);
    }

    @GetMapping("/address-details/{addressId}")
    public  AccountResponseDTO accountByAddressId(@PathVariable String addressId) {
        var result = accountService.getAccountByAddressId(addressId);
        return AccountMapper.dtoMapper(result);
    }
}