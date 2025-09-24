package br.com.zef.repository;

// ...existing imports...
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InvestmentRepositoryTest {

    @Test
    public void createInvestmentAndInitWallet() {
        var accountRepo = new AccountRepository();
        var investmentRepo = new InvestmentRepository();

        // criar conta com 100 centavos (R$1,00) e chave pix
        var pix = List.of("abc@pix");
        var account = accountRepo.create(pix, 100);
        assertNotNull(account);
        assertEquals(100, account.getFunds());

        // criar investimento que exige 50 centavos iniciais
        var inv = investmentRepo.create(10, 50);
        assertNotNull(inv);

        // iniciar carteira de investimento
        var wallet = investmentRepo.initInvestment(account, inv.id());
        assertNotNull(wallet);
        // após mover 50 centavos para investimento, conta deve ter 50
        assertEquals(50, account.getFunds());
        // carteira de investimento deve ter 50
        assertEquals(50, wallet.getFunds());
        // carteira deve referenciar o investimento correto
        assertEquals(inv.id(), wallet.getInvestment().id());
        // account pix deve estar disponível
        assertTrue(wallet.getAccount().getPix().contains("abc@pix"));
    }
}
