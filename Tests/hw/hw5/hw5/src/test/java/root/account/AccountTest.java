package root.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    /***
     * Тест на метод задания максимального кредита. Как и требуется,
     * при заблокированном аккаунте и нормальном аргументе максимальный
     * кредит меняется. Также при размере максимального кредита большем
     * по абсолютной величине чем bound функция возвращает false, как и надо.
     */
    @Test
    void setMaxCredit() {
        Account account = new Account();
        account.block();
        assertTrue(account.setMaxCredit(100));
        assertEquals(100, account.getMaxCredit());

        assertTrue(account.setMaxCredit(1000000));
        assertEquals(1000000, account.getMaxCredit());

        assertFalse(account.setMaxCredit(1000001));
        assertEquals(1000000, account.getMaxCredit());
        assertFalse(account.setMaxCredit(-1000001));
        assertEquals(1000000, account.getMaxCredit());
    }

    /***
     * тест на то, что значение максимального кредита
     * можно менять только у заблокированного аккаунта.
     * А вот и нет, говорит код. У разблокированного тоже возвращает true.
     */
    @Test
    void setMaxCreditUnblocked() {
        Account account = new Account();
        assertFalse(account.setMaxCredit(100));
        assertEquals(1000, account.getMaxCredit());
    }

    /***
     * тест на то что собственно меняется значение максимального
     * кредита при вызове функции у разблокированного аккаунта, а не только
     * возвращается true.
     */
    @Test
    void setMaxCreditUnblockedValueChanged() {
        Account account = new Account();
        account.setMaxCredit(100);
        assertEquals(1000, account.getMaxCredit());
    }

    /***
     * Тест на метод внесения суммы. Как и требуется,
     * сумма вносится, если она соответствует требованиям,
     * если же она отрицательна или больше bound, то
     * внесение не происходит. Также не происходит внесение суммы
     * на заблокированный аккаунт.
     */
    @Test
    void deposit() {
        Account account = new Account();

        assertTrue(account.deposit(10));
        assertEquals(10, account.getBalance());

        assertFalse(account.deposit(-10000));
        assertEquals(10, account.getBalance());

        assertFalse(account.deposit(1000001));
        assertEquals(10, account.getBalance());

        account.block();
        assertTrue(account.isBlocked());
        assertFalse(account.deposit(1));
    }

    /***
     * тест на функцию внесения суммы: хотя
     * по требованиям не должно получаться внести сумму,
     * которая сделает баланс больше bound, это возможно.
     */
    @Test
    void depositMax() {
        Account account = new Account();
        account.deposit(1000);
        assertEquals(1000, account.getBalance());

        assertFalse(account.deposit(999001));
        assertEquals(1000, account.getBalance());
    }

    /***
     * Тест на метод списывания суммы. Метод работает корректно на
     * нормальных значениях, списывая требуемую сумму, не списывает при
     * аргументе отрицательном и большем bound. Также, как и требуется,
     * не списывает сумму при заблокированном аккаунте.
     */
    @Test
    void withdraw() {
        Account account = new Account();
        assertTrue(account.withdraw(10));
        assertEquals(-10, account.getBalance());

        assertFalse(account.withdraw(-10000));
        assertEquals(-10, account.getBalance());

        assertFalse(account.withdraw(1000001));
        assertEquals(-10, account.getBalance());

        account.block();
        assertTrue(account.isBlocked());
        assertFalse(account.withdraw(1));
    }

    /***
     * тест на попытку списать сумму равную максимальному
     * кредиту. По требованиям это должно работать, однако не работает.
     */
    @Test
    void withdrawMax() {
        Account account = new Account();
        assertTrue(account.withdraw(account.getMaxCredit()));
        assertEquals(account.maxCredit, account.getBalance());
    }

    /***
     * Тест геттера баланса: он действительно правильно выводит
     * изменяющийся баланс.
     */
    @Test
    void getBalance() {
        Account account = new Account();
        assertEquals(0, account.getBalance());
        account.deposit(100);
        assertEquals(100, account.getBalance());
        account.deposit(-100);
        assertEquals(100, account.getBalance());
    }

    /***
     * Тест геттера максимального кредита: все работает корректно.
     */
    @Test
    void getMaxCredit() {
        Account account = new Account();
        assertEquals(1000, account.getMaxCredit());
        account.block();
        account.setMaxCredit(2000);
        assertEquals(2000, account.getMaxCredit());
        account.setMaxCredit(1000001);
        assertNotEquals(1000001, account.getMaxCredit());
    }

    /***
     * Тест геттера состояния блокировки: все работает корректно.
     */
    @Test
    void isBlocked() {
        Account account = new Account();
        assertFalse(account.isBlocked());
        account.block();
        assertTrue(account.isBlocked());
        account.unblock();
        assertFalse(account.isBlocked());
    }

    /***
     * Тест метода блокировки, все нормально блокируется.
     */
    @Test
    void block() {
        Account account = new Account();
        assertFalse(account.isBlocked());
        account.block();
        assertTrue(account.isBlocked());
    }

    /***
     * Тест на метод unblock, проверяется что оно действительно
     * разблокирует заблокированный (и не заблокированный) аккаунт.
     */
    @Test
    void unblock() {
        Account account = new Account();
        assertTrue(account.withdraw(10));
        assertTrue(account.unblock());
        account.block();
        assertTrue(account.setMaxCredit(1));
        assertFalse(account.unblock());
    }

    /***
     * Дополнительные тесты для убийства мутантов
     */

    @Test
    void M1() {
        Account account = new Account();
        assertTrue(account.deposit(0));
    }

    @Test
    void M2() {
        Account account = new Account();
        assertTrue(account.deposit(1000000));
    }

    @Test
    void M3() {
        Account account = new Account();
        assertTrue(account.deposit(100));
        assertTrue(account.deposit(999900));
    }

    @Test
    void M4() {
        Account account = new Account();
        assertTrue(account.withdraw(0));
    }

    @Test
    void M5() {
        Account account = new Account();
        account.block();
        assertTrue(account.setMaxCredit(1000000));
        assertTrue(account.unblock());
        assertTrue(account.withdraw(1000000));
    }

    @Test
    void M67() {
        Account account = new Account();
        assertFalse(account.withdraw(1001));
    }

    @Test
    void M8() {
        Account account = new Account();
        assertTrue(account.withdraw(1000));
        assertTrue(account.unblock());
    }

    @Test
    void M9() {
        Account account = new Account();
        account.block();
        assertTrue(account.setMaxCredit(-1000000));
        assertEquals(-1000000, account.getMaxCredit());
        assertFalse(account.setMaxCredit(-1000001));
    }
}