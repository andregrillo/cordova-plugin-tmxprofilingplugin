var exec = require('cordova/exec');

exports.init = function (orgID, fpServer, success, error) {
    exec(success, error, 'TMXProfilingPlugin', 'init', [orgID, fpServer]);
};

exports.doProfile = function (success, error) {
    exec(success, error, 'TMXProfilingPlugin', 'doProfile', []);
};
