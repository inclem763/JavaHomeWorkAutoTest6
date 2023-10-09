package ru.netology.web.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;



public class MoneyTransferTest {

    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }


    @Test
    void shouldTransferFromFirstToSecond() {
        var loginPage = new LoginPage();
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCardNumber().getCardNumber());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardNumber().getCardNumber());
        var transferPage = dashboardPage.depositToFirstCard();
        int amount = 1_000;
        transferPage.transferMoney(amount, getSecondCardNumber());
        var expectedFirstCardBalanceAfter = firstCardBalance + amount;
        var expectedSecondCardBalanceAfter = secondCardBalance - amount;
        Assertions.assertEquals(expectedFirstCardBalanceAfter, dashboardPage.getCardBalance(getFirstCardNumber().getCardNumber()));
        Assertions.assertEquals(expectedSecondCardBalanceAfter, dashboardPage.getCardBalance(getSecondCardNumber().getCardNumber()));
    }


    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardNumber().getCardNumber());
        var transferPage = dashboardPage.depositToFirstCard();
        int amount = DataHelper.generateInvalidAmount(secondCardBalance);
        transferPage.transferMoney(amount, DataHelper.getSecondCardNumber());
        transferPage.amountMoreThanBalance();
    }
}