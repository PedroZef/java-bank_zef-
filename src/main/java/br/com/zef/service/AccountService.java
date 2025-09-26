package br.com.zef.service;

import br.com.zef.exception.AccountNotFoundException;
import br.com.zef.exception.NoFundsEnoughException;
import br.com.zef.model.AccountWallet;
import br.com.zef.model.MoneyAudit;
import br.com.zef.repository.AccountRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class AccountService {

    private final AccountRepository accountRepository = new AccountRepository();

    public AccountWallet createAccount(List<String> pix, long amount) {
        return accountRepository.create(pix, amount);
    }

    public void withdraw(String pix, long amount) throws NoFundsEnoughException, AccountNotFoundException {
        accountRepository.withdraw(pix, amount);
    }

    public void deposit(String pix, long amount) throws AccountNotFoundException {
        accountRepository.deposit(pix, amount);
    }

    public void transferToAccount(String source, String target, long amount) throws AccountNotFoundException {
        accountRepository.transferMoney(source, target, amount);
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(String pix) throws AccountNotFoundException {
        return accountRepository.getHistory(pix);
    }

    public List<AccountWallet> listAccounts() {
        return accountRepository.list();
    }
}
