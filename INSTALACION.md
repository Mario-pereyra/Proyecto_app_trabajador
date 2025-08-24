# 🚀 Guía de Instalación y Configuración

## 📖 Descripción General

Esta guía te ayudará a instalar y configurar correctamente la **App Trabajador** tanto para uso final como para desarrollo. Incluye instrucciones detalladas para diferentes escenarios y sistemas operativos.

## 📱 Instalación para Usuarios Finales

### 📋 Requisitos del Sistema

#### Dispositivo Android
- **Sistema Operativo**: Android 7.0 (API 24) o superior
- **RAM**: Mínimo 2GB, recomendado 4GB o más
- **Almacenamiento**: 100MB libres para la instalación
- **Conexión**: Internet estable (WiFi o datos móviles)
- **Permisos**: Ubicación y cámara para funcionalidades completas

#### Aplicaciones Adicionales
- **Google Maps**: Para navegación y visualización de ubicaciones
- **Navegador web**: Para links externos (opcional)

### 📥 Proceso de Instalación

#### Método 1: Desde Google Play Store (Recomendado)
```
🔍 PRÓXIMAMENTE: La app estará disponible en Google Play Store
```

#### Método 2: Instalación Manual (APK)

⚠️ **Advertencia de Seguridad**: Solo instala APKs desde fuentes confiables.

1. **Habilitar instalación de fuentes desconocidas:**
   - Ve a `Configuración > Seguridad`
   - Activa `Fuentes desconocidas` o `Instalar aplicaciones desconocidas`
   - En Android 8.0+: Ve a `Configuración > Apps y notificaciones > Acceso especial > Instalar apps desconocidas`

2. **Descargar el APK:**
   - Obtén el archivo APK desde la fuente autorizada
   - Asegúrate de descargar la versión más reciente

3. **Instalar la aplicación:**
   - Abre el archivo APK descargado
   - Toca `Instalar` cuando aparezca el prompt
   - Espera a que se complete la instalación
   - Toca `Abrir` para iniciar la app

### 🔧 Configuración Inicial

#### Primer Arranque
1. **Abrir la aplicación** desde el menú de apps
2. **Conceder permisos** cuando se soliciten:
   - **Ubicación**: Para mostrar tu posición en el mapa
   - **Cámara**: Para tomar foto de perfil
   - **Almacenamiento**: Para seleccionar fotos de galería

3. **Verificar conexión** a internet

#### Configuración de Cuenta
1. **Nuevo usuario**: Sigue el proceso de registro de 3 pasos
2. **Usuario existente**: Inicia sesión con tus credenciales

## 🛠️ Instalación para Desarrollo

### 🖥️ Configuración del Entorno

#### Sistemas Operativos Soportados
- **Windows** 10/11 (64-bit)
- **macOS** 10.14 o superior
- **Linux** Ubuntu 18.04+ / otras distribuciones compatibles

#### Herramientas Requeridas

##### 1. Java Development Kit (JDK)
```bash
# Verificar instalación existente
java -version
javac -version

# Debería mostrar JDK 11 o superior
```

**Instalación JDK 11:**

**Windows:**
```powershell
# Con Chocolatey
choco install openjdk11

# O descargar desde: https://adoptopenjdk.net/
```

**macOS:**
```bash
# Con Homebrew
brew install openjdk@11

# Configurar JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v11)' >> ~/.zshrc
source ~/.zshrc
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-11-jdk

# Configurar JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

##### 2. Android Studio

**Descarga e instalación:**
1. Ir a [Android Studio](https://developer.android.com/studio)
2. Descargar la versión estable más reciente
3. Seguir el asistente de instalación
4. Incluir Android SDK y emulador durante la instalación

**Configuración inicial:**
```bash
# Variables de entorno (agregar a ~/.bashrc o ~/.zshrc)
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

##### 3. SDK de Android

En Android Studio:
1. `Tools > SDK Manager`
2. Instalar las siguientes versiones:
   - **Android 14 (API 34)** - Para compilación
   - **Android 7.0 (API 24)** - Versión mínima soportada
   - **Android SDK Build-Tools** 34.0.0+
   - **Android SDK Platform-Tools**
   - **Android Emulator**

##### 4. Git

**Windows:**
```powershell
# Con Git for Windows
# Descargar desde: https://git-scm.com/download/win

# Con Chocolatey
choco install git
```

**macOS:**
```bash
# Con Homebrew
brew install git

# O usar Xcode Command Line Tools
xcode-select --install
```

**Linux:**
```bash
# Ubuntu/Debian
sudo apt install git

# CentOS/RHEL
sudo yum install git
```

### 📂 Configuración del Proyecto

#### 1. Clonar el Repositorio

```bash
# Clonar proyecto
git clone https://github.com/Mario-pereyra/Proyecto_app_trabajador.git
cd Proyecto_app_trabajador

# Verificar rama actual
git branch

# Cambiar a rama de desarrollo si existe
git checkout develop
```

#### 2. Configurar Variables de Entorno

Crear archivo `local.properties` en la raíz del proyecto:

```properties
# === CONFIGURACIÓN DE DESARROLLO ===

# Google Maps API Key (OBLIGATORIO)
# Obtener desde: https://console.cloud.google.com/
MAPS_API_KEY=AIzaSyC_TU_CLAVE_API_AQUI

# URLs del Backend
API_BASE_URL_DEV=http://10.0.2.2:8000/api/
API_BASE_URL_STAGING=https://staging-api.servicios.com/api/
API_BASE_URL_PROD=https://api.servicios.com/api/

# Configuraciones de Debug
DEBUG_MODE=true
ENABLE_LOGGING=true
NETWORK_TIMEOUT=30

# Configuración del Keystore (para release)
RELEASE_STORE_FILE=../keystore/app-release.keystore
RELEASE_STORE_PASSWORD=tu_password_aqui
RELEASE_KEY_ALIAS=app-release
RELEASE_KEY_PASSWORD=tu_password_aqui
```

#### 3. Obtener Google Maps API Key

1. **Ir a Google Cloud Console:**
   - Visitar [Google Cloud Console](https://console.cloud.google.com/)
   - Crear proyecto nuevo o seleccionar existente

2. **Habilitar Maps SDK:**
   - `APIs & Services > Library`
   - Buscar "Maps SDK for Android"
   - Hacer clic en "Enable"

3. **Crear API Key:**
   - `APIs & Services > Credentials`
   - Hacer clic en "Create Credentials > API Key"
   - Copiar la clave generada

4. **Configurar restricciones (Recomendado):**
   - Editar la API Key creada
   - En "Application restrictions" seleccionar "Android apps"
   - Agregar package name: `com.example.proyectoapptrabajador`
   - Agregar fingerprint SHA-1 del certificado de debug

#### 4. Configurar Backend (Desarrollo)

Si estás desarrollando con un backend local:

```bash
# Configurar servidor backend local
# Asegurar que el servidor está ejecutándose en localhost:8000

# Verificar conectividad desde emulador
# El emulador usa 10.0.2.2 para localhost del host
curl http://10.0.2.2:8000/api/health
```

#### 5. Sincronizar Proyecto

```bash
# Desde línea de comandos
./gradlew sync

# O desde Android Studio
# File > Sync Project with Gradle Files
```

### 📱 Configuración de Dispositivos de Prueba

#### Emulador Android

1. **Crear AVD (Android Virtual Device):**
   - En Android Studio: `Tools > AVD Manager`
   - Hacer clic en "Create Virtual Device"
   - Seleccionar dispositivo (recomendado: Pixel 4)
   - Seleccionar system image (API 30+ recomendado)
   - Configurar settings avanzados si es necesario

2. **Configuración recomendada del emulador:**
   ```
   Device: Pixel 4
   System Image: Android 11 (API 30) o superior
   RAM: 2048 MB o más
   Internal Storage: 2 GB o más
   ```

3. **Iniciar emulador:**
   ```bash
   # Desde línea de comandos
   emulator -avd Pixel_4_API_30
   
   # O desde Android Studio
   # AVD Manager > Play button
   ```

#### Dispositivo Físico

1. **Habilitar opciones de desarrollador:**
   - Ir a `Configuración > Acerca del teléfono`
   - Tocar "Número de compilación" 7 veces
   - Volver a Configuración, buscar "Opciones de desarrollador"

2. **Habilitar depuración USB:**
   - En "Opciones de desarrollador"
   - Activar "Depuración USB"
   - Conectar dispositivo via USB

3. **Verificar conexión:**
   ```bash
   adb devices
   # Debería mostrar tu dispositivo conectado
   ```

### 🔧 Configuración de Herramientas Adicionales

#### Dependencias de Gradle

El proyecto ya incluye todas las dependencias necesarias:

```kotlin
// Las siguientes dependencias están pre-configuradas:
dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    
    // Architecture Components
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Google Maps
    implementation 'com.google.android.gms:play-services-maps:18.2.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
}
```

#### Plugins de Android Studio (Opcionales)

Plugins recomendados para mejorar la experiencia de desarrollo:

1. **Kotlin Multiplatform Mobile**
2. **Android WiFi ADB**
3. **GitToolBox**
4. **Rainbow Brackets**
5. **Gradle Dependencies Helper**

Instalar desde: `File > Settings > Plugins`

## ✅ Verificación de Instalación

### 🧪 Tests de Funcionamiento

#### 1. Compilación Exitosa
```bash
# Compilar versión debug
./gradlew assembleDebug

# Debe completarse sin errores
# APK generado en: app/build/outputs/apk/debug/
```

#### 2. Instalación en Dispositivo
```bash
# Instalar en dispositivo/emulador conectado
./gradlew installDebug

# Verificar instalación
adb shell pm list packages | grep proyectoapptrabajador
```

#### 3. Tests Unitarios
```bash
# Ejecutar tests unitarios
./gradlew test

# Ver reporte en: app/build/reports/tests/testDebugUnitTest/index.html
```

#### 4. Tests de Integración
```bash
# Ejecutar tests instrumentados (requiere dispositivo conectado)
./gradlew connectedAndroidTest
```

### 🔍 Checklist de Verificación

#### Configuración Básica
- [ ] JDK 11+ instalado y configurado
- [ ] Android Studio instalado y actualizado
- [ ] SDK de Android instalado (API 24-34)
- [ ] Git instalado y configurado
- [ ] Proyecto clonado exitosamente

#### Configuración del Proyecto
- [ ] Archivo `local.properties` creado
- [ ] Google Maps API Key configurada
- [ ] Sync de Gradle completado sin errores
- [ ] No hay errores de lint críticos

#### Dispositivos de Prueba
- [ ] Emulador configurado y funcionando
- [ ] O dispositivo físico conectado y reconocido
- [ ] Depuración USB habilitada

#### Funcionalidad de la App
- [ ] App se compila sin errores
- [ ] App se instala correctamente
- [ ] Pantalla de login se muestra
- [ ] Maps API funciona (si hay datos de prueba)

## 🔧 Solución de Problemas Comunes

### ❌ Problemas de Compilación

#### Error: "SDK location not found"
```bash
# Crear/editar local.properties
echo "sdk.dir=$ANDROID_HOME" >> local.properties
```

#### Error: "JAVA_HOME not set"
```bash
# Linux/macOS
export JAVA_HOME=/path/to/your/jdk11

# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
```

#### Error: "Gradle sync failed"
```bash
# Limpiar y reconstruir
./gradlew clean
./gradlew build --refresh-dependencies
```

### 🗺️ Problemas con Google Maps

#### Maps no se muestran
1. Verificar API Key en `local.properties`
2. Verificar que Maps SDK está habilitado en Google Cloud
3. Verificar restricciones de API Key
4. Revisar logs de Android para errores específicos

#### Error de autenticación en Maps
```bash
# Verificar SHA-1 fingerprint
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### 🌐 Problemas de Red

#### "Unable to resolve host"
1. Verificar conexión a internet
2. Para desarrollo local: usar `10.0.2.2` en lugar de `localhost`
3. Verificar firewall del sistema

#### Timeouts en requests
1. Aumentar timeout en configuración de Retrofit
2. Verificar estabilidad de conexión
3. Revisar logs del servidor backend

### 📱 Problemas del Emulador

#### Emulador lento
```bash
# Habilitar aceleración de hardware
# En AVD Manager > Advanced Settings
# Graphics: Hardware - GLES 2.0
# RAM: 2048 MB mínimo
```

#### "adb not found"
```bash
# Agregar platform-tools al PATH
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Reiniciar adb server
adb kill-server
adb start-server
```

## 📊 Configuraciones de Performance

### ⚡ Optimizaciones de Desarrollo

#### Gradle Performance
```kotlin
// En gradle.properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
android.useAndroidX=true
android.enableJetifier=true
```

#### Android Studio Performance
```
# En Help > Edit Custom VM Options
-Xmx4096m
-XX:MaxMetaspaceSize=512m
-XX:+UseG1GC
```

### 🔧 Build Variants

Configurar diferentes variants para desarrollo:

```kotlin
// En build.gradle.kts (app)
android {
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000/api/\"")
        }
        
        staging {
            isDebuggable = true
            buildConfigField("String", "API_BASE_URL", "\"https://staging-api.com/api/\"")
        }
        
        release {
            isMinifyEnabled = true
            buildConfigField("String", "API_BASE_URL", "\"https://api.com/api/\"")
        }
    }
}
```

## 🆘 Soporte y Recursos

### 📚 Documentación Adicional
- [README.md](./README.md) - Descripción general del proyecto
- [ARQUITECTURA.md](./ARQUITECTURA.md) - Documentación técnica detallada
- [API.md](./API.md) - Documentación de endpoints
- [DESARROLLO.md](./DESARROLLO.md) - Guía para desarrolladores

### 🔗 Enlaces Útiles
- [Android Studio User Guide](https://developer.android.com/studio/intro)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Material Design Guidelines](https://material.io/design)
- [Google Maps Android API](https://developers.google.com/maps/documentation/android-sdk)

### 💬 Obtener Ayuda
1. **Issues de GitHub**: Para reportar bugs o solicitar features
2. **Documentación del proyecto**: Para dudas técnicas específicas
3. **Android Developers Community**: Para cuestiones generales de Android

---

¡Felicidades! 🎉 Si has seguido esta guía, deberías tener la **App Trabajador** completamente instalada y configurada para desarrollo o uso. 

Para cualquier problema no cubierto en esta guía, no dudes en crear un issue en el repositorio del proyecto.