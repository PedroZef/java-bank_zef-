package br.com.zef.application;

import br.com.zef.exception.AccountNotFoundException;
import br.com.zef.exception.InvestmentNotFoundException;
import br.com.zef.exception.NoFundsEnoughException;
import br.com.zef.exception.WalletNotFoundException;
import br.com.zef.service.AccountService;
import br.com.zef.service.InvestmentService;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class BankConsoleApp {

    private final AccountService accountService;
    private final InvestmentService investmentService;
    private final Scanner scan;

    public BankConsoleApp() {
        this.accountService = new AccountService();
        this.investmentService = new InvestmentService();
        this.scan = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Olá seja bem vindo ao DIO bank");
        while (true) {
            displayMenu();
            int option = getUserOption();
            executeOption(option);
        }
    }

    private void displayMenu() {
        System.out.println("\nSelecione a operação desejada");
        System.out.println("1 - Criar uma conta");
        System.out.println("2 - Criar um tipo de investimento");
        System.out.println("3 - Fazer um investimento (Criar carteira de investimento)");
        System.out.println("4 - Depositar na conta");
        System.out.println("5 - Sacar da conta");
        System.out.println("6 - Transferência entre contas");
        System.out.println("7 - Aportar em investimento");
        System.out.println("8 - Resgatar investimento");
        System.out.println("9 - Listar contas");
        System.out.println("10 - Listar tipos de investimentos disponíveis");
        System.out.println("11 - Listar carteiras de investimentos");
        System.out.println("12 - Atualizar investimentos (aplicar rendimento)");
        System.out.println("13 - Histórico da conta");
        System.out.println("14 - Sair");
    }

    private int getUserOption() {
        try {
            System.out.print("Opção: ");
            int option = scan.nextInt();
            scan.nextLine(); // Consome a quebra de linha pendente
            return option;
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            scan.nextLine(); // Consome a entrada inválida para evitar loop infinito
            return -1; // Retorna uma opção inválida para reexibir o menu
        }
    }

    private void executeOption(int option) {
        switch (option) {
            case 1 -> createAccount();
            case 2 -> createInvestment();
            case 3 -> createWalletInvestment();
            case 4 -> deposit();
            case 5 -> withdraw();
            case 6 -> transferToAccount();
            case 7 -> incInvestment();
            case 8 -> rescueInvestment();
            case 9 -> {
                System.out.println("--- Contas Cadastradas ---");
                accountService.listAccounts().forEach(System.out::println);
            }
            case 10 -> {
                System.out.println("--- Tipos de Investimentos Disponíveis ---");
                investmentService.listInvestments().forEach(System.out::println);
            }
            case 11 -> {
                System.out.println("--- Carteiras de Investimentos Ativas ---");
                investmentService.listInvestmentWallets().forEach(System.out::println);
            }
            case 12 -> {
                investmentService.updateInvestments();
                System.out.println("Investimentos reajustados com sucesso!");
            }
            case 13 -> checkHistory();
            case 14 -> {
                System.out.println("Obrigado por usar o DIO Bank. Até mais!");
                scan.close();
                System.exit(0);
            }
            default -> System.out.println("Opção inválida. Por favor, tente novamente.");
        }
    }

    private void createAccount() {
        System.out.println("--- Criar Nova Conta ---");
        System.out.println("Informe as chaves pix (separadas por ';'): ");
        var pixInput = scan.nextLine();
        var pix = Arrays.stream(pixInput.split(";")).map(String::trim).toList();
        System.out.println("Informe o valor inicial de depósito (em centavos): ");
        var amount = getLongInput();
        if (amount == -1)
            return;

        try {
            var wallet = accountService.createAccount(pix, amount);
            System.out.println("Conta criada com sucesso: " + wallet);
        } catch (Exception ex) { // Captura exceções como PixInUseException
            System.out.println("Erro ao criar conta: " + ex.getMessage());
        }
    }

    private void createInvestment() {
        System.out.println("--- Criar Novo Tipo de Investimento ---");
        System.out.println("Informe a taxa de rendimento anual (ex: 10 para 10%): ");
        var tax = getIntInput();
        if (tax == -1)
            return;

        System.out.println("Informe o valor inicial mínimo para este investimento (em centavos): ");
        var initialFunds = getLongInput();
        if (initialFunds == -1)
            return;

        try {
            var investment = investmentService.createInvestment(tax, initialFunds);
            System.out.println("Tipo de investimento criado: " + investment);
        } catch (Exception ex) {
            System.out.println("Erro ao criar investimento: " + ex.getMessage());
        }
    }

    private void withdraw() {
        System.out.println("--- Sacar da Conta ---");
        System.out.println("Informe a chave pix da conta para saque: ");
        var pix = scan.nextLine();
        System.out.println("Informe o valor que será sacado (em centavos): ");
        var amount = getLongInput();
        if (amount == -1)
            return;

        try {
            accountService.withdraw(pix, amount);
            System.out.println("Saque de R$" + String.format("%.2f", (double) amount / 100.0)
                    + " realizado com sucesso da conta " + pix + ".");
        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println("Erro ao sacar: " + ex.getMessage());
        }
    }

    private void deposit() {
        System.out.println("--- Depositar na Conta ---");
        System.out.println("Informe a chave pix da conta para depósito: ");
        var pix = scan.nextLine();
        System.out.println("Informe o valor que será depositado (em centavos): ");
        var amount = getLongInput();
        if (amount == -1)
            return;

        try {
            accountService.deposit(pix, amount);
            System.out.println("Depósito de R$" + String.format("%.2f", (double) amount / 100.0)
                    + " realizado com sucesso na conta " + pix + ".");
        } catch (AccountNotFoundException ex) {
            System.out.println("Erro ao depositar: " + ex.getMessage());
        }
    }

    private void transferToAccount() {
        System.out.println("--- Transferência entre Contas ---");
        System.out.println("Informe a chave pix da conta de origem: ");
        var source = scan.nextLine();
        System.out.println("Informe a chave pix da conta de destino: ");
        var target = scan.nextLine();
        System.out.println("Informe o valor que será transferido (em centavos): ");
        var amount = getLongInput();
        if (amount == -1)
            return;

        try {
            accountService.transferToAccount(source, target, amount);
            System.out.println("Transferência de R$" + String.format("%.2f", (double) amount / 100.0) + " de " + source
                    + " para " + target + " realizada com sucesso.");
        } catch (AccountNotFoundException | NoFundsEnoughException ex) {
            System.out.println("Erro ao transferir: " + ex.getMessage());
        }
    }

    private void createWalletInvestment() {
        System.out.println("--- Fazer um Investimento (Criar Carteira) ---");
        System.out.println("Informe a chave pix da conta que fará o investimento: ");
        var pix = scan.nextLine();
        System.out.println("Informe o identificador do tipo de investimento (ID): ");
        var investmentId = getIntInput();
        if (investmentId == -1)
            return;

        try {
            var investmentWallet = investmentService.createWalletInvestment(pix, investmentId);
            System.out.println("Carteira de investimento criada com sucesso: " + investmentWallet);
        } catch (AccountNotFoundException | InvestmentNotFoundException | NoFundsEnoughException ex) {
            System.out.println("Erro ao criar carteira de investimento: " + ex.getMessage());
        }
    }

    private void incInvestment() {
        System.out.println("--- Aportar em Investimento ---");
        System.out.println("Informe a chave pix da conta para aportar no investimento: ");
        var pix = scan.nextLine();
        System.out.println("Informe o valor a ser aportado (em centavos): ");
        var amount = getLongInput();
        if (amount == -1)
            return;

        try {
            investmentService.incInvestment(pix, amount);
            System.out.println("Aporte de R$" + String.format("%.2f", (double) amount / 100.0)
                    + " realizado com sucesso na carteira de investimento da conta " + pix + ".");
        } catch (WalletNotFoundException | AccountNotFoundException | NoFundsEnoughException ex) {
            System.out.println("Erro ao aportar em investimento: " + ex.getMessage());
        }
    }

    private void rescueInvestment() {
        System.out.println("--- Resgatar Investimento ---");
        System.out.println("Informe a chave pix da conta para resgate do investimento: ");
        var pix = scan.nextLine();
        System.out.println("Informe o valor que será resgatado (em centavos): ");
        var amount = getLongInput();
        if (amount == -1)
            return;

        try {
            investmentService.rescueInvestment(pix, amount);
            System.out.println("Resgate de R$" + String.format("%.2f", (double) amount / 100.0)
                    + " realizado com sucesso da carteira de investimento da conta " + pix + ".");
        } catch (NoFundsEnoughException | AccountNotFoundException | WalletNotFoundException ex) {
            System.out.println("Erro ao resgatar investimento: " + ex.getMessage());
        }
    }

    private void checkHistory() {
        System.out.println("--- Histórico da Conta ---");
        System.out.println("Informe a chave pix da conta para verificar extrato:");
        var pix = scan.nextLine();
        try {
            var sortedHistory = accountService.getHistory(pix);
            if (sortedHistory.isEmpty()) {
                System.out.println("Nenhuma transação encontrada para a conta " + pix + ".");
                return;
            }
            System.out.println("Histórico de transações para a conta " + pix + ":");
            sortedHistory.forEach((k, v) -> {
                System.out.println("--------------------------------------------------");
                System.out.println("Data/Hora: " + k.format(ISO_DATE_TIME));
                // Assumindo que todos os MoneyAudit na lista para um dado timestamp têm o mesmo
                // transactionId e description
                // v.size() representa o número de centavos.
                System.out.println("ID Transação: " + v.get(0).transactionId());
                System.out.println("Descrição: " + v.get(0).description());
                System.out.printf("Valor: R$%.2f%n", (double) v.size() / 100.0); // Formatação correta para R$X.YY
            });
            System.out.println("--------------------------------------------------");
        } catch (AccountNotFoundException ex) {
            System.out.println("Erro ao verificar histórico: " + ex.getMessage());
        }
    }

    // Método auxiliar para obter entrada de inteiro com tratamento de erro
    private int getIntInput() {
        try {
            int value = scan.nextInt();
            scan.nextLine(); // Consome a quebra de linha
            return value;
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, digite um número inteiro.");
            scan.nextLine(); // Consome a entrada inválida
            return -1; // Indica erro
        }
    }

    // Método auxiliar para obter entrada de long com tratamento de erro
    private long getLongInput() {
        try {
            long value = scan.nextLong();
            scan.nextLine(); // Consome a quebra de linha
            return value;
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, digite um número inteiro longo.");
            scan.nextLine(); // Consome a entrada inválida
            return -1; // Indica erro
        }
    }
}