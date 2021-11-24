Для использования, необходимо у класса LogManager вызвать метод getLogger(String name) или getLogger(Class<?> class).
После этого будет выдан объект класса Logger в соответствии с конфигурацией в файле log-config.xml (если логгера для данного имени/класса нет в конфигурации, или файл конфигурации не найден будет использован логгер по умлочанию - логирующий сообщения в консоль на уровне ERROR И ниже)

Файл log-config.xml должен находится в корне проекта, или в папке ./resources
Формат файла log-config.xml:
<Configuration>
  <Handlers>
    <Console name="handler1">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
    ...
  </Handlers>
  <Filters>
    <Level name="filter1" level="info"/>
    ...
  </Filters>
  <Loggers>
    <Logger name="com.netcracker.project.MyClass">
      <HandlerRef ref="handler1"/>
      <FilterRef ref="filter1"/>
    </Logger>
    ...
  </Loggers>
</Configuration>
