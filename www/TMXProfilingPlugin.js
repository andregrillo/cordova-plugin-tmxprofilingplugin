var exec = require('cordova/exec');

exports.init = function (orgID, fpServer, registerForLocationServices, screenOffTimeout, disableLocSerOnBatteryLow, profileTimeout, disableNonfatalLogs, success, error) {
    exec(success, error, 'TMXProfilingPlugin', 'init', [orgID, fpServer, registerForLocationServices, screenOffTimeout, disableLocSerOnBatteryLow, profileTimeout, disableNonfatalLogs]);
};

exports.doProfile = function (sessionID, success, error) {
    exec(success, error, 'TMXProfilingPlugin', 'doProfile', [sessionID]);
};

exports.cancelProfile = function (success, error) {
    exec(success, error, 'TMXProfilingPlugin', 'cancelProfile', []);
};

exports.scanPackages = function (timeout, success, error) {
    exec(success, error, 'TMXProfilingPlugin', 'scanPackages', [timeout]);
};