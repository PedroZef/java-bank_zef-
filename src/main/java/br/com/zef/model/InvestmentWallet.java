package br.com.zef.model;

import static br.com.zef.model.BankService.INVESTMENT;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public class InvestmentWallet extends Wallet {

    private final Investment investment;
    private final AccountWallet account;

    public InvestmentWallet(Investment investment, AccountWallet account, final long amount) {
        super(INVESTMENT);
        this.investment = investment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getServiceType(), "Investimento");
    }

    public void updateAmount(final long percent) {
        var amount = getFunds() * percent / 100;
        var history = new MoneyAudit(UUID.randomUUID(), getServiceType(), "Rendimentos", OffsetDateTime.now());
        var money = Stream.generate(() -> new Money(history)).limit(amount).toList();
        this.money.addAll(money);
    }

    @Override
    public String toString() {
        return super.toString() + "InvestmentWallet{" +
                "investment=" + investment +
                ", account=" + account +
                '}';
    }

    public AccountWallet getAccount() {
        return this.account;
    }

    public Investment getInvestment() {
        return this.investment;
    }

    @Override
    public java.util.List<String> getPix() {
        return this.account.getPix();
    }
}