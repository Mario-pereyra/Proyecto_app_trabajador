# 🌐 Documentación de API - Sistema Trabajador

## 📝 Descripción General

La **App Trabajador** se comunica con una API REST que gestiona todas las operaciones relacionadas con trabajadores de servicios, citas, chat y autenticación. Esta documentación describe todos los endpoints utilizados por la aplicación móvil.

## 🔗 Base URL y Configuración

```kotlin
// Configuración en RetrofitHelper.kt
private const val BASE_URL = "https://api-servicios.com/api/" // URL del servidor

// Headers requeridos
Authorization: Bearer {jwt_token}
Content-Type: application/json
Accept: application/json
```

## 🔐 Autenticación

### 🔑 Login de Trabajador
Autentica a un trabajador y devuelve un token JWT.

```http
POST /worker/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "trabajador@email.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
}
```

**Códigos de Error:**
- `401 Unauthorized` - Credenciales inválidas
- `422 Unprocessable Entity` - Datos de entrada inválidos

### 👤 Obtener Información del Usuario Actual
Obtiene la información completa del trabajador autenticado.

```http
GET /me
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 123,
  "name": "Juan",
  "email": "juan@email.com",
  "profile": {
    "id": 456,
    "name": "Juan",
    "last_name": "Pérez",
    "type": 2
  },
  "worker": {
    "id": 789,
    "user_id": 123,
    "picture_url": "https://api.com/uploads/profile.jpg",
    "average_rating": "4.5",
    "reviews_count": 25
  }
}
```

## 👷 Registro de Trabajador

### 📝 Paso 1: Registro Básico
Registra los datos básicos del trabajador.

```http
POST /worker/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@email.com",
  "password": "password123"
}
```

**Response (201 Created):**
```json
{
  "id": 123,
  "name": "Juan",
  "email": "juan.perez@email.com",
  "profile": {
    "id": 456,
    "name": "Juan",
    "last_name": "Pérez",
    "type": 2
  },
  "worker": {
    "id": 789,
    "user_id": 123,
    "picture_url": null,
    "average_rating": null,
    "reviews_count": 0
  }
}
```

### 📷 Paso 2: Subir Foto de Perfil
Sube la foto de perfil del trabajador.

```http
POST /workers/profile-picture
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Request Body (multipart):**
```
picture: [archivo de imagen]
```

**Response (200 OK):**
```
Status: 200 OK
```

### 🏷️ Paso 3: Asignar Categorías
Asigna las categorías de servicios que puede ofrecer el trabajador.

```http
POST /workers/{worker_id}/categories
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "categories": [
    {"id": 1},
    {"id": 3},
    {"id": 5}
  ]
}
```

**Response (200 OK):**
```json
{
  "id": 789,
  "user_id": 123,
  "picture_url": "https://api.com/uploads/profile.jpg",
  "average_rating": null,
  "reviews_count": 0,
  "user": {
    "id": 123,
    "name": "Juan",
    "email": "juan.perez@email.com"
  },
  "categories": [
    {
      "id": 1,
      "name": "Plomería",
      "pivot": {
        "worker_id": 789,
        "category_id": 1
      }
    },
    {
      "id": 3,
      "name": "Electricidad",
      "pivot": {
        "worker_id": 789,
        "category_id": 3
      }
    }
  ],
  "reviews": []
}
```

## 📅 Gestión de Citas

### 📋 Obtener Lista de Citas
Obtiene todas las citas asignadas al trabajador autenticado.

```http
GET /appointments
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "worker_id": 789,
    "user_id": 456,
    "appointment_date": "2024-01-15",
    "appointment_time": "14:30:00",
    "category_selected_id": 1,
    "latitude": "-34.6037",
    "longitude": "-58.3816",
    "status": 1,
    "category": {
      "id": 1,
      "name": "Plomería"
    },
    "client": {
      "id": 456,
      "name": "María",
      "profile": {
        "name": "María",
        "last_name": "González"
      }
    }
  }
]
```

### 🔍 Obtener Detalles de Cita
Obtiene información detallada de una cita específica.

```http
GET /appointments/{appointment_id}
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "worker_id": 789,
  "user_id": 456,
  "appointment_date": "2024-01-15",
  "appointment_time": "14:30:00",
  "category_selected_id": 1,
  "latitude": "-34.6037",
  "longitude": "-58.3816",
  "status": 1,
  "category": {
    "id": 1,
    "name": "Plomería"
  },
  "client": {
    "id": 456,
    "name": "María",
    "profile": {
      "name": "María",
      "last_name": "González"
    }
  }
}
```

### ✅ Confirmar Cita
Confirma una cita que ha sido solicitada por el cliente.

```http
POST /appointments/{appointment_id}/confirm
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "worker_id": "789",
  "category_selected_id": 1
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "worker_id": 789,
  "user_id": 456,
  "appointment_date": "2024-01-15",
  "appointment_time": "14:30:00",
  "category_selected_id": 1,
  "latitude": "-34.6037",
  "longitude": "-58.3816",
  "status": 2,  // Cambia a estado "confirmada"
  "category": {
    "id": 1,
    "name": "Plomería"
  },
  "client": {
    "id": 456,
    "name": "María",
    "profile": {
      "name": "María",
      "last_name": "González"
    }
  }
}
```

### 🏁 Finalizar Cita
Marca una cita como finalizada después de completar el trabajo.

```http
POST /appointments/{appointment_id}/finalize
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "worker_id": "789",
  "category_selected_id": 1
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "worker_id": 789,
  "user_id": 456,
  "appointment_date": "2024-01-15",
  "appointment_time": "14:30:00",
  "category_selected_id": 1,
  "latitude": "-34.6037",
  "longitude": "-58.3816",
  "status": 3,  // Cambia a estado "finalizada"
  "category": {
    "id": 1,
    "name": "Plomería"
  },
  "client": {
    "id": 456,
    "name": "María",
    "profile": {
      "name": "María",
      "last_name": "González"
    }
  }
}
```

## 💬 Sistema de Chat

### 📨 Obtener Mensajes de Chat
Obtiene todos los mensajes de chat para una cita específica.

```http
GET /appointments/{appointment_id}/chats
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "appointment_id": 1,
    "sender_id": 456,
    "receiver_id": 789,
    "date_sent": "2024-01-15T10:30:00Z",
    "message": "Hola, necesito ayuda con una fuga de agua",
    "appointment": {
      "id": 1,
      "worker_id": 789,
      "user_id": 456,
      "appointment_date": "2024-01-15",
      "appointment_time": "14:30:00",
      "category_selected_id": 1,
      "latitude": "-34.6037",
      "longitude": "-58.3816",
      "status": 1
    },
    "sender": {
      "id": 456,
      "name": "María",
      "email": "maria@email.com"
    },
    "receiver": {
      "id": 789,
      "name": "Juan",
      "email": "juan@email.com"
    }
  },
  {
    "id": 2,
    "appointment_id": 1,
    "sender_id": 789,
    "receiver_id": 456,
    "date_sent": "2024-01-15T10:35:00Z",
    "message": "Hola María, puedo ayudarte. ¿Podrías describirme el problema?",
    "appointment": {
      "id": 1,
      "worker_id": 789,
      "user_id": 456,
      "appointment_date": "2024-01-15",
      "appointment_time": "14:30:00",
      "category_selected_id": 1,
      "latitude": "-34.6037",
      "longitude": "-58.3816",
      "status": 1
    },
    "sender": {
      "id": 789,
      "name": "Juan",
      "email": "juan@email.com"
    },
    "receiver": {
      "id": 456,
      "name": "María",
      "email": "maria@email.com"
    }
  }
]
```

### 📤 Enviar Mensaje de Chat
Envía un nuevo mensaje en el chat de una cita.

```http
POST /appointments/{appointment_id}/chats
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "message": "Perfecto, estaré allí a las 14:30",
  "receiver_id": 456
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "appointment_id": 1,
  "sender_id": 789,
  "receiver_id": 456,
  "date_sent": "2024-01-15T10:40:00Z",
  "message": "Perfecto, estaré allí a las 14:30",
  "appointment": {
    "id": 1,
    "worker_id": 789,
    "user_id": 456,
    "appointment_date": "2024-01-15",
    "appointment_time": "14:30:00",
    "category_selected_id": 1,
    "latitude": "-34.6037",
    "longitude": "-58.3816",
    "status": 1
  },
  "sender": {
    "id": 789,
    "name": "Juan",
    "email": "juan@email.com"
  },
  "receiver": {
    "id": 456,
    "name": "María",
    "email": "maria@email.com"
  }
}
```

## 🏷️ Gestión de Categorías

### 📋 Obtener Lista de Categorías
Obtiene todas las categorías de servicios disponibles.

```http
GET /categories
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Plomería"
  },
  {
    "id": 2,
    "name": "Electricidad"
  },
  {
    "id": 3,
    "name": "Carpintería"
  },
  {
    "id": 4,
    "name": "Pintura"
  },
  {
    "id": 5,
    "name": "Limpieza"
  }
]
```

### 🆕 Crear Nueva Categoría
Crea una nueva categoría de servicio (si el trabajador tiene permisos).

```http
POST /categories
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Jardinería"
}
```

**Response (201 Created):**
```json
{
  "id": 6,
  "name": "Jardinería"
}
```

## 📊 Estados de Cita

### 🔢 Códigos de Estado

| Estado | Código | Descripción | Acciones del Trabajador |
|--------|--------|-------------|-------------------------|
| **Pendiente** | 0 | Cita asignada, esperando acción | Ver detalles, Chat |
| **Solicitada** | 1 | Cliente ha solicitado el servicio | **Confirmar**, Chat, Ver ubicación |
| **Confirmada** | 2 | Trabajo confirmado por el trabajador | **Finalizar**, Chat, Ver ubicación |
| **Finalizada** | 3 | Trabajo completado | Ver historial, Chat |

### 🔄 Flujo de Estados

```mermaid
stateDiagram-v2
    [*] --> Pendiente : Cita asignada
    Pendiente --> Solicitada : Cliente solicita
    Solicitada --> Confirmada : Trabajador confirma
    Confirmada --> Finalizada : Trabajador finaliza
    Finalizada --> [*]
    
    note right of Solicitada : POST /appointments/{id}/confirm
    note right of Confirmada : POST /appointments/{id}/finalize
```

## ⚠️ Códigos de Error Comunes

### 🔴 Errores de Autenticación
```json
// 401 Unauthorized
{
  "message": "Unauthenticated"
}

// 403 Forbidden
{
  "message": "This action is unauthorized"
}
```

### 🔴 Errores de Validación
```json
// 422 Unprocessable Entity
{
  "message": "The given data was invalid",
  "errors": {
    "email": ["The email field is required"],
    "password": ["The password must be at least 8 characters"]
  }
}
```

### 🔴 Errores de Recurso
```json
// 404 Not Found
{
  "message": "No query results for model [App\\Models\\Appointment] 123"
}

// 500 Internal Server Error
{
  "message": "Server Error"
}
```

## 🔐 Seguridad y Autorización

### 🎫 Token JWT
- **Formato**: Bearer Token en header Authorization
- **Duración**: Configurada en el servidor (ej: 24 horas)
- **Renovación**: Automática en el interceptor de la app
- **Almacenamiento**: DataStore seguro en el dispositivo

### 🛡️ Headers de Seguridad
```http
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
Accept: application/json
User-Agent: AppTrabajador/1.0 (Android)
```

### 🔒 Validaciones del Servidor
- Verificación de token JWT en cada request
- Validación de permisos por rol (trabajador vs cliente)
- Verificación de propiedad de recursos (solo acceso a propias citas)
- Rate limiting para prevenir abuso

## 🧪 Testing de API

### 🔬 Endpoints de Testing Recomendados

#### Flujo Completo de Autenticación:
```bash
# 1. Login
curl -X POST https://api.com/worker/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@email.com","password":"password123"}'

# 2. Verificar token
curl -X GET https://api.com/me \
  -H "Authorization: Bearer {token}"
```

#### Flujo de Citas:
```bash
# 1. Obtener citas
curl -X GET https://api.com/appointments \
  -H "Authorization: Bearer {token}"

# 2. Obtener detalles
curl -X GET https://api.com/appointments/1 \
  -H "Authorization: Bearer {token}"

# 3. Confirmar cita
curl -X POST https://api.com/appointments/1/confirm \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"worker_id":"789","category_selected_id":1}'
```

## 📈 Optimizaciones y Mejores Prácticas

### ⚡ Performance
- **Paginación** en endpoints que devuelven listas
- **Caching** de respuestas frecuentes
- **Compresión GZIP** en respuestas
- **Lazy loading** de relaciones en modelos

### 🔄 Retry Logic
La app implementa retry automático para:
- Errores de conexión temporales (500, 502, 503)
- Timeouts de red
- Fallos de autenticación (refresh de token)

### 📊 Logging y Monitoreo
- Logs detallados de requests/responses en debug
- Métricas de tiempo de respuesta
- Alertas en errores críticos
- Dashboard de uso de endpoints

---

*Esta API proporciona todas las funcionalidades necesarias para una experiencia completa y fluida en la App Trabajador.*