cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "id": "cordova-plugin-device.device",
        "file": "plugins/cordova-plugin-device/www/device.js",
        "pluginId": "cordova-plugin-device",
        "clobbers": [
            "device"
        ]
    },
    {
        "id": "com.plugin.imei.IMEIPlugin",
        "file": "plugins/com.plugin.imei/www/imei.js",
        "pluginId": "com.plugin.imei",
        "clobbers": [
            "window.plugins.imei"
        ]
    },
    {
        "id": "com.joandilee.imeiplugin.imeiplugin",
        "file": "plugins/com.joandilee.imeiplugin/www/imeiplugin.js",
        "pluginId": "com.joandilee.imeiplugin",
        "clobbers": [
            "imeiplugin"
        ]
    },
    {
        "id": "cordova-sms-plugin.Sms",
        "file": "plugins/cordova-sms-plugin/www/sms.js",
        "pluginId": "cordova-sms-plugin",
        "clobbers": [
            "window.sms"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.3.2",
    "cordova-plugin-device": "1.1.6",
    "com.plugin.imei": "0.0.1",
    "com.joandilee.imeiplugin": "0.1",
    "cordova-sms-plugin": "0.1.11"
};
// BOTTOM OF METADATA
});