package br.com.zef;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.util.Arrays;
import java.util.Scanner;

import br.com.zef.exception.AccountNotFoundException;
import br.com.zef.exception.NoFundsEnoughException;
import br.com.zef.exception.WalletNotFoundException;
import br.com.zef.repository.AccountRepository;
import br.com.zef.repository.InvestmentRepository;

public class Main {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestmentRepository investmentRepository = new InvestmentRepository();

    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá seja bem vindo ao DIO bank");
        while (true) {
            System.out.println("Selecione a operação desejada");
            System.out.println("1 - Criar uma conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Fazer um investimento");
            System.out.println("4 - Depositar na conta");
            System.out.println("5 - Sacar da conta");
            System.out.println("6 - Transferência entre contas");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar investimento");
            System.out.println("9 - Listar contas");
            System.out.println("10 - Listar investimentos");
            System.out.println("11 - Listar carteiras de investimentos");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Histórico da conta");
            System.out.println("14 - Sair");
            var option = scan.nextInt();
            switch (option) {
                case 1 -> createAccount();
                case 2 -> createInvestment();
                case 3 -> createWalletInvestment();
                case 4 -> deposit();
                case 5 -> withdraw();
                case 6 -> transferToAccount();
                case 7 -> incInvestment();
                case 8 -> rescueInvestment();
                case 9 -> accountRepository.list().forEach(System.out::println);
                case 10 -> investmentRepository.list().forEach(System.out::println);
                case 11 -> investmentRepository.listWallets().forEach(System.out::println);
                case 12 -> {
                    investmentRepository.updateAmount();
                    System.out.println("Investimentos reajustados");
                }
                case 13 -> checkHistory();
                case 14 -> System.exit(0);
                default -> System.out.println("Opção inválida");
            }

            scan.close();
        }
    }

    private static void createAccount() {
        System.out.println("Informe as chaves pix (separadas por ';'): ");
        var pix = Arrays.stream(scan.next().split(";")).toList();
        System.out.println("Informe o valor inicial de deposito");
        var amount = scan.nextLong();
        var wallet = accountRepository.create(pix, amount);
        System.out.println("Conta criada: " + wallet);
    }

    private static void createInvestment() {
        System.out.println("Informe a taxa do investimento: ");
        var tax = scan.nextInt();
        System.out.println("Informe o valor inicial de deposito");
        var initialFunds = scan.nextLong();
        var investment = investmentRepository.create(tax, initialFunds);
        System.out.println("Investimento criado: " + investment);
    }

    private static void withdraw() {
        System.out.println("Informe a chave pix da conta para saque: ");
        var pix = scan.next();
        System.out.println("Informe o valor que será sacado: ");
        var amount = scan.nextLong();
        try {
            accountRepository.withdraw(pix, amount);
        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void deposit() {
        System.out.println("Informe a chave pix da conta para depósito: ");
        var pix = scan.next();
        System.out.println("Informe o valor que será depositado: ");
        var amount = scan.nextLong();
        try {
            accountRepository.deposit(pix, amount);
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void transferToAccount() {
        System.out.println("Informe a chave pix da conta de origem: ");
        var source = scan.next();
        System.out.println("Informe a chave pix da conta de destino: ");
        var target = scan.next();
        System.out.println("Informe o valor que será transferido: ");
        var amount = scan.nextLong();
        try {
            accountRepository.transferMoney(source, target, amount);
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void createWalletInvestment() {
        System.out.println("Informe a chave pix da conta: ");
        var pix = scan.next();
        var account = accountRepository.findByPix(pix);
        System.out.println("Informe o identificador do investimento: ");
        var investmentId = scan.nextInt();
        var investmentWallet = investmentRepository.initInvestment(account, investmentId);
        System.out.println("Conta de investimento criada: " + investmentWallet);
    }

    private static void incInvestment() {
        System.out.println("Informe a chave pix da conta para investimento: ");
        var pix = scan.next();
        System.out.println("Informe o valor que será investido: ");
        var amount = scan.nextLong();
        try {
            investmentRepository.deposit(pix, amount);
        } catch (WalletNotFoundException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void rescueInvestment() {
        System.out.println("Informe a chave pix da conta para resgate do investimento: ");
        var pix = scan.next();
        System.out.println("Informe o valor que será sacado: ");
        var amount = scan.nextLong();
        try {
            investmentRepository.withdraw(pix, amount);
        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void checkHistory() {
        System.out.println("Informe a chave pix da conta para verificar extrato:");
        var pix = scan.next();
        try {
            var sortedHistory = accountRepository.getHistory(pix);
            sortedHistory.forEach((k, v) -> {
                System.out.println(k.format(ISO_DATE_TIME));
                System.out.println(v.get(0).transactionId());
                System.out.println(v.get(0).description());
                System.out.println("R$" + (v.size() / 100) + "," + (v.size() % 100));
            });
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}