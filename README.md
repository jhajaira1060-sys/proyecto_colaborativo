# Reto Colaborativo 📱

Aplicación móvil para Android desarrollada en **Kotlin** que implementa un flujo completo de autenticación de usuarios mediante una API REST (`DummyJSON`), utilizando arquitectura moderna basada en **Retrofit**, **Corrutinas de Kotlin** y componentes de **AndroidX**.

---

## 🚀 Características Principales

- **Autenticación REST**: Conexión con servicio web mediante `POST /auth/login` para validación de credenciales y obtención de token de acceso.
- **Peticiones Protegidas**: Consumo del endpoint seguro `GET /auth/me` enviando el token de autorización en la cabecera (`Bearer Token`).
- **Gestión Asíncrona**: Uso de corrutinas (`lifecycleScope`) para evitar bloqueos en el hilo principal durante las llamadas de red.
- **Depuración de Red**: Integración de `HttpLoggingInterceptor` de OkHttp para inspección de peticiones y respuestas HTTP en el Logcat.
- **Diseño Adaptativo**: Soporte para *Edge-to-Edge* y componentes de Material Design 3.

---

## 🛠️ Tecnologías y Librerías

- **Lenguaje:** Kotlin
- **Cliente HTTP:** [Retrofit 2](https://square.github.io/retrofit/) (versión 2.11.0)
- **Serialización JSON:** Gson Converter (`converter-gson`)
- **Logging HTTP:** OkHttp Logging Interceptor
- **Concurrencia:** Kotlin Coroutines (`kotlinx-coroutines-android`)
- **UI & AndroidX:** ConstraintLayout, Material Components, AppCompat, EdgeToEdge.

---

## 📂 Estructura del Código Fuente

```text
app/src/main/java/com/example/retocolaborativo/
├── ApiService.kt            # Definición de endpoints (login y perfil)
├── LoginRequest.kt          # Modelo de datos para solicitud de login
├── LoginResponse.kt         # Modelo de datos para respuesta de login
├── UserResponse.kt          # Modelo de datos para información del usuario
├── RetrofitClient.kt        # Configuración Singleton de Retrofit y OkHttpClient
├── MainActivity.kt          # Pantalla de Login y lógica de autenticación
└── activity_perfil.kt       # Pantalla de perfil de usuario autenticado
```

---

## 🔑 Credenciales de Prueba

La aplicación incluye credenciales de prueba prellenadas automáticamente en la pantalla de inicio de sesión (gracias a la API pública de prueba [DummyJSON](https://dummyjson.com/)):

- **Usuario:** `emilys`
- **Contraseña:** `emilyspass`

---

##  Requisitos y Ejecución

1. **Requisitos previos:**
   - [Android Studio](https://developer.android.com/studio) (última versión recomendada).
   - Android SDK con compilación en nivel **37** (mínimo SDK 24).
   - Dispositivo físico o emulador Android con conexión a internet.

2. **Pasos para ejecutar:**
   - Clonar o abrir el proyecto en Android Studio.
   - Sincronizar el proyecto con Gradle (`Sync Project with Gradle Files`).
   - Ejecutar la aplicación (`Run 'app'`) seleccionando un emulador o dispositivo conectado.

---

  ## Respuestas Brayan Rada

### 1. Predicción
**Si cambian `@GET("auth/me")` por `@GET("auth/mee")` (un typo), ¿qué error esperarían ver y en qué parte del código aparecería?**

Esperaríamos un **error 404 (Not Found)**, porque el servidor no tiene ninguna ruta llamada `auth/mee` y no sabría qué responder. En el código lo veríamos en el bloque `else` de `obtenerUsuario()` en `MainActivity.kt`, donde se muestra el código de error que llegó. Además, como tenemos el `HttpLoggingInterceptor` de OkHttp, en el **Logcat** de Android Studio podríamos ver la URL exacta que se pidió y confirmar que el problema es la ruta mal escrita.

### 2. Depuración
**"Recibo error 401 al pedir los datos del usuario, pero el login sí funcionó." ¿Cuáles son las dos causas más probables?**

1. **El token se está enviando mal.** Aunque el login funcionó y el token llegó bien, puede que al armar la cabecera `Authorization` se nos haya olvidado poner `"Bearer "` antes del token, o que se haya colado un espacio o un salto de línea de más.
2. **El token no es válido.** Puede que ya haya expirado, o que el que estamos enviando no sea el que nos entregó el login (por ejemplo, si quedó guardado uno viejo en memoria). En ese caso el servidor lo rechaza.

### 3. Transferencia
**¿En qué otras apps que usan a diario reconocen este mismo patrón de login → token → petición protegida? Den un ejemplo y expliquen dónde "vive" el token ahí.**

Un ejemplo claro es **Spotify** (también pasa con Instagram). Cuando entramos con nuestro correo y contraseña, el servidor nos responde con un token de acceso. La app lo guarda en el almacenamiento del teléfono, idealmente de forma protegida (por ejemplo con *EncryptedSharedPreferences*). Desde ese momento, cada vez que buscamos una canción o abrimos nuestro perfil, la app toma ese token y lo manda en la cabecera de la petición. Así el servidor sabe quiénes somos sin pedirnos la contraseña de nuevo en cada pantalla.

### 4. Lectura de código
**¿Por qué `getCurrentUser` recibe el token como parámetro en vez de leerlo directamente de una variable dentro de la función?**

Porque así la función no depende de nada externo: solo usa lo que le pasan. Si leyera el token de una variable global o de otra clase, quedaría atada a ese estado y sería más difícil de reutilizar y de probar. Con el parámetro podemos pasarle cualquier token, incluso uno falso para pruebas, sin importar de dónde venga. Eso hace el código más limpio, flexible y fácil de mantener.

### 5. Justificación de diseño
**¿Qué riesgo ven en guardar el token en SharedPreferences sin cifrar?**

El riesgo es que alguien pueda **leer el token y robarlo**. Las `SharedPreferences` normales se guardan como un archivo XML en texto plano. En un celular con root, o si alguien accede a una copia de seguridad (backup) o al dispositivo físicamente, podría abrir ese archivo y copiar el token. Con él, el atacante podría hacerse pasar por el usuario y hacer cosas en su nombre en el servidor, sin necesitar su contraseña.