package br.com.zef.service;

import br.com.zef.exception.AccountNotFoundException;
import br.com.zef.exception.NoFundsEnoughException;
import br.com.zef.exception.WalletNotFoundException;
import br.com.zef.model.Investment;
import br.com.zef.model.InvestmentWallet;
import br.com.zef.repository.AccountRepository;
import br.com.zef.repository.InvestmentRepository;

import java.util.List;

public class InvestmentService {

    private final InvestmentRepository investmentRepository = new InvestmentRepository();
    private final AccountRepository accountRepository = new AccountRepository();

    public Investment createInvestment(int tax, long initialFunds) {
        return investmentRepository.create(tax, initialFunds);
    }

    public InvestmentWallet createWalletInvestment(String pix, int investmentId) {
        var account = accountRepository.findByPix(pix);
        return investmentRepository.initInvestment(account, investmentId);
    }

    public void incInvestment(String pix, long amount) throws WalletNotFoundException, AccountNotFoundException {
        investmentRepository.deposit(pix, amount);
    }

    public void rescueInvestment(String pix, long amount) throws NoFundsEnoughException, AccountNotFoundException {
        investmentRepository.withdraw(pix, amount);
    }

    public void updateInvestments() {
        investmentRepository.updateAmount();
    }

    public List<Investment> listInvestments() {
        return investmentRepository.list();
    }

    public List<InvestmentWallet> listInvestmentWallets() {
        return investmentRepository.listWallets();
    }
}
