package br.com.zef.service;

import br.com.zef.exception.AccountClosureException;
import br.com.zef.exception.AccountNotFoundException;
import br.com.zef.exception.NoFundsEnoughException;
import br.com.zef.model.AccountWallet;
import br.com.zef.model.MoneyAudit;
import br.com.zef.repository.AccountRepository;
import br.com.zef.repository.InvestmentRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class AccountService {

    private final AccountRepository accountRepository = new AccountRepository();
    private final InvestmentRepository investmentRepository = new InvestmentRepository();

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

    public void closeAccount(String pix) throws AccountNotFoundException, AccountClosureException {
        var wallet = accountRepository.findByPix(pix);

        if (wallet.getFunds() > 0) {
            throw new AccountClosureException("Conta com saldo. Saque o valor restante antes de encerrar.");
        }

        if (investmentRepository.hasInvestment(wallet)) {
            throw new AccountClosureException("Conta possui investimentos. Resgate os valores antes de encerrar.");
        }

        accountRepository.delete(wallet);
    }
}
