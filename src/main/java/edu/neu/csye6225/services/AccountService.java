package edu.neu.csye6225.services;

import edu.neu.csye6225.dto.AuthenticationDetails;
import edu.neu.csye6225.dto.Response;
import edu.neu.csye6225.entity.Account;
import edu.neu.csye6225.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;


@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    private final HttpServletRequest request;

    @Autowired
    public AccountService(AccountRepository accountRepository, HttpServletRequest request){
        this.accountRepository = accountRepository;
        this.request = request;
    }

    @PostConstruct
    private void initializeTheUsers()  {
        String line;
        String[] data;
        Account temp;
        Account newAccount;
        log.debug("Initializing user accounts in the application");
        BufferedReader fileReader;
        try{
            log.debug("Reading data from the file");
            fileReader = new BufferedReader(new FileReader("opt/users.csv"));
            log.debug("Data access completed, validating the data before proceeding with account creation");
            line = fileReader.readLine();
            data = line.split(",");
            if(data.length != 4) {
                log.error("Invalid data provided, data must contain 4 fields first_name, last_name,email and password");
                throw new RuntimeException("Corrupt data provide, please check the data and make sure it has exactly four fields first_name, last_name and password");
            }
            while ((line = fileReader.readLine()) != null){
                temp = null;
                data = line.split(",");
                if(data.length != 4) {
                    log.error("Invalid data provided, data must contain 4 fields first_name, last_name,email and password");
                    throw new RuntimeException("Corrupt data provide, please the data and make sure it has exactly four fields first_name, last_name,email and password");
                }
                if(!data[2].matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
                    log.warn("Invalid data provided, Format of the email provided doesn't match the accepted format of abc@xyx.in, skipping the account creation for the user with email :" + data[2]);
                    continue;
                }
                temp = accountRepository.findUserByEmail(data[2]);
                if(temp == null){
                    newAccount = new Account();
                    if(!data[0].matches("^[a-zA-Z]+$")){
                        log.warn("Invalid data provided, first_name should contain only Upper or lower case letter, skipping account creating for the user with email :" + data[2]);
                        continue;
                    }
                    if(data[0].length() > 20){
                        log.warn("Invalid data provided, first_name should not contain more than 20 characters, skipping account creating for the user with email :" + data[2]);
                        continue;
                    }
                    newAccount.setFirstName(data[0]);
                    if(!data[1].matches("^[a-zA-Z]*$")){
                        log.warn("Invalid data provided, last_name should contain only Upper or lower case letter, skipping account creating for the user with email :" + data[2]);
                        continue;
                    }
                    if(data[1].length() > 20){
                        log.warn("Invalid data provided, last_name should not contain more than 20 characters, skipping account creating for the user with email :" + data[2]);
                        continue;
                    }
                    newAccount.setLastName(data[1]);
                    newAccount.setEmail(data[2]);
                    newAccount.setPassword(BCrypt.hashpw(data[3],BCrypt.gensalt()));
                    newAccount.setAccountCreated(new Timestamp(System.currentTimeMillis()));
                    newAccount.setAccountUpdated(new Timestamp(System.currentTimeMillis()));
                    newAccount = accountRepository.save(newAccount);
                    if(newAccount.getId()!= null){
                        log.info("User account created successfully for user with email: " + newAccount.getEmail());
                    }else{
                        log.warn("Failed creating account for the user with email : " + newAccount.getEmail());
                    }
                }else{
                    log.info("Account already exists for the user with email : " + data[2]);
                }
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            long count = accountRepository.count();
            if(count > 0)
                log.warn("Failed to locate the CSV file with user data, but proceeding with deployment as the Application has registered users");
            else{
                log.warn("Failed to locate the CSV file with user data, please check the file location and try again");
                throw new RuntimeException("Failed to locate the CSV file with user data, please check the file location and try again");
            }
        } catch (IOException e) {
            throw new RuntimeException("Data inside the CSV file is corrupted, please check the data and try again");
        }
    }

    public boolean authenticateUser(String email, String password){
        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            log.warn("Invalid Email provided, check your email and try again");
            return false;
        }
        Account account = accountRepository.findUserByEmail(email);

        if(null != account && BCrypt.checkpw(password, account.getPassword())){
            log.info("Email and password match for provided details, authentication successful");
            HttpSession session = request.getSession();
            session.setAttribute("accountId", account.getId());
            return true;
        }else {
            log.info("Invalid credentials provided, authentication failed");
            return false;
        }
    }

    public String generateToken(String email, String password){
        log.info("Generating token for the email and password combination");
        return "Basic" + " " +
                Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
    }

    public AuthenticationDetails getEmailAndPassword(String token){
        log.info("Trying to extract Email and Password from the token provided");
        AuthenticationDetails details = new AuthenticationDetails();
        String[] tokenParts = token.split(" ");
        if(tokenParts.length !=2){
            log.error("Token is invalid, length of the array after splitting thr token is not equal to 2. Token : " +  token );
            details.setErrorMessage("Invalid Token provided, check the data and try again");
            details.setStatus(Response.ReturnStatus.FAILURE);
            return details;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(tokenParts[1]);
        String decodedString = new String(decodedBytes);
        String[] detailsArray = decodedString.split(":");
        if(detailsArray.length !=2){
            log.error("Token is invalid, Not able to get the email and password from the token provided. Token : " +  token );
            details.setErrorMessage("Invalid Token provided, check the data and try again");
            details.setStatus(Response.ReturnStatus.FAILURE);
            return details;
        }
        log.info("Email and password are extracted from the token provided");
        details.setStatus(Response.ReturnStatus.SUCCESS);
        details.setEmail(detailsArray[0]);
        details.setPassword(detailsArray[1]);
        return details;
    }

    public Account getAccountById(UUID uuid){
        return accountRepository.findById(uuid).orElse(null);
    }
}
