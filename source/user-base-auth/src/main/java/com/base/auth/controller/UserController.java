package com.base.auth.controller;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.user.UserAutoCompleteDto;
import com.base.auth.dto.user.UserDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.form.account.AccountProfileDto;
import com.base.auth.form.user.SignUpUserForm;
import com.base.auth.form.user.LoginForm;
import com.base.auth.form.user.UpdateUserForm;
import com.base.auth.form.user.UserIdForm;
import com.base.auth.form.user.VerifyOtpLoginForm;
import com.base.auth.mapper.AccountMapper;
import com.base.auth.mapper.UserMapper;
import com.base.auth.model.Account;
import com.base.auth.model.Group;
import com.base.auth.model.User;
import com.base.auth.model.criteria.UserCriteria;
import com.base.auth.repository.*;
import com.base.auth.service.MFAService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class UserController extends ABasicController{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private MFAService mfaService;

    @PostMapping(value = "/signup", produces= MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> create(@Valid @RequestBody SignUpUserForm signUpUserForm, BindingResult bindingResult)
    {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Account account = accountMapper.fromSignUpUserToAccount(signUpUserForm);
        account.setPassword(passwordEncoder.encode(signUpUserForm.getPassword()));
        account.setKind(UserBaseConstant.USER_KIND_USER);
        Group group = groupRepository.findFirstByKind(UserBaseConstant.GROUP_KIND_USER);
        account.setGroup(group);
        account.setStatus(UserBaseConstant.STATUS_ACTIVE);
        accountRepository.save(account);

        User user = new User();
        user.setAccount(account);
        user.setBirthday(signUpUserForm.getBirthday());
        user.setStatus(UserBaseConstant.STATUS_ACTIVE);
        userRepository.save(user);
        apiMessageDto.setMessage("Sign Up Success");
        return apiMessageDto;
    }

    @PostMapping(value = "/login", produces= MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> login(@Valid @RequestBody LoginForm loginForm, BindingResult bindingResult)
    {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findAccountByPhone(loginForm.getPhone());
        if (account==null||!passwordEncoder.matches((loginForm.getPassword()),account.getPassword()))
        {
            throw new BadRequestException("phone number or password is not correct");
        }
        User user = userRepository.findByAccountId(account.getId()).orElseThrow(()
            -> new NotFoundException("User not found"));
        if (!Boolean.TRUE.equals(user.getMfaEnabled())){
            String qrCodeUrl = null;
            if (StringUtils.isBlank(user.getMfaSecretKey())){
                String secretKey = mfaService.generateSecretKeyForUser();
                qrCodeUrl = mfaService.generateQrCodeUrl(secretKey, loginForm.getPhone());
                user.setMfaSecretKey(secretKey);
            }
            user.setMfaEnabled(false);
            userRepository.save(user);
            apiMessageDto.setData(qrCodeUrl);
        }
        apiMessageDto.setMessage("Login success, please verify OTP");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('US_V')")
    public ApiMessageDto<UserDto> getUser(@PathVariable("id") Long id)
    {
        ApiMessageDto<UserDto> apiMessageDto = new ApiMessageDto<>();
        User user = userRepository.findById(id).orElse(null);
        if (user==null)
        {
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Not found user");
            apiMessageDto.setCode(ErrorCode.USER_ERROR_NOT_FOUND);
            return apiMessageDto;
        }
        apiMessageDto.setData(userMapper.fromEntityToUserDto(user));
        apiMessageDto.setMessage("get user success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasRole('US_D')")
    public ApiMessageDto<String> deleteUser(@PathVariable("id") Long id)
    {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        User user = userRepository.findById(id).orElse(null);
        if (user==null)
        {
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Not found user");
            apiMessageDto.setCode(ErrorCode.USER_ERROR_NOT_FOUND);
            return apiMessageDto;
        }
        Account account = accountRepository.findById(user.getAccount().getId()).orElse(null);
        if (account.getIsSuperAdmin()){
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Not allow delete super admin");
            apiMessageDto.setCode(ErrorCode.ACCOUNT_ERROR_NOT_ALLOW_DELETE_SUPPER_ADMIN);
            return apiMessageDto;
        }
        addressRepository.deleteAllByUserId(id);
        userRepository.delete(user);
        serviceRepository.deleteAllByAccountId(account.getId());
        accountRepository.delete(account);
        apiMessageDto.setMessage("Delete User success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('US_L')")
    public ApiMessageDto<ResponseListDto<List<UserDto>>> getList(UserCriteria userCriteria , Pageable pageable)
    {

        ApiMessageDto<ResponseListDto<List<UserDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<UserDto>> responseListDto = new ResponseListDto<>();
        Page<User> listUser = userRepository.findAll(userCriteria.getSpecification(),pageable);
        responseListDto.setContent(userMapper.fromUserListToUserDtoList(listUser.getContent()));
        responseListDto.setTotalPages(listUser.getTotalPages());
        responseListDto.setTotalElements(listUser.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list user success");
        return apiMessageDto;
    }

    @GetMapping(value = "/auto-complete",produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<UserAutoCompleteDto>>> ListAutoComplete(UserCriteria userCriteria)
    {
        ApiMessageDto<ResponseListDto<List<UserAutoCompleteDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<UserAutoCompleteDto>> responseListDto = new ResponseListDto<>();
        Pageable pageable = PageRequest.of(0,10);
        Page<User> listUser =userRepository.findAll(userCriteria.getSpecification(),pageable);
        responseListDto.setContent(userMapper.fromUserListToUserDtoListAutocomplete(listUser.getContent()));
        responseListDto.setTotalPages(listUser.getTotalPages());
        responseListDto.setTotalElements(listUser.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("get success");
        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('US_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateUserForm updateUserForm, BindingResult bindingResult) {

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        User user = userRepository.findById(updateUserForm.getId()).orElse(null);
        if (user==null)
        {
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Not found user");
            apiMessageDto.setCode(ErrorCode.USER_ERROR_NOT_FOUND);
            return apiMessageDto;
        }
        if (!user.getAccount().getPhone().equalsIgnoreCase(updateUserForm.getPhone()))
        {
            Account account = accountRepository.findAccountByPhone(updateUserForm.getPhone());
            if(account!=null)
            {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.USER_ERROR_EXIST);
                apiMessageDto.setMessage("Phone is existed");
                return apiMessageDto;
            }
        }
        if (!user.getAccount().getEmail().equalsIgnoreCase(updateUserForm.getEmail()))
        {
            Account account = accountRepository.findAccountByEmail(updateUserForm.getEmail());
            if(account!=null)
            {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.USER_ERROR_EXIST);
                apiMessageDto.setMessage("Email is existed");
                return apiMessageDto;
            }

        }
        Account account = accountRepository.findById(user.getAccount().getId()).orElse(null);
        if(StringUtils.isNoneBlank(updateUserForm.getPassword()))
        {
            account.setPassword(passwordEncoder.encode(updateUserForm.getPassword()));
        }
        accountMapper.fromUpdateUserFormToEntity(updateUserForm,account);
        accountRepository.save(account);
        apiMessageDto.setMessage("update success");
        return apiMessageDto;
    }

    @PutMapping(value = "/verify-otp-login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> verifyOtpLogin(@RequestBody @Valid VerifyOtpLoginForm verifyOtpLoginForm, BindingResult bindingResult){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findAccountByPhone(verifyOtpLoginForm.getPhone());
        if (account == null){
            throw new NotFoundException("Account not found");
        }
        User user = userRepository.findByAccountId(account.getId()).orElseThrow(()
        -> new NotFoundException("User not found"));
        boolean isVerifyOtp = mfaService.verifyOtp(user.getMfaSecretKey(), verifyOtpLoginForm.getOtp());
        if (!isVerifyOtp){
            throw new BadRequestException("Invalid OTP");
        }
        user.setMfaEnabled(UserBaseConstant.MFA_ENABLE);
        userRepository.save(user);
        apiMessageDto.setMessage("Verify OTP success");
        return apiMessageDto;
    }

    @PutMapping(value = "/restart-qrcode", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('US_RT')")
    public ApiMessageDto<String> restartQRCode(@RequestBody UserIdForm userIdForm, BindingResult bindingResult){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        User user = userRepository.findById(userIdForm.getId()).orElseThrow(()
        -> new NotFoundException("User not found"));
        user.setMfaSecretKey(null);
        user.setMfaEnabled(false);
        userRepository.save(user);
        apiMessageDto.setMessage("Restart QR code success");
        return apiMessageDto;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('US_P')")
    public ApiMessageDto<AccountProfileDto> profile(){
        ApiMessageDto<AccountProfileDto> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findById(getCurrentUser()).orElseThrow(()
        -> new NotFoundException("Account not found"));
        if (account == null){
            throw new NotFoundException("Account not found");
        }
        apiMessageDto.setData(accountMapper.fromEntityToAccountProfileDto(account));
        apiMessageDto.setMessage("Get profile success");
        return apiMessageDto;
    }
}
