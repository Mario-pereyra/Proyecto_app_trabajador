# 🛠️ Guía de Desarrollo - App Trabajador

## 🎯 Introducción

Esta guía está dirigida a desarrolladores que quieren contribuir, extender o mantener la **App Trabajador**. Aquí encontrarás toda la información técnica necesaria para el desarrollo eficiente.

## 🚀 Configuración del Entorno de Desarrollo

### 📋 Prerrequisitos

#### Herramientas Básicas
- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK 11** o superior
- **Gradle 8.0** o superior
- **Git** para control de versiones

#### SDK de Android
- **Compile SDK**: 36
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36

#### APIs Externas
- **Google Maps API Key** para funcionalidad de mapas
- **Acceso al servidor backend** para pruebas

### ⚙️ Configuración Inicial

#### 1. Clonar y Configurar Proyecto
```bash
# Clonar repositorio
git clone https://github.com/Mario-pereyra/Proyecto_app_trabajador.git
cd Proyecto_app_trabajador

# Crear archivo de configuración local
touch local.properties
```

#### 2. Configurar `local.properties`
```properties
# Google Maps API Key (obtener desde Google Cloud Console)
MAPS_API_KEY=AIzaSyC_tu_clave_api_aqui

# URLs del servidor (opcional, para desarrollo)
API_BASE_URL_DEV=http://10.0.2.2:8000/api/
API_BASE_URL_STAGING=https://staging-api.com/api/
API_BASE_URL_PROD=https://api.servicios.com/api/

# Configuraciones de debug
DEBUG_MODE=true
ENABLE_LOGGING=true
```

#### 3. Sincronizar Proyecto
```bash
# En Android Studio
./gradlew sync

# O desde línea de comandos
./gradlew build
```

## 🏗️ Estructura del Proyecto

### 📁 Organización de Carpetas

```
app/src/main/java/com/example/proyectoapptrabajador/
├── ui/                          # Capa de Presentación
│   ├── activities/              # Activities principales
│   │   ├── MainActivity.kt      # Activity principal
│   │   └── MapsActivity.kt      # Activity para mapas
│   ├── fragments/               # Fragments de UI
│   │   ├── LoginFragment.kt     # Autenticación
│   │   ├── HomeTrabajadorFragment.kt  # Menú principal
│   │   ├── MisCitasFragment.kt  # Lista de citas
│   │   ├── ChatFragment.kt      # Chat con cliente
│   │   ├── MapaFragment.kt      # Visualización de mapa
│   │   └── WorkerRegister*.kt   # Registro en 3 pasos
│   ├── adapters/                # Adapters para RecyclerViews
│   │   ├── CitaAdapter.kt       # Lista de citas
│   │   └── ChatAdapter.kt       # Mensajes de chat
│   ├── dialogs/                 # Diálogos modales
│   │   ├── ConfirmAppointmentDialog.kt  # Confirmar cita
│   │   └── FinalizeAppointmentDialog.kt # Finalizar trabajo
│   └── viewmodels/              # ViewModels (MVVM)
│       ├── LoginViewModel.kt    # Lógica de login
│       ├── CitasViewModel.kt    # Gestión de citas
│       ├── ChatViewModel.kt     # Lógica de chat
│       └── WorkerRegisterViewModel.kt # Registro
├── data/                        # Capa de Datos
│   ├── model/                   # Modelos de datos
│   │   └── ApiModels.kt         # DTOs y entidades
│   ├── network/                 # Networking
│   │   ├── ApiClient.kt         # Interface Retrofit
│   │   ├── RetrofitHelper.kt    # Configuración de Retrofit
│   │   └── AuthInterceptor.kt   # Interceptor JWT
│   └── datastore/               # Almacenamiento local
│       └── TokenDataStore.kt    # Gestión de tokens
└── repositories/                # Repositorios
    └── AppRepository.kt         # Acceso unificado a datos
```

### 📱 Recursos de UI

```
app/src/main/res/
├── layout/                      # Layouts de pantallas
│   ├── activity_main.xml       # Layout principal
│   ├── fragment_login.xml      # Pantalla de login
│   ├── fragment_mis_citas.xml  # Lista de citas
│   ├── fragment_chat.xml       # Pantalla de chat
│   └── item_cita.xml           # Item de cita en lista
├── navigation/                  # Navigation Component
│   └── nav_graph.xml           # Grafo de navegación
├── values/                      # Valores de recursos
│   ├── colors.xml              # Colores de la app
│   ├── strings.xml             # Textos y literales
│   ├── styles.xml              # Estilos personalizados
│   └── themes.xml              # Temas de Material Design
└── drawable/                    # Recursos gráficos
    ├── ic_*.xml                # Iconos vectoriales
    └── background_*.xml        # Backgrounds personalizados
```

## 🔧 Arquitectura de Desarrollo

### 📐 Patrón MVVM

#### Model (Modelos de Datos)
```kotlin
// Ejemplo: Modelo de Cita
data class Cita(
    val id: Int,
    val worker_id: Int,
    val user_id: Int,
    val appointment_date: String?,
    val appointment_time: String?,
    val category_selected_id: Int,
    val latitude: String?,
    val longitude: String?,
    val status: Int,
    val category: Category,
    val client: Client
)
```

#### View (Fragments/Activities)
```kotlin
class MisCitasFragment : Fragment() {
    private var _binding: FragmentMisCitasBinding? = null
    private val binding get() = _binding!!
    
    private val citasViewModel: CitasViewModel by viewModels {
        CitasViewModelFactory(MainActivity.repository)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupRecyclerView()
        citasViewModel.loadCitas()
    }
    
    private fun setupObservers() {
        citasViewModel.citas.observe(viewLifecycleOwner) { citas ->
            citaAdapter.updateCitas(citas)
        }
        
        citasViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}
```

#### ViewModel (Lógica de Negocio)
```kotlin
class CitasViewModel(private val repository: AppRepository) : ViewModel() {
    
    private val _citas = MutableLiveData<List<Cita>>()
    val citas: LiveData<List<Cita>> = _citas
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    fun loadCitas() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getCitas()
                if (response.isSuccessful) {
                    _citas.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar citas"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

### 🗄️ Repository Pattern

```kotlin
class AppRepository(
    private val apiClient: ApiClient,
    private val tokenDataStore: TokenDataStore
) {
    suspend fun getCitas() = apiClient.getAppointments()
    
    suspend fun confirmCita(citaId: Int, workerId: String, categoryId: Int) =
        apiClient.confirmAppointment(citaId, ConfirmAppointmentRequest(workerId, categoryId))
    
    suspend fun saveToken(token: String) {
        tokenDataStore.saveToken(token)
    }
    
    // Más métodos del repository...
}
```

## 🌐 Configuración de Networking

### 🔧 Retrofit Setup

```kotlin
object RetrofitHelper {
    private const val BASE_URL = "https://api.servicios.com/api/"
    
    fun getInstance(context: Context): ApiClient {
        val tokenDataStore = TokenDataStore(context)
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenDataStore))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiClient::class.java)
    }
}
```

### 🔐 Auth Interceptor

```kotlin
class AuthInterceptor(private val tokenDataStore: TokenDataStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val token = runBlocking { tokenDataStore.getToken.first() }
        
        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}
```

## 🎨 Guías de Estilo

### 🔤 Convenciones de Nomenclatura

#### Kotlin Code Style
```kotlin
// Classes: PascalCase
class CitasViewModel

// Functions: camelCase
fun loadCitas()

// Variables: camelCase
val citasList = mutableListOf<Cita>()

// Constants: UPPER_SNAKE_CASE
companion object {
    private const val BASE_URL = "https://api.com/"
    private const val REQUEST_TIMEOUT = 30L
}

// Package names: lowercase
package com.example.proyectoapptrabajador.ui.fragments
```

#### XML Resources
```xml
<!-- Layouts: snake_case with descriptive prefixes -->
activity_main.xml
fragment_mis_citas.xml
item_cita.xml
dialog_confirm_appointment.xml

<!-- IDs: snake_case with type prefix -->
android:id="@+id/tv_client_name"
android:id="@+id/btn_confirm_appointment"
android:id="@+id/rv_citas"

<!-- Strings: snake_case with context -->
<string name="login_button_text">Iniciar Sesión</string>
<string name="error_network_connection">Error de conexión</string>
<string name="cita_status_confirmed">Confirmada</string>
```

### 🎨 Material Design Guidelines

#### Colores
```xml
<!-- colors.xml -->
<color name="colorPrimary">#2196F3</color>
<color name="colorPrimaryDark">#1976D2</color>
<color name="colorSecondary">#4CAF50</color>
<color name="colorAccent">#FF9800</color>

<!-- Estados de citas -->
<color name="status_pending">#FFC107</color>
<color name="status_requested">#2196F3</color>
<color name="status_confirmed">#4CAF50</color>
<color name="status_finished">#9E9E9E</color>
```

#### Espaciado
```xml
<!-- dimens.xml -->
<dimen name="margin_small">8dp</dimen>
<dimen name="margin_medium">16dp</dimen>
<dimen name="margin_large">24dp</dimen>
<dimen name="margin_xlarge">32dp</dimen>

<dimen name="text_size_small">12sp</dimen>
<dimen name="text_size_medium">16sp</dimen>
<dimen name="text_size_large">20sp</dimen>
<dimen name="text_size_title">24sp</dimen>
```

## 🔍 Testing

### 🧪 Estructura de Tests

```
app/src/test/java/                    # Unit Tests
├── viewmodels/
│   ├── CitasViewModelTest.kt
│   ├── ChatViewModelTest.kt
│   └── LoginViewModelTest.kt
├── repositories/
│   └── AppRepositoryTest.kt
└── utils/
    └── NetworkUtilsTest.kt

app/src/androidTest/java/             # Instrumentation Tests
├── ui/
│   ├── LoginFlowTest.kt
│   ├── CitasListTest.kt
│   └── ChatFunctionalityTest.kt
└── database/
    └── TokenDataStoreTest.kt
```

### 🔬 Ejemplo de Unit Test

```kotlin
class CitasViewModelTest {
    
    @Mock
    private lateinit var repository: AppRepository
    
    @Mock
    private lateinit var observer: Observer<List<Cita>>
    
    private lateinit var viewModel: CitasViewModel
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = CitasViewModel(repository)
    }
    
    @Test
    fun `when loadCitas called, should update citas LiveData`() = runTest {
        // Given
        val mockCitas = listOf(
            Cita(id = 1, status = 1, /* otros campos */),
            Cita(id = 2, status = 2, /* otros campos */)
        )
        val response = Response.success(mockCitas)
        whenever(repository.getCitas()).thenReturn(response)
        
        // When
        viewModel.citas.observeForever(observer)
        viewModel.loadCitas()
        
        // Then
        verify(observer).onChanged(mockCitas)
        assertEquals(mockCitas, viewModel.citas.value)
    }
    
    @Test
    fun `when API call fails, should update errorMessage`() = runTest {
        // Given
        whenever(repository.getCitas()).thenThrow(IOException("Network error"))
        
        // When
        viewModel.loadCitas()
        
        // Then
        assertTrue(viewModel.errorMessage.value?.contains("Error de conexión") == true)
    }
}
```

### 🎯 UI Test Ejemplo

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginFlowTest {
    
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)
    
    @Test
    fun loginWithValidCredentials_navigatesToCitasList() {
        // Given - en pantalla de login
        onView(withId(R.id.et_email))
            .perform(typeText("test@email.com"))
        
        onView(withId(R.id.et_password))
            .perform(typeText("password123"))
        
        // When - hacer login
        onView(withId(R.id.btn_login))
            .perform(click())
        
        // Then - debe navegar a lista de citas
        onView(withId(R.id.rv_citas))
            .check(matches(isDisplayed()))
    }
}
```

## 🏃‍♂️ Scripts de Build y Desarrollo

### 📜 Comandos Gradle

```bash
# Limpiar proyecto
./gradlew clean

# Compilar debug
./gradlew assembleDebug

# Compilar release
./gradlew assembleRelease

# Ejecutar tests unitarios
./gradlew test

# Ejecutar tests instrumentados
./gradlew connectedAndroidTest

# Instalar en dispositivo conectado
./gradlew installDebug

# Generar APK firmado
./gradlew assembleRelease

# Analizar dependencias
./gradlew dependencies

# Verificar código con lint
./gradlew lint
```

### 🔧 Tasks Personalizados

```kotlin
// En build.gradle.kts (app level)
tasks.register("generateApiDocs") {
    doLast {
        exec {
            commandLine("python", "scripts/generate_api_docs.py")
        }
    }
}

tasks.register("runAllTests") {
    dependsOn("test", "connectedAndroidTest")
    doLast {
        println("Todos los tests completados")
    }
}
```

## 📊 Debugging y Logging

### 🔍 Configuración de Logging

```kotlin
object Logger {
    private const val TAG = "AppTrabajador"
    
    fun d(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
    
    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable)
        }
    }
    
    fun i(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }
}

// Uso en ViewModels
class CitasViewModel : ViewModel() {
    fun loadCitas() {
        Logger.d("Cargando lista de citas...")
        viewModelScope.launch {
            try {
                val response = repository.getCitas()
                Logger.d("Citas cargadas: ${response.body()?.size ?: 0}")
            } catch (e: Exception) {
                Logger.e("Error al cargar citas", e)
            }
        }
    }
}
```

### 🐛 Debugging de Red

```kotlin
// En RetrofitHelper.kt
val loggingInterceptor = HttpLoggingInterceptor { message ->
    Logger.d(message, "NetworkCall")
}.apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
}
```

## 🚀 Despliegue

### 📱 Configuración de Release

```kotlin
// build.gradle.kts (app level)
android {
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/app-release.keystore")
            storePassword = project.properties["RELEASE_STORE_PASSWORD"] as String
            keyAlias = project.properties["RELEASE_KEY_ALIAS"] as String
            keyPassword = project.properties["RELEASE_KEY_PASSWORD"] as String
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            
            buildConfigField("String", "API_BASE_URL", "\"https://api.servicios.com/api/\"")
            buildConfigField("boolean", "DEBUG_MODE", "false")
        }
        
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000/api/\"")
            buildConfigField("boolean", "DEBUG_MODE", "true")
        }
    }
}
```

### 🔐 ProGuard Rules

```proguard
# proguard-rules.pro

# Mantener clases de modelo para Gson
-keep class com.example.proyectoapptrabajador.data.model.** { *; }

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
```

## 🤝 Contribución al Proyecto

### 🔄 Flujo de Contribución

1. **Fork** del repositorio
2. **Crear rama** feature: `git checkout -b feature/nueva-funcionalidad`
3. **Desarrollar** siguiendo las guías de estilo
4. **Escribir tests** para la nueva funcionalidad
5. **Commit** con mensajes descriptivos
6. **Push** a tu fork: `git push origin feature/nueva-funcionalidad`
7. **Crear Pull Request** con descripción detallada

### 📝 Convenciones de Commit

```bash
# Formato: tipo(scope): descripción

# Tipos válidos:
feat(ui): agregar pantalla de configuración de perfil
fix(network): corregir timeout en llamadas API
docs(readme): actualizar guía de instalación
refactor(viewmodel): simplificar lógica de estados de cita
test(unit): agregar tests para ChatViewModel
style(format): aplicar formato Kotlin estándar
```

### 🔍 Code Review Checklist

#### Antes de enviar PR:
- [ ] Tests pasan correctamente
- [ ] Código sigue convenciones de estilo
- [ ] Documentación actualizada si es necesario
- [ ] Sin warnings de lint
- [ ] Funcionalidad probada en dispositivo real

#### Durante Code Review:
- [ ] Lógica de negocio correcta
- [ ] Manejo de errores adecuado
- [ ] Performance optimizada
- [ ] Seguridad considerada
- [ ] UI/UX consistente con design system

## 📈 Métricas y Monitoreo

### 📊 KPIs de Desarrollo

- **Build time**: < 2 minutos para debug
- **Test coverage**: > 80% en ViewModels y Repository
- **APK size**: < 20MB para release
- **Crash rate**: < 1% en producción
- **ANR rate**: < 0.5% en producción

### 🔧 Herramientas de Análisis

```kotlin
// Firebase Performance (ejemplo de integración)
class PerformanceTracker {
    fun trackApiCall(endpoint: String, duration: Long) {
        val trace = FirebasePerformance.getInstance().newTrace("api_call_$endpoint")
        trace.putAttribute("duration_ms", duration.toString())
        trace.start()
        // ... código de API call
        trace.stop()
    }
}
```

---

Esta guía proporciona una base sólida para el desarrollo profesional y mantenimiento eficiente de la App Trabajador. ¡Happy coding! 🚀