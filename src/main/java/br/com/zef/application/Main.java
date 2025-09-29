package br.com.zef.application;

// Removidas importações não utilizadas após a refatoração
// import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
// import java.util.Arrays;
// import java.util.Scanner;
// import br.com.zef.exception.AccountNotFoundException;
// import br.com.zef.exception.NoFundsEnoughException;
// import br.com.zef.exception.WalletNotFoundException;
// import br.com.zef.service.AccountService;
// import br.com.zef.service.InvestmentService;

public class Main {

    public static void main(String[] args) {
        BankConsoleApp app = new BankConsoleApp();
        app.run();
    }
}
