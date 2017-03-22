var exec = require('cordova/exec');

module.exports = {
    registerFingerprint: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, "CorewiseFingerprintAuth", "register", []);
    },
    validateFingerprint: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, "CorewiseFingerprintAuth", "validate", []);
    }

};

