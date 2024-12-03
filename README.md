### Bootcamp NTT DATA Girl Power - JAVA

Microservicio de Transacciones

1. Clonar el repositorio o descargar zip

2. Abrir proyecto en intelliJ

3. Conexión de Base de datos con MongoDB 

   Crear la BD "bootcampbd"

4. Crear la Collection "transaction" 

5. Considerar la conexión de Mongo Compass:

    Local : spring.data.mongodb.uri=mongodb://localhost:27017
  
    Mongo Atlas: spring.data.mongodb.uri=mongodb+srv://"USUARIO":"CONTRASEÑA"@cluster0.kc9nmdx.mongodb.net/


6. Correr el main con Shift+F10

    Aplicación lista para realizar pruebas en `POSTMAN`

7. Para visualizar las pruebas unitarias en la carpeta src>test

8. Para ver el informe de jacoco ejecutar el comando mvn clean test y en la carpeta target>site>jacoco-resources>index.html dar click derecho y "Open in" seleccionamos "Explorer"
