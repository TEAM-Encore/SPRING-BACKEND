package encore.server.domain.user.service;

import encore.server.domain.user.converter.UserConverter;
import encore.server.domain.user.dto.request.UserLoginReq;
import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.enumerate.NickNameAnimal;
import encore.server.domain.user.enumerate.NickNameColor;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.auth.util.JwtUtil;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final JwtUtil jwtUtil;
    private final Random random = new Random();

    @Transactional
    public String signup(UserSignupReq userSignupReq) {
        
        // validation: 회원가입된 유저인지 확인
        if(userRepository.existsByEmail(userSignupReq.email())) {
            throw new ApplicationException(ErrorCode.ALREADY_EXIST_EXCEPTION);
        }

        // business logic: 비밀번호 인코딩 후 유저 저장
        String encodedPassword = passwordEncoder.encode(userSignupReq.password());

        // businessLogic: 요청값, 인코딩된 비밀번호, 랜덤 생성된 겹치지 않는 닉네임을 통해 User Entity를 만들고 저장함.
        // return: 유저 email 반환
        return userRepository.save(userConverter.toEntity(userSignupReq, encodedPassword, getUniqueNickName())).getEmail();
    }
    
    public String login(UserLoginReq userLoginReq){

        // validation: 회원가입된 유저인지 확인
        User findUser = userRepository.findByEmail(userLoginReq.email())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // validation: 비밀번호 확인
        if(!passwordEncoder.matches(userLoginReq.password(), findUser.getPassword())){
            throw new ApplicationException(ErrorCode.PASSWORD_MISMATCH_EXCEPTION);
        }

        // return: Access Token 반환
        return jwtUtil.createToken(findUser.getEmail(), findUser.getRole());
    }

    public String getUniqueNickName(){

        StringBuilder uniqueNickNameStringBuilder = new StringBuilder();
        String uniqueNickName;

        while(true){

            NickNameAnimal animal = NickNameAnimal.values()[random.nextInt(NickNameAnimal.values().length)];
            NickNameColor color = NickNameColor.values()[random.nextInt(NickNameColor.values().length)];
            uniqueNickNameStringBuilder
                    .append(color.name())
                    .append(animal.name())
                    .append(random.nextInt(100));
            uniqueNickName = uniqueNickNameStringBuilder.toString();

            if(!userRepository.existsByNickName(uniqueNickName)){
                break;
            }

            uniqueNickNameStringBuilder.setLength(0);
        }

        return uniqueNickName;
    }
}
