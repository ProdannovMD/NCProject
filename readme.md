Для использования, необходимо у класса LogManager вызвать метод getLogger(String name) или getLogger(Class<?> class).
После этого будет выдан объект класса Logger в соответствии с конфигурацией в файле log-config.xml (если логгера для данного имени/класса нет в конфигурации, или файл конфигурации не найден будет использован логгер по умлочанию - логирующий сообщения в консоль на уровне ERROR И ниже)

Файл log-config.xml должен находится в корне проекта, или в папке ./resources

Формат файла log-config.xml:
* Тэг ```<PatternLayout pattern="...">``` описывает паттерн для сообщений логгера. Параметр ```pattern``` - паттерн, который может содержать следующие ключевые слова:
  - ```%date | $d[{формат даты и времени}]``` - дата и время создания сообщения
  - ```%level``` - уровень лога
  - ```%logger[{точность}]``` - имя логгера
  - ```%message | %msg | %m``` - сообщение
  - ```%nano``` - время создания сообщения в наносекундах
  - ```%n``` - переход на новую строку
  - ```%threadName | %thread | %tn | %t``` - имя потока, создавшего сообщение
  - ```%threadId | %tid | %T``` - идентификатор потока, создавшего сообщение
  - ```%throwable[{количество строк}]``` - сообщение об ошибке

* Тэг ```<HTMLLayout title="..." dateTimePattern="...">``` описывает паттерн для html логов. Параметры: ```title``` - заголовок html файла, ```dateTimePattern``` - формат даты и времени

* Тэг  ```<Handlers>``` содержит описание хэндлеров:
  - ```<Console name="...">``` - Хэндлер, логирующий в консоль. Обязательный параметр: ```name``` - имя хэндлера. Может содержать ```<PatternLayout>```
  - ```<File name="..." fileName="...">``` - Хэндлер, логирующий в файл. Обязательные параметры: ```name``` - имя хэндлера, ```fileName``` - имя файла, в который будут записываться логи. Может содержать ```<PatternLayout>```
  - ```<Mail name="...">``` - Хэндлер, логирующий на электронную почту. Обязательныq параметр: ```name``` - имя хэндлера. Может содержать ```<PatternLayout>```. Для работы необходимо указать ```<Properties>``` со следующими свойствами:
    - ```mail.smtp.auth``` - использование аутентификации (по умолчанию true)
    - ```mail.smtp.starttls.enable``` - использование tls (по умолчанию true)
    - ```mail.smtp.host``` - имя SMTP хоста
    - ```mail.smtp.port``` - порт SMTP хоста
    - ```mail.smtp.ssl.trust``` - сервер, предоставляющий ssl сертификат
    - ```mail.smtp.user``` - имя пользователя
    - ```mail.smtp.password``` - пароль
    - ```mail.smtp.from``` - адрес отправителя письма
    - ```mail.smtp.to``` - адрес получателя письма
    - ```mail.smtp.subject``` - тема письма (по умолчанию "Application Logs")

* Тэг ```<HTML name="..." fileName="...">``` - Хэндлер, логирующий в html файл. Обязательные параметры: ```name``` - имя хэндлера, ```fileName``` - имя html файла, в который будут записываться логи. Может содержать ```<HTMLLayout>```

Пример файла log-config.xml:
```xml
<Configuration>
  <Handlers>
    <Console name="consoleHandler">
      <PatternLayout pattern="%d{HH:mm:ss.SSS}::%N [%t] %level %logger{36} - %msg%n"/>
    </Console>
    <File name="fileHandler" fileName="resources/logs/app-logs.txt">
      <PatternLayout pattern="to file: %d{HH:mm:ss.SSS}::%N [%t] %level %logger{36} - %msg"/>
    </File>
    <Mail name="mailHandler">
      <PatternLayout pattern="to mail: %d{HH:mm:ss.SSS}::%N [%t] %level %logger{36} - %msg%n"/>
      <Properties>
        <Property name="mail.smtp.auth" value="true"/>
        <Property name="mail.smtp.starttls.enable" value="true"/>
        <Property name="mail.smtp.host" value="smtp.mailtrap.io"/>
        <Property name="mail.smtp.port" value="2525"/>
        <Property name="mail.smtp.ssl.trust" value="smtp.mailtrap.io"/>
        <Property name="mail.smtp.user" value="9037aa6f74e463"/>
        <Property name="mail.smtp.password" value="f9ae46d93c0655"/>
        <Property name="mail.smtp.from" value="nclogger@mail.ru"/>
        <Property name="mail.smtp.to" value="prodanovmd@gmail.com"/>
        <Property name="mail.smtp.subject" value="My Application Logs"/>
      </Properties>
    </Mail>
    <HTML name="htmlHandler" fileName="resources/logs/app-logs.html">
      <HTMLLayout title="My title" dateTimePattern="HH:mm:ss.SSS"/>
    </HTML>
  </Handlers>
  <Filters>
    <Level name="errorLevelFilter" level="error" mode="equal"/>
    <Level name="infoLevelFilter" level="info" mode="equal"/>
  </Filters>
  <Loggers>
    <Logger name="com.netcracker.project.MyClass" filtersMode="or">
      <HandlerRef ref="consoleHandler"/>
      <HandlerRef ref="fileHandler"/>
      <HandlerRef ref="mailHandler"/>
      <HandlerRef ref="htmlHandler"/>
      <FilterRef ref="errorLevelFilter"/>
      <FilterRef ref="infoLevelFilter"/>
    </Logger>
  </Loggers>
</Configuration>
```
