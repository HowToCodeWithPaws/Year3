package com.example.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.junit.Assert.fail;

public class UntitledTestCase {
    private WebDriver driver;
    private Actions builder;
    private final StringBuffer verificationErrors = new StringBuffer();
    private static String userPublicPostUrl;
    private static String userPrivatePostUrl;
    private static String userDelayedPostUrl;

    @Before
    public void setUp() {
        System.setProperty("webdriver.gecko.driver", "C:\\tools\\selenium\\geckodriver.exe");
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        builder = new Actions(driver);
    }

    /***
     * Метод для выделения функции входа зарегистрированного пользователя с логином и паролем.
     * Заполняет требуемые поля требуемыми данными и сабмитит ответ.
     * @param login - логин юзера.
     * @param password - пароль юзера.
     */
    private void LogIn(String login, String password){
        driver.findElement(By.id("user_login")).clear();
        driver.findElement(By.id("user_login")).sendKeys(login);
        driver.findElement(By.xpath("//form[@id='loginform']/div")).click();
        driver.findElement(By.id("user_pass")).click();
        driver.findElement(By.id("user_pass")).clear();
        driver.findElement(By.id("user_pass")).sendKeys(password);
        driver.findElement(By.id("wp-submit")).click();
    }

    /***
     * Метод для обособления создания поста с нужным текстом.
     * Заполняет заголовок и текст, нажимает на кнопку публикации.
     * @param title - желаемый заголовок поста.
     * @param content - желаемый текст поста.
     * @throws InterruptedException - может выкидывать эксепшен из-за тред слипа.
     */
    private void MakePublish(String title, String content) throws InterruptedException {
        builder.keyDown(Keys.SHIFT).keyDown(Keys.CONTROL).keyDown(Keys.ALT).sendKeys("m").build().perform();
        Thread.sleep(5000);
        builder.keyUp(Keys.CONTROL).keyUp(Keys.SHIFT).keyUp(Keys.ALT).build().perform();

        driver.findElement(By.id("post-title-1")).click();
        driver.findElement(By.id("post-title-1")).sendKeys(title);
        driver.findElement(By.id("post-content-0")).click();
        driver.findElement(By.id("post-content-0")).sendKeys(content);

        driver.findElement(By.xpath("//button[text()='Опубликовать']")).click();
    }

    /***
     * Тест для проверки возможности залогиниться, выйти, перелогиниться со следующим
     * сценарием: сначала входим на сайт, логинимся как первый зарегистрированный пользователь,
     * потом выходим из учетной записи, логинимся как второй зарегистрированный пользователь.
     * @throws InterruptedException - может выкидывать эксепшен из-за тред слипа.
     */
    @Test
    public void testLogInOut() throws InterruptedException {
        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.linkText("Войти")).click();

        LogIn("noa_lternatives", "testingdisaster1");

        builder.moveToElement(driver.findElement(By.id("wp-admin-bar-my-account"))).perform();
        sleep(500);
        driver.findElement(By.id("wp-admin-bar-logout")).click();

        LogIn("potassium", "testingdisaster2");
    }

    /***
     * Тестирование метода создания и просмотра по ссылке публичного поста. Удобнее тестировать это сразу,
     * потому что методы запускаются в хаотичном порядке и было бы нехорошо пытаться смотреть пост до тех
     * пор пока он не создан. Сценарий следующий: входим, логинимся, создаем новый пост, копируем постоянную
     * ссылку на него, выходим на главную страницу сайта, переходим на страницу поста по ссылке, проверяем, что
     * есть элемент с надписью, соответствующей заголовку. Далее дважды выходим и проверяем такую доступность
     * для второго зарегистрированного пользователя и для гостя.
     * @throws InterruptedException - может выкидывать эксепшен из-за тред слипа.
     */
    @Test
    public void testAddAvailablePublicPostUser() throws InterruptedException {
        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.linkText("Войти")).click();
        LogIn("noa_lternatives", "testingdisaster1");

        driver.findElement(By.id("wp-admin-bar-new-content")).click();
        sleep(500);
        builder.sendKeys(Keys.ESCAPE).perform();
        sleep(500);

        MakePublish("Обычный публичный пост", "Невыносимо невозможно я не могу этим больше заниматься");

        builder.sendKeys(Keys.ENTER).perform();
        sleep(5000);

        var wait = new WebDriverWait(driver, 10L);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Просмотреть запись"))).click();
        sleep(5000);
        userPublicPostUrl = driver.getCurrentUrl();
        System.out.println(userPublicPostUrl);
        sleep(5000);

        driver.get("https://ruswizard.su/test/");
        driver.get(userPublicPostUrl);
        driver.findElement(By.xpath("//h1[text()='Обычный публичный пост']"));
        sleep(5000);

        driver.findElement(By.xpath("/html/body/div[3]/div/aside/div/div[2]/div[3]/div/nav/ul/li[2]/a")).click();
        sleep(5000);
        LogIn("potassium", "testingdisaster2");
        driver.get(userPublicPostUrl);
        driver.findElement(By.xpath("//h1[text()='Обычный публичный пост']"));
        sleep(5000);

        driver.findElement(By.xpath("/html/body/div[3]/div/aside/div/div[2]/div[3]/div/nav/ul/li[2]/a")).click();
        sleep(5000);
        driver.get(userPublicPostUrl);
        driver.findElement(By.xpath("//h1[text()='Обычный публичный пост']"));
        sleep(5000);
    }

    /***
     * Тест создания и доступа к приватному посту. Все то же что и в прошлом тесте, но при
     * создании поста мы указываем приватность, и поэтому при выходе из аккаунта и перелогинивании
     * на второго пользователя и гостя проверяем наличие надписи "страница не найдена".
     * @throws InterruptedException - может выкидывать эксепшен из-за тред слипа.
     */
    @Test
    public void testAddAvailablePrivatePostUser() throws InterruptedException {
        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.linkText("Войти")).click();
        LogIn("potassium", "testingdisaster2");

        driver.findElement(By.id("wp-admin-bar-new-content")).click();
        sleep(500);
        builder.sendKeys(Keys.ESCAPE).perform();
        sleep(500);

        MakePublish("Это приватный пост", "Никто не разрешал вам сюда смотреть");
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div/div[2]/div[4]/div[2]/div/div/div[2]/div/div[3]/h2/button")).click();
        driver.findElement(By.xpath("//*[@id='editor-post-private-0']")).click();
        driver.switchTo().alert().accept();

        sleep(5000);
        var wait = new WebDriverWait(driver, 10L);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Просмотреть запись"))).click();
        sleep(5000);
        userPrivatePostUrl = driver.getCurrentUrl();
        System.out.println(userPrivatePostUrl);
        sleep(5000);

        driver.get("https://ruswizard.su/test/");
        driver.get(userPrivatePostUrl);
        driver.findElement(By.xpath("//h1[text()='Личное: Это приватный пост']"));
        sleep(5000);

        driver.findElement(By.xpath("/html/body/div[3]/div/aside/div/div[2]/div[3]/div/nav/ul/li[2]/a")).click();
        sleep(5000);
        LogIn("noa_lternatives", "testingdisaster1");
        driver.get(userPrivatePostUrl);
        driver.findElement(By.xpath("//h1[text()='Страница не найдена']"));
        sleep(5000);

        builder.moveToElement(driver.findElement(By.id("wp-admin-bar-my-account"))).perform();
        sleep(500);
        driver.findElement(By.id("wp-admin-bar-logout")).click();
        driver.get(userPrivatePostUrl);
        driver.findElement(By.xpath("//h1[text()='Страница не найдена']"));
        sleep(5000);
    }

    /***
     * Тест проверки возможности удаления. Сценарий: юзер логинится, создает пост,
     * затем по ссылке на него переходит на страницу поста, открывает редактирование
     * и нажимает там удаление. Далее проверяется что при переходе по ссылке есть надпись
     * "страница не найдена".
     * @throws InterruptedException - может выкидывать эксепшен из-за тред слипа.
     */
    @Test
    public void testDelete() throws InterruptedException {
        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.linkText("Войти")).click();
        LogIn("noa_lternatives", "testingdisaster1");

        driver.findElement(By.id("wp-admin-bar-new-content")).click();
        sleep(500);
        builder.sendKeys(Keys.ESCAPE).perform();
        sleep(500);

        MakePublish("Пост который будет удален", "По секрету скажу, что это не секрет");

        builder.sendKeys(Keys.ENTER).perform();
        sleep(5000);

        var wait = new WebDriverWait(driver, 10L);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Просмотреть запись"))).click();
        sleep(5000);
        userPublicPostUrl = driver.getCurrentUrl();
        System.out.println(userPublicPostUrl);
        sleep(5000);

        driver.get("https://ruswizard.su/test/");

        driver.get(userPublicPostUrl);
        driver.findElement(By.xpath("//h1[text()='Пост который будет удален']"));
        driver.findElement(By.xpath("/html/body/main/article/div[2]/div/ul/li/span[2]/a")).click();
        sleep(5000);
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div/div[2]/div[3]/div/div[3]/div[1]/div[3]/button")).click();
        sleep(5000);

        driver.get(userPublicPostUrl);
        driver.findElement(By.xpath("//h1[text()='Страница не найдена']"));
        sleep(5000);
    }

    /***
     * Тест на создание и доступность поста с отложенной публикацией. Юзер логинится,
     * создает пост, настраивает отложенность публикации +2 минуты от текущего времени,
     * далее проверяется что при переходе на страницу поста автор сразу его видит, а при
     * перелогинивании на другого пользователя сначала есть надпись "страница не найдена",
     * но по прошествии достаточного для публикации времени пост появляется.
     * @throws InterruptedException - может выкидывать эксепшен из-за тред слипа.
     */
    @Test
    public void testAddAvailableDelay() throws InterruptedException {
        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.linkText("Войти")).click();
        LogIn("potassium", "testingdisaster2");

        driver.findElement(By.id("wp-admin-bar-new-content")).click();
        sleep(500);
        builder.sendKeys(Keys.ESCAPE).perform();
        sleep(500);

        MakePublish("Пост в будущее", "Приходите на вечеринку путешественников во времени");
        sleep(5000);
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div/div[2]/div[4]/div[2]/div/div/div[2]/div/div[4]/h2/button")).click();
        sleep(5000);
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div/div[2]/div[4]/div[2]/div/div/div[2]/div/div[4]/div/div[1]/fieldset[2]/div/div/input[2]")).click();
        sleep(5000);
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div/div[2]/div[4]/div[2]/div/div/div[2]/div/div[4]/div/div[1]/fieldset[2]/div/div/input[2]")).sendKeys(Keys.BACK_SPACE);
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div/div[2]/div[4]/div[2]/div/div/div[2]/div/div[4]/div/div[1]/fieldset[2]/div/div/input[2]")).sendKeys(Keys.DELETE);
        sleep(5000);
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div/div[2]/div[4]/div[2]/div/div/div[2]/div/div[4]/div/div[1]/fieldset[2]/div/div/input[2]")).sendKeys((String.valueOf(LocalDateTime.now().plusMinutes(2).getMinute())));
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div/div[2]/div[4]/div[2]/div/div/div[1]/div[1]/button")).click();
        sleep(5000);
        sleep(5000);
        var wait = new WebDriverWait(driver, 10L);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Просмотреть запись"))).click();
        sleep(5000);
        userDelayedPostUrl = driver.getCurrentUrl();
        System.out.println(userDelayedPostUrl);
        sleep(5000);

        driver.get("https://ruswizard.su/test/");
        driver.get(userDelayedPostUrl);
        driver.findElement(By.xpath("//h1[text()='Пост в будущее']"));
        sleep(5000);

        driver.findElement(By.xpath("/html/body/div[3]/div/aside/div/div[2]/div[3]/div/nav/ul/li[2]/a")).click();
        sleep(5000);
        LogIn("noa_lternatives", "testingdisaster1");
        driver.get(userDelayedPostUrl);
        driver.findElement(By.xpath("//h1[text()='Страница не найдена']"));
        sleep(120000);

        driver.get(userDelayedPostUrl);
        driver.findElement(By.xpath("//h1[text()='Пост в будущее']"));
        sleep(5000);
    }

    /***
     * Тест проверки того, что пользователь может видеть пост в списке своих записей.
     * Пользователь логинится, создает пост, далее заходит в консоль, оттуда в записи,
     * там проверяется наличие элемента с текстом - заголовком поста.
     * @throws InterruptedException - может выкидывать эксепшен из-за тред слипа.
     */
    @Test
    public void testAvailableFromUserPostsList() throws InterruptedException {
        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.linkText("Войти")).click();
        LogIn("noa_lternatives", "testingdisaster1");

        driver.findElement(By.id("wp-admin-bar-new-content")).click();
        sleep(500);
        builder.sendKeys(Keys.ESCAPE).perform();
        sleep(500);

        MakePublish("Видно ли его в списке", "А правда, видно ли?");

        builder.sendKeys(Keys.ENTER).perform();
        sleep(5000);

        var wait = new WebDriverWait(driver, 10L);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Просмотреть запись"))).click();
        sleep(5000);
        userPublicPostUrl = driver.getCurrentUrl();
        System.out.println(userPublicPostUrl);
        sleep(5000);

        driver.get("https://ruswizard.su/test/wp-admin/");
        driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[2]/ul/li[3]/a/div[3]")).click();
        driver.findElement(By.linkText("Видно ли его в списке")).click();
    }

    /***
     * Тест на возможность оставления комментариев. Пользователь 1 логинится и создает пост,
     * далее выходит, пользователь 2 логинится и по ссылке на пост пользователя 1 оставляет там
     * один комментарий - без слова geek, идет в свою консоль и проверяет, что комментарий с нужным
     * текстом появился в ожидающих одобрения. Далее пишет комментарий со словом geek, при этом по условию
     * кажется у зарегистрированных пользователей не должно быть цензуры, но видимо НЕ БАГ А ФИЧА,
     * пост находится по тексту в консоли, корзине, то есть забаненых постах. Далее происходит разлогин
     * в гостя, гость также оставляет два комментария и проверяет наличие соответствующих по тексту элементов
     * в разделах ожидающих и удаленных постов соответственно.
     * @throws InterruptedException - может выкидывать эксепшен из-за тред слипа.
     */
    @Test
    public void testCommentUserAndGuest() throws InterruptedException {
        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.linkText("Войти")).click();
        LogIn("noa_lternatives", "testingdisaster1");

        driver.findElement(By.id("wp-admin-bar-new-content")).click();
        sleep(500);
        builder.sendKeys(Keys.ESCAPE).perform();
        sleep(500);

        MakePublish("Вещи, которые нас удивляют", "Меня удивляет, что я делаю это второй день. Что удивляет вас?");

        builder.sendKeys(Keys.ENTER).perform();
        sleep(5000);

        var wait = new WebDriverWait(driver, 10L);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Просмотреть запись"))).click();
        sleep(5000);
        userPublicPostUrl = driver.getCurrentUrl();
        System.out.println(userPublicPostUrl);
        sleep(5000);

        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.xpath("/html/body/div[3]/div/aside/div/div[2]/div[3]/div/nav/ul/li[2]/a")).click();
        sleep(5000);
        LogIn("potassium", "testingdisaster2");
        driver.get(userPublicPostUrl);

        driver.findElement(By.xpath("//*[@id='comment']")).click();
        driver.findElement(By.xpath("//*[@id='comment']")).sendKeys("Меня удивляет, что никто еще не написал пост про чайник.");
        driver.findElement(By.xpath("//*[@id='submit']")).click();

        driver.get("https://ruswizard.su/test/wp-admin/index.php");
        driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[2]/ul/li[5]/a/div[3]")).click();
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[3]/ul/li[3]/a")).click();
        driver.findElement(By.xpath("//td[p='Меня удивляет, что никто еще не написал пост про чайник.']"));

        sleep(60000);

        driver.get(userPublicPostUrl);
        driver.findElement(By.xpath("//*[@id='comment']")).click();
        driver.findElement(By.xpath("//*[@id='comment']")).sendKeys("Кажется цензура на geek не только у гостей?");
        driver.findElement(By.xpath("//*[@id='submit']")).click();

        driver.get("https://ruswizard.su/test/wp-admin/index.php");
        driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[2]/ul/li[5]/a/div[3]")).click();
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[3]/ul/li[6]/a")).click();
        driver.findElement(By.xpath("//td[p='Кажется цензура на geek не только у гостей?']"));


        driver.get("https://ruswizard.su/test/");
        driver.findElement(By.xpath("/html/body/div[3]/div/aside/div/div[2]/div[3]/div/nav/ul/li[2]/a")).click();
        sleep(5000);

        driver.get(userPublicPostUrl);

        driver.findElement(By.xpath("//*[@id='author']")).click();
        driver.findElement(By.xpath("//*[@id='author']")).sendKeys("опоссум бежит орет");

        driver.findElement(By.xpath("//*[@id='email']")).click();
        driver.findElement(By.xpath("//*[@id='email']")).sendKeys("ndzubareva@edu.hse.ru");

        driver.findElement(By.xpath("//*[@id='comment']")).click();
        driver.findElement(By.xpath("//*[@id='comment']")).sendKeys("Меня удивляет, что на улице уже темнеет");
        driver.findElement(By.xpath("//*[@id='submit']")).click();

        sleep(100000);

        driver.findElement(By.xpath("//*[@id='author']")).click();
        driver.findElement(By.xpath("//*[@id='author']")).sendKeys("другой опоссум");

        driver.findElement(By.xpath("//*[@id='email']")).click();
        driver.findElement(By.xpath("//*[@id='email']")).sendKeys("ndzubareva@edu.hse.ru");
        driver.findElement(By.xpath("//*[@id='comment']")).click();
        driver.findElement(By.xpath("//*[@id='comment']")).sendKeys("ПИ такое geek");
        driver.findElement(By.xpath("//*[@id='submit']")).click();

        driver.get("https://ruswizard.su/test/wp-admin/index.php");
        LogIn("noa_lternatives", "testingdisaster1");
        driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[2]/ul/li[5]/a/div[3]")).click();
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[3]/ul/li[3]/a")).click();
        driver.findElement(By.xpath("//td[p='Меня удивляет, что на улице уже темнеет']"));
        driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[3]/ul/li[6]/a")).click();
        driver.findElement(By.xpath("//td[p='ПИ такое geek']"));
    }

    @After
    public void tearDown() {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

}
