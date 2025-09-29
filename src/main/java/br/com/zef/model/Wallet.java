package br.com.zef.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class Wallet {

    @Getter
    private final BankService serviceType;

    protected final List<Money> money;

    protected Wallet(BankService serviceType) {
        this.serviceType = serviceType;
        this.money = new ArrayList<>();
    }

    protected List<Money> generateMoney(final long amount, final String description) {
        var history = new MoneyAudit(UUID.randomUUID(), serviceType, description, OffsetDateTime.now());
        return Stream.generate(() -> new Money(history)).limit(amount).toList();
    }

    public long getFunds() {
        return money.size();
    }

    public void addMoney(final List<Money> money, final BankService serviceType, final String description) {
        var history = new MoneyAudit(UUID.randomUUID(), serviceType, description, OffsetDateTime.now());
        money.forEach(m -> m.addHistory(history));
        this.money.addAll(money);
    }

    public List<Money> reduceMoney(final long amount) {
        int amountToRemove = (int) amount;
        List<Money> toRemove = new ArrayList<>(money.subList(0, amountToRemove));
        money.subList(0, amountToRemove).clear();
        return toRemove;
    }

    public List<MoneyAudit> getFinancialTransactions() {
        return money.stream().flatMap(m -> m.getHistory().stream()).toList();
    }

    public void reduceFunds(final long amount) {
        int amountToRemove = (int) amount;
        money.subList(0, amountToRemove).clear();
    }


    @Override
    public String toString() {
        return "Wallet{" +
                "service=" + serviceType +
                ", money= R$" + money.size() / 100 + "," + money.size() % 100 +
                '}';
    }

    public abstract List<String> getPix();

}