# Guía de Push Notifications con Expo — App Mobile

> Integración con el backend Spring Boot ya configurado.  
> Servicio intermediario: **Expo Push Service** (el backend NO habla directo con FCM/APNs).

---

## Resumen del flujo

```
App Expo  ──registra token──▶  Backend (GraphQL)  ──guarda en BD──▶ push_tokens
Backend   ──POST──▶  https://exp.host/--/api/v2/push/send
Expo Push ──FCM/APNs──▶  Dispositivo del técnico
```

1. El usuario hace login → la app obtiene un **Expo Push Token** del dispositivo.
2. La app llama la mutation `registerPushToken` para guardar ese token en el backend.
3. Cuando alguien crea una solicitud de mantenimiento, el backend notifica automáticamente a todos los usuarios con rol **TECNICO** que tengan token activo.
4. Al cerrar sesión, la app llama `unregisterPushTokens` para desactivar el token.

---

## Paso 1 — Instalar dependencias

```bash
npx expo install expo-notifications expo-device expo-constants
```

> `expo-device` se necesita para detectar si el código corre en un dispositivo físico real (los simuladores no reciben push).

---

## Paso 2 — Configurar `app.json` / `app.config.js`

Agrega el plugin de notificaciones y los colores/iconos de Android:

```json
{
  "expo": {
    "plugins": [
      [
        "expo-notifications",
        {
          "icon": "./assets/notification-icon.png",
          "color": "#ffffff",
          "sounds": []
        }
      ]
    ],
    "android": {
      "useNextNotificationsApi": true
    }
  }
}
```

> El `icon` debe ser una imagen **cuadrada de 96×96 px**, fondo transparente, solo para Android.  
> Si no tienes el ícono todavía, puedes omitir esa línea por ahora.

---

## Paso 3 — Permisos en iOS

En iOS se debe **pedir permiso explícito** al usuario antes de recibir notificaciones.  
Crea una función utilitaria (ej. `src/utils/notifications.ts`):

```typescript
import * as Notifications from 'expo-notifications';
import * as Device from 'expo-device';
import Constants from 'expo-constants';
import { Platform } from 'react-native';

// Comportamiento cuando llega una notificación con la app EN PRIMER PLANO
Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

export async function registerForPushNotificationsAsync(): Promise<string | null> {
  if (!Device.isDevice) {
    console.warn('Las push notifications solo funcionan en dispositivos físicos.');
    return null;
  }

  // Verificar / pedir permisos
  const { status: existingStatus } = await Notifications.getPermissionsAsync();
  let finalStatus = existingStatus;

  if (existingStatus !== 'granted') {
    const { status } = await Notifications.requestPermissionsAsync();
    finalStatus = status;
  }

  if (finalStatus !== 'granted') {
    console.warn('Permiso de notificaciones denegado.');
    return null;
  }

  // Canal de Android (obligatorio en Android 8+)
  if (Platform.OS === 'android') {
    await Notifications.setNotificationChannelAsync('default', {
      name: 'default',
      importance: Notifications.AndroidImportance.MAX,
      vibrationPattern: [0, 250, 250, 250],
      lightColor: '#FF231F7C',
    });
  }

  // Obtener el token de Expo
  const projectId =
    Constants?.expoConfig?.extra?.eas?.projectId ??
    Constants?.easConfig?.projectId;

  if (!projectId) {
    console.error('No se encontró el projectId de EAS. Revisa app.config.js');
    return null;
  }

  const tokenData = await Notifications.getExpoPushTokenAsync({ projectId });
  return tokenData.data; // Ej: "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]"
}
```

---

## Paso 4 — Registrar el token en el backend tras el login

Donde manejes el login (ej. tu store de Zustand, contexto de autenticación, o directamente en la pantalla de login), llama `registerPushToken` **después** de recibir el JWT:

```typescript
import { registerForPushNotificationsAsync } from '@/utils/notifications';
import { gql, useMutation } from '@apollo/client'; // o tu cliente GraphQL

const REGISTER_PUSH_TOKEN = gql`
  mutation RegisterPushToken($input: RegisterPushTokenInput!) {
    registerPushToken(input: $input)
  }
`;

// Dentro de tu función de login, después de guardar el JWT:
async function handleAfterLogin() {
  const [registerPushToken] = useMutation(REGISTER_PUSH_TOKEN);

  const expoPushToken = await registerForPushNotificationsAsync();

  if (expoPushToken) {
    const platform = Platform.OS === 'ios' ? 'ios' : 'android';
    try {
      await registerPushToken({
        variables: {
          input: {
            token: expoPushToken,
            platform,
          },
        },
      });
      console.log('Push token registrado correctamente.');
    } catch (err) {
      // No crítico: si falla, el usuario simplemente no recibirá notificaciones
      console.warn('No se pudo registrar el push token:', err);
    }
  }
}
```

> **Importante:** la mutation `registerPushToken` requiere el header `Authorization: Bearer <token>`.  
> Asegúrate de que tu cliente GraphQL ya envía ese header en todas las peticiones autenticadas.

---

## Paso 5 — Escuchar notificaciones en la app

En tu componente raíz (ej. `_layout.tsx` o `App.tsx`) registra los listeners:

```typescript
import { useEffect, useRef } from 'react';
import * as Notifications from 'expo-notifications';
import { useRouter } from 'expo-router'; // o tu navegación

export default function RootLayout() {
  const router = useRouter();
  const notificationListener = useRef<Notifications.EventSubscription>();
  const responseListener = useRef<Notifications.EventSubscription>();

  useEffect(() => {
    // Notificación recibida con la app EN PRIMER PLANO
    notificationListener.current = Notifications.addNotificationReceivedListener(notification => {
      console.log('Notificación recibida:', notification);
      // Aquí puedes mostrar un toast, badge, etc.
    });

    // Usuario TOCÓ la notificación (app en background o cerrada)
    responseListener.current = Notifications.addNotificationResponseReceivedListener(response => {
      const data = response.notification.request.content.data;

      if (data?.type === 'NEW_REQUEST' && data?.solicitudId) {
        // Navegar al detalle de la solicitud
        router.push(`/solicitudes/${data.solicitudId}`);
      }
    });

    return () => {
      notificationListener.current?.remove();
      responseListener.current?.remove();
    };
  }, []);

  // ... resto del layout
}
```

### Datos que envía el backend en cada notificación

```json
{
  "title": "Nueva solicitud de mantenimiento",
  "body": "<título de la solicitud>",
  "data": {
    "solicitudId": "<UUID>",
    "type": "NEW_REQUEST"
  }
}
```

---

## Paso 6 — Desregistrar el token al cerrar sesión

En tu función de logout:

```typescript
const UNREGISTER_PUSH_TOKENS = gql`
  mutation UnregisterPushTokens {
    unregisterPushTokens
  }
`;

async function handleLogout() {
  try {
    await unregisterPushTokens(); // mutation GraphQL
  } catch (err) {
    console.warn('No se pudo desregistrar el push token:', err);
  }
  // Luego limpia el JWT y navega a login
}
```

---

## Paso 7 — Configurar EAS (para builds de producción)

Las push notifications **no funcionan en Expo Go en producción**. Necesitas una build de desarrollo o producción con EAS.

### 7.1 Instalar EAS CLI

```bash
npm install -g eas-cli
eas login
```

### 7.2 Inicializar EAS en el proyecto

```bash
eas build:configure
```

Esto crea `eas.json`. Revisa que tenga al menos:

```json
{
  "build": {
    "development": {
      "developmentClient": true,
      "distribution": "internal"
    },
    "production": {}
  }
}
```

### 7.3 Agregar el `projectId` en `app.config.js`

```javascript
export default {
  expo: {
    extra: {
      eas: {
        projectId: "TU_PROJECT_ID_AQUI"
      }
    }
  }
};
```

> El `projectId` lo encuentras en [expo.dev](https://expo.dev) → tu proyecto → Settings.

### 7.4 Generar credenciales de push

```bash
# Android (FCM): EAS lo gestiona automáticamente
eas credentials

# iOS: necesitas una Apple Developer account
eas credentials --platform ios
```

---

## Paso 8 — Probar sin build completo (Expo Go + token manual)

Puedes probar el flujo de registro desde Expo Go:

1. Corre la app en Expo Go.
2. Llama `registerForPushNotificationsAsync()` → obtendrás un token del tipo `ExponentPushToken[...]`.
3. Registra ese token en el backend con la mutation `registerPushToken`.
4. Crea una solicitud de mantenimiento desde el backend o GraphiQL.
5. Verifica que llegue la notificación al dispositivo.

> En Expo Go las push funcionan mientras el proyecto esté en desarrollo bajo la cuenta de Expo correcta.

---

## Referencia rápida de las mutations GraphQL

### Registrar token

```graphql
mutation {
  registerPushToken(input: {
    token: "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]"
    platform: "android"  # o "ios"
  })
}
```

Header requerido:
```
Authorization: Bearer <JWT>
```

### Desregistrar tokens

```graphql
mutation {
  unregisterPushTokens
}
```

Header requerido:
```
Authorization: Bearer <JWT>
```

---

## Checklist de integración

- [ ] `expo-notifications`, `expo-device`, `expo-constants` instalados
- [ ] Plugin de notificaciones en `app.json`
- [ ] `projectId` de EAS configurado en `app.config.js`
- [ ] `registerForPushNotificationsAsync()` llamado tras el login exitoso
- [ ] Token enviado al backend con `registerPushToken` mutation
- [ ] Header `Authorization: Bearer <token>` presente en el cliente GraphQL
- [ ] Canal de Android creado (`setNotificationChannelAsync`)
- [ ] Listener de respuesta a notificación implementado (para navegación)
- [ ] `unregisterPushTokens` llamado en el logout
- [ ] Probado en dispositivo físico (no en simulador)

---

## Errores comunes

| Error | Causa | Solución |
|---|---|---|
| `Must be a physical device` | Corre en simulador | Usar dispositivo real o build EAS |
| `Permission denied` | Usuario negó el permiso | Mostrar diálogo explicativo antes de pedir permiso |
| `projectId not found` | Falta en `app.config.js` | Agregar `extra.eas.projectId` |
| `401 Unauthorized` en la mutation | JWT no se envía | Verificar header `Authorization` en el cliente GraphQL |
| Token se registra pero no llegan notificaciones | Rol del usuario no es `TECNICO` | Verificar en BD que el usuario tenga ese rol exacto |
| `DeviceNotRegistered` | Token expirado o app desinstalada | El backend lo desactiva automáticamente |
