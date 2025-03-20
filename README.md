# Portal de Fórmula 1

Este proyecto es una aplicación web para la gestión de un portal de Fórmula 1, desarrollada como parte de un máster, donde se aplicaron la metodología SCRUM y el patrón de diseño MVC. Este proyecto se centra en la implementación de metodologías ágiles en el desarrollo de una aplicación web para la gestión de un portal de Fórmula 1, permitiendo a los aficionados acceder a noticias y votaciones, a los equipos realizar cálculos de rendimiento, y a los administradores gestionar el contenido y los usuarios del sitio.

## Tecnologías Utilizadas

* Spring Boot (versión 3.3.4)
* Base de Datos SQL (MySQL)
* Hibernate JPA
* Lombok
* Thymeleaf
* Mockito
* FullCalendar
* reCAPTCHA de Google

## Patrones de Diseño

* MVC (Modelo-Vista-Controlador)
* DAO (Data Access Object)

## Estructura del Proyecto

La organización en carpetas de este proyecto sigue una estructura basada en los patrones de diseño MVC y DAO:

* `src/main`: Contiene el código fuente de la aplicación.
    * `config`: Clases de configuración.
    * `controller`: Controladores que manejan las solicitudes HTTP.
    * `interceptors`: Interceptor de sesión para control de accesos.
    * `model`: Clases que representan las entidades de la base de datos (@Entity).
    * `repository`: Interfaces para el acceso a la base de datos (Spring Data JPA).
    * `service`: Clases con la lógica de negocio.
* `src/test`: Almacena las clases de pruebas.
* `resources`: Ficheros estáticos.
    * `images`: Imágenes estáticas.
    * `javascript`: Ficheros .js para el comportamiento de las plantillas .html.
    * `styles`: Estilos CSS.
    * `uploads`: Imágenes enviadas por los usuarios.

## Funcionalidades Principales

* **Sección Pública (Aficionados):**
    * Visualización de noticias.
    * Participación en votaciones.
    * Visualización del calendario de la temporada.
    * Visualización de la lista de equipos y pilotos.
* **Sección Privada (Equipos):**
    * Gestión de miembros del equipo.
    * Gestión de pilotos.
    * Herramientas de Simulación (cálculo de combustible y ganancia de energía ERS).
* **Sección Restringida (Administradores):**
    * Gestión de noticias, encuestas, circuitos y usuarios.
    * Gestión de equipos.
* **Usuarios Registrados:**
    * Historial de participación en encuestas.

## Configuración del Proyecto

Las configuraciones principales se encuentran en `application.properties`:

* Configuración de la Base de Datos e Hibernate JPA.
* Configuración de la Sesión.
* Configuración de Recursos Estáticos y Subidas de Archivos.
* Configuración de Vistas y Thymeleaf.
* Configuración de Logs.
* Configuración Google reCAPTCHA.

El fichero `POM.xml` gestiona las dependencias y automatiza tareas de compilación, pruebas e implementación.

## Metodología SCRUM

Este proyecto fue desarrollado utilizando la metodología SCRUM, lo que permitió una gestión ágil y adaptativa del proyecto.
