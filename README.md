# EventPlanner

App para organizar eventos personales con Firebase y Jetpack Compose.

## Qué hace la app

Puedes registrarte con email y password, crear eventos con título, fecha y descripción, ver todos tus eventos en tiempo real, editarlos cuando quieras y eliminarlos si ya no los necesitas.

## Tecnologías que usé

- Kotlin
- Jetpack Compose para toda la UI
- Firebase Authentication para login y registro
- Firestore para guardar los eventos
- MVVM para organizar el código
- Patrón DAO para acceder a los datos

## Cómo está organizado

```
app/src/main/java/com/example/eventplanner/
├── models/          (Event, User)
├── dao/             (AuthDao, EventDao)
├── viewmodels/      (AuthViewModel, EventViewModel)
├── screens/         (Login, Register, EventList, etc)
└── Navigation.kt
```

## Para ejecutar el proyecto

1. Clonar el repo
2. Abrir en Android Studio
3. Agregar tu archivo google-services.json en la carpeta app/
4. Configurar Firebase Auth y Firestore en tu cuenta
5. Sync gradle y correr

## Funcionalidades

- Login y registro con validaciones
- CRUD completo de eventos
- Actualización en tiempo real
- Diseño con gradientes y cards
- Confirmación antes de eliminar

