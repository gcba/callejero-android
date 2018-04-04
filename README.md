# Callejero Android

Cliente Android del servicio de normalización de direcciones de USIG para CABA y AMBA.

## Instalación

Se debe exportar el módulo **callejerogcba** como AAR e importarlo en el proyecto de destino. Para importar el AAR en Android Studio:

```
File > New > New Module... > Import .JAR/.AAR Package
```

## Uso

El componente se puede utilizar con una vista llamada `CallejeroView` que se encarga de mostrar la dirección e iniciar la búsqueda u obtenerla mediante la ubicación del dispositivo. También se puede hacer la búsqueda programáticamente mediante la clase `CallejeroManager`.

### CallejeroView

```xml
<com.gcba.callejero.ui.CallejeroView
    android:id="@+id/callejero"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

Es necesario sobreescribir el método `onActivityResult` en la activity donde se use la instancia de `CallejeroView` (para poder recibir el resultado de búsqueda).

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callejeroView.onActivityResult(requestCode, resultCode, data);
}
```

#### Carga mediante GPS

Para habilitar un botón que cargue la dirección con la ubicación del dispositivo, llamar al método `enabledLocation()`.

```java
callejeroView = (CallejeroView) findViewById(R.id.callejero);
callejeroView.enabledLocation();
```

Para poder usar este método primero hay que pedir los permisos de ubicación.

```java
private void allowCallejeroLocation() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        return;
    }

    callejeroView.enabledLocation();
}
```

#### Callback

Para obtener la dirección seleccionada, implementar el método `onAddressSelection` de la interfaz `SelectionCallback`.

```java
@Override
public void onAddressSelection(StandardizedAddress address) {
    // Do something
}
```

Para registrar la implementación de la interfaz, usar el método `setSelectionCallback`.

```java
callejeroView.setSelectionCallback(new SelectionCallback() {
    @Override
    public void onAddressSelection(StandardizedAddress address) {
        // Do something
    }

    @Override
    public void onCancel() {
        // Do something
    }

    ...
});
```

El método `onCancel` se ejecuta cuando el usuario sale de la interfaz de búsqueda con el botón **Back** (sin haber escogido un resultado).

La interfaz `SelectionCallback` especifica dos métodos más, `onSelectedPin` y `onSelectionLabel`, para el caso que se hayan configurado funciones opcionales.

#### Opciones

Se puede configurar el componente mediante una instancia de `CallejeroOptions`.

```java
CallejeroOptions options = new CallejeroOptions();
```

##### Búsquedas en AMBA

Para ampliar la búsqueda a direcciones del AMBA, usar el método `setShowOnlyFromCaba`. Por defecto la búsqueda se limita a direcciones de CABA.

```java
options.setShowOnlyFromCaba(false);
callejeroView.setOptions(options);
```

##### No forzar normalización

Para permitir que el usuario pueda seleccionar una dirección no normalizada, llamar al método `setShowLabel`. Por defecto se fuerza la normalización de direcciones.

```java
options.setShowLabel(true);
callejeroView.setOptions(options);
```

Si el usuario tapea la dirección no normalizada, no se la obtiene a través del callback `onAddressSelection` sino de `onSelectionLabel` (como un string, en vez de `StandardizedAddress`).

##### Incluir lugares

Para mostrar lugares entre los resultados de búsqueda, usar el método `setShowPlaces`.

```java
options.setShowPlaces(true);
callejeroView.setOptions(options);
```

##### Mostrar pin

Es posible mostrar al final de los resultados de búsqueda un botón con la imagen de un [pin](https://www.google.com.ar/search?q=map+pin) y el texto **Fijar la ubicación en el mapa**, con el método `setShowPin`. Por defecto no se muestra el pin.

```java
options.setShowPin(true);
callejeroView.setOptions(options);
```

El callback del botón se puede configurar implementando el método `onSelectedPin` de la interfaz `SelectionCallback`.

```java
@Override
public void onSelectedPin() {
    // Do something
}
```

### CallejeroManager

#### Búsqueda a partir del nombre de una calle

Para buscar una dirección a partir del nombre (o parte del nombre) de una calle se cuenta con el método `normalizeQuery`. Este método recibe 3 parámetros: el texto de búsqueda, un booleano para buscar sólo en CABA y un `SearchCallback`.

```java
CallejeroManager.getInstance().normalizeQuery(query, onlyFromCABA, new SearchCallback() {
    @Override
    public void onSuccess(NormalizeResponse normalize) {

    }

    @Override
    public void onError(Throwable error) {

    }
});
```

#### Búsqueda a partir de un par de coordenadas

##### Sólo CABA

Es posible buscar una dirección a partir de latitud y longitud, con el método `loadAddressLatLongFromCABA`. Este método recibe dos parámetros: un objeto `AddressLocation` con las coordenadas y un `LocationCallback`.

```java
AddressLocation location = new AddressLocation();

location.setX(longitude);
location.setY(latitude);

CallejeroManager.getInstance().loadAddressLatLongFromCABA(location, new LocationCallBack() {
    @Override
    public void onSuccess(StandardizedAddress address) {

    }

    @Override
    public void onError(Throwable error) {

    }
});
```

##### Esquina más cercana

Para obtener la dirección de la esquina más cercana a una latitud y longitud se emplea el método `loadAddressLatLong`. Este método recibe dos parámetros: un objeto `AddressLocation` con las coordenadas y un `LocationCallback`.

```java
AddressLocation location = new AddressLocation();

location.setX(longitude);
location.setY(latitude);

CallejeroManager.getInstance().loadAddressLatLong(location, new LocationCallBack() {
    @Override
    public void onSuccess(StandardizedAddress address) {

    }

    @Override
    public void onError(Throwable error) {

    }
});
```

#### Iniciar programáticamente la UI de búsqueda

Para mostrar la interfaz de búsqueda de forma programática (sin usar el `CallejeroView`) se puede usar el método `startSearch`. Este método recibe 4 parámetros: una instancia de la activity que inicia la búsqueda, un objeto `CallejeroOptions` con opciones, un entero para usar como `requestCode` y un `SelectionCallback`.

```java
CallejeroOptions options = new CallejeroOptions();

CallejeroManager.getInstance().startSearch(activity, options, requestCode, new SelectionCallback() {
    @Override
    public void onAddressSelection(StandardizedAddress address) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onSelectedPin() {

    }

    @Override
    public void onSelectionLabel(String direction) {

    }
});
```

Si se usa este método, es necesario sobreescribir el método `onActivityResult` de la activity desde donde se lo esté llamando (para poder recibir el resultado de búsqueda).

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    CallejeroManager.getInstance().onActivityResult(requestCode, resultCode, data);
}
```