# Mi Comercio — Android independiente

Mi Comercio es una aplicación de punto de venta **100% Android y offline-first**.

## Características

- Toda la información operativa queda guardada en el almacenamiento privado del teléfono.
- Productos, categorías, stock, clientes, deudas, ventas, caja, proveedores, compras, promociones, mesas, usuarios, reportes, movimientos y auditoría.
- Lector de códigos de barras con la cámara.
- Cobros y medios de pago.
- Crédito y cuenta corriente de clientes.
- Apertura, movimientos y cierre de caja.
- Mercado Pago como medio/importe de caja.
- Compras y recepción de mercadería.
- Proveedores y productos asociados.
- Promociones.
- Mesas y tickets abiertos.
- Reportes y Corte Z.
- Configuración de temas, sonidos y email.
- Envío de comprobantes/reportes mediante las aplicaciones de correo o mensajería instaladas en Android.
- No necesita FerrariPOS, QR de vinculación, Cloudflare, servidor ni computadora para funcionar.

## Privacidad de datos

La base operativa se guarda localmente dentro de la aplicación. No se requiere conexión a Internet para vender o administrar el negocio.

## Compilación

En Windows ejecutar (si compilás localmente):

`COMPILAR_ANDROID.bat`

El APK se genera en:

`app\build\outputs\apk\release\app-release.apk`

## Publicación en GitHub

Ejecutar:

`SUBIR_A_GITHUB.bat`

El script pide usuario y nombre del repositorio nuevo y publica **solamente este proyecto Android**.
