# TMXProfilingPlugin for Cordova/OutSystems

This plugin provides easy integration of LexisNexis Risk Solutions' ThreatMetrix Mobile SDK into a Cordova/OutSystems application. It allows you to perform device profiling and package scanning, which are crucial for detecting and preventing fraud in mobile applications.

## Features

- Initialize profiling with organization-specific configurations.
- Perform device profiling with optional session ID.
- Cancel ongoing profiling operations.
- Scan installed packages with an optional timeout.

## Installation

To install the plugin into your Cordova project, run the following command:

cordova plugin add https://github.com/andregrillo/TMXProfilingPlugin.git

## Usage

The plugin exposes the following methods for Cordova/OutSystems applications:

### init

Initializes the ThreatMetrix profiling with the provided configuration.

Parameters:

- orgID: Your organization's ID.
- fpServer: The fingerprint server URL.
- registerForLocationServices: Boolean to register for location services.
- screenOffTimeout: Integer for screen off timeout.
- disableLocSerOnBatteryLow: Boolean to disable location services when the battery is low.
- profileTimeout: Integer for profile timeout.
- disableNonfatalLogs: Boolean to disable nonfatal logs.

Example:

```javascript
TMXProfilingPlugin.init('orgID', 'fpServer', true, 180, false, 30, false, successCallback, errorCallback);
```

### doProfile

Performs device profiling. Optionally, a session ID can be provided.

Parameters:

- sessionID: (Optional) The session ID for the profiling.

Example:

```javascript
TMXProfilingPlugin.doProfile('sessionID', successCallback, errorCallback);
```

### cancelProfile

Cancels an ongoing profiling operation.

Example:

```javascript
TMXProfilingPlugin.cancelProfile(successCallback, errorCallback);
```

## iOS & Android Implementation

The plugin for iOS and Android is implemented in the `TMXProfilingPlugin` class. It handles the initialization, profiling, and scanning of packages (Android only) as directed by the Cordova/OutSystems interface. The plugin ensures that correct values are passed and provides appropriate success and error callbacks.

## Usage

Here's how you can use the plugin in your application:

1. Initialize the ThreatMetrix profiling with the necessary configuration.
2. Call `doProfile` to start the profiling process.
3. Use `cancelProfile` if you need to stop the profiling process.

Remember to handle the success and error callbacks appropriately to ensure a smooth user experience.

## Contributing

We welcome contributions to this plugin. Please consider the following guidelines:

- Fork the repository and create your branch from `master`.
- Write clear, concise commit messages.
- Ensure that your code follows the existing coding standards.
- Update the README.md with details of changes to the interface.
- Merge the latest changes from `master` and resolve any conflicts before submitting a pull request.

## License

This plugin is released under the [MIT License](LICENSE).

## Author

This plugin was developed by André Grillo - OutSystems

---

