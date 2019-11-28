# Changelog

## v1.2.7 - 28/11/2019
- Fixes Android 8.0.0 (API 26) bug, incompatibility between windowIsTranslucent and screenOrientation.

## v1.2.6 - 27/11/2019
- Updates target SDK to 28.

## v1.2.5 - 10/04/2019

### Changed
- Http to Https 

## v1.2.4 - 29/06/2018

### Changed
- EPOK search limit from 5 to 10 (iOS default).

## v1.2.3 - 01/02/2018

### Added
- JSON utils.

### Fixed
- Crash when choosing a place with no normalized address.

## v1.2.2 - 31/01/2018

### Changed
- Uncommented public method `loadAddressLatLongFromCaba`.

## v1.2.1 - 29/01/2018

### Changed
- Displayed version number in `AddressSearchActivity`.

## v1.2 - 26/01/2018

### Added
- Places search feature.

### Changed
- A minimum number of characters is now required to display search suggestions.

### Fixed
- Missing `CABA` field when selecting one of the last search suggestions.
- Duplicated values issue after resuming app.

## v1.1 - 20/10/2017

### Fixed
- Null pointer in `CallejeroView` due to missing options.
